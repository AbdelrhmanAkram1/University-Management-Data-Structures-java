//library for GUI,animations,buttons and colors
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
public class UniversitySystemGUI extends JFrame {
    private UniversitySystem universitySystem;
    private final ByteArrayOutputStream consoleOutput;
    private final PrintStream originalOut;
    private final Set<Integer> existingStudents = new HashSet<>();
    private final Set<Integer> existingCourses = new HashSet<>();
//colors 
    private final Color HEADER_BACKGROUND = Color.WHITE;
    private final Color UNDO_REDO_BACKGROUND = new Color(0, 120, 215);
    private final Color PRIMARY_DARK = new Color(12, 35, 64);
    private final Color PRIMARY_LIGHT = new Color(20, 108, 148);
    private final Color SECONDARY = new Color(240, 147, 43);
    private final Color ACCENT = new Color(188, 57, 37);
    private final Color BACKGROUND = new Color(245, 247, 250);
    private final Color CARD = new Color(255, 255, 255);
    private final Color TEXT = new Color(60, 60, 60);
    private final Color LIGHT_TEXT = new Color(140, 140, 140);
//font
    private final Font TITLE_FONT = new Font("Segoe UI Semibold", Font.BOLD, 28);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
//fields
    private JTextArea outputArea;
    private JTextField studentIdField;
    private JTextField courseIdField;
    private JPanel currentInputPanel;

    public UniversitySystemGUI() {
        universitySystem = new UniversitySystem();
        originalOut = System.out;
        consoleOutput = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(consoleOutput, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        initializeUI();
    }
// intialize ui
    private void initializeUI() {
        setTitle("University Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
//intialize window of the program
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        JPanel mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                g2d.setColor(BACKGROUND);
                g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
            }
        };
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainContainer.setOpaque(false);
        mainContainer.add(createHeaderPanel(), BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(createOutputPanel(), BorderLayout.CENTER);
        currentInputPanel = new JPanel();
        currentInputPanel.setOpaque(false);
        contentPanel.add(currentInputPanel, BorderLayout.SOUTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        add(mainContainer);
        showStudentPanel();
        Timer timer = new Timer(100, e -> updateOutput());
        timer.start();
    }
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BACKGROUND);
        header.setPreferredSize(new Dimension(getWidth(), 80));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        JLabel title = new JLabel("UNIVERSITY MANAGEMENT SYSTEM");
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_DARK);
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlPanel.setOpaque(false);
        JButton minimizeBtn = new JButton("—");
        styleControlButton(minimizeBtn);
        minimizeBtn.addActionListener(e -> setState(Frame.ICONIFIED));
        JButton closeBtn = new JButton("×");
        styleControlButton(closeBtn);
        closeBtn.addActionListener(e -> System.exit(0));
        controlPanel.add(minimizeBtn);
        controlPanel.add(closeBtn);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        JButton undoBtn = createUndoRedoButton("UNDO");
        undoBtn.addActionListener(e -> {
            universitySystem.undo();
            updateOutput();
        });
        JButton redoBtn = createUndoRedoButton("REDO");
        redoBtn.addActionListener(e -> {
            universitySystem.redo();
            updateOutput();
        });
        actionPanel.add(undoBtn);
        actionPanel.add(redoBtn);
        header.add(title, BorderLayout.WEST);
        header.add(actionPanel, BorderLayout.CENTER);
        header.add(controlPanel, BorderLayout.EAST);
        return header;
    }
//style button
    private void styleControlButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(TEXT);
        button.setBackground(new Color(0, 0, 0, 0));
        button.setBorder(new EmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setForeground(ACCENT);
                button.setBackground(new Color(0, 0, 0, 10));
            }
            public void mouseExited(MouseEvent e) {
                button.setForeground(TEXT);
                button.setBackground(new Color(0, 0, 0, 0));
            }
        });
    }
    private JButton createUndoRedoButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(UNDO_REDO_BACKGROUND.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(UNDO_REDO_BACKGROUND.brighter());
                } else {
                    g2.setColor(UNDO_REDO_BACKGROUND);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
            @Override
            protected void paintBorder(Graphics g) {
                // No border
            }
        };
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(8, 20, 8, 20));
        button.setFocusPainted(false);
        return button;
    }
//create output panel
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(220, 220, 220, 150)),
                new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setBackground(CARD);
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        JLabel icon = new JLabel("");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        JLabel title = new JLabel("SYSTEM OUTPUT");
        title.setFont(SUBTITLE_FONT);
        title.setForeground(PRIMARY_DARK);
        titlePanel.add(icon);
        titlePanel.add(title);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setForeground(TEXT);
        outputArea.setBackground(CARD);
        outputArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD);
        JButton clearButton = new JButton("Clear Output");
        clearButton.addActionListener(e -> outputArea.setText(""));
        styleControlButton(clearButton);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(clearButton);
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }
    private void showStudentPanel() {
        currentInputPanel.removeAll();
        currentInputPanel.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(220, 220, 220, 150)),
                new EmptyBorder(20, 20, 20, 20)));
        panel.setBackground(CARD);
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("STUDENT OPERATIONS");
        title.setFont(SUBTITLE_FONT);
        title.setForeground(PRIMARY_DARK);
        titlePanel.add(title);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titlePanel);
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setOpaque(false);
        studentIdField = createInputField("Enter Student ID");
        JLabel studentLabel = createInputLabel("STUDENT ID:");
        JPanel studentInputPanel = new JPanel(new BorderLayout(10, 10));
        studentInputPanel.setOpaque(false);
        studentInputPanel.add(studentLabel, BorderLayout.WEST);
        studentInputPanel.add(studentIdField, BorderLayout.CENTER);
        inputPanel.add(studentInputPanel, BorderLayout.NORTH);
        panel.add(inputPanel);
        panel.add(Box.createVerticalStrut(20));
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 2, 15, 15)); // Changed to 3 rows
        buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(createButton("ADD STUDENT", SECONDARY, e -> handleStudentAction(true)));
        buttonsPanel.add(createButton("REMOVE STUDENT", ACCENT, e -> handleStudentAction(false)));
        buttonsPanel.add(createButton("LIST COURSES", SECONDARY, e -> listCourses()));
        buttonsPanel.add(createButton("CHECK STATUS", SECONDARY, e -> checkStudentStatus()));
        buttonsPanel.add(createButton("LAST ADDED STUDENT", SECONDARY, e -> showLastStudentAdded()));
        buttonsPanel.add(new JLabel()); // Empty label for layout
        panel.add(buttonsPanel);
        currentInputPanel.add(panel, BorderLayout.CENTER);
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tabPanel.setOpaque(false);
        JButton studentTab = createTabButton("STUDENTS", true);
        JButton courseTab = createTabButton("COURSES", false);
        JButton enrollTab = createTabButton("ENROLLMENT", false);
        studentTab.addActionListener(e -> showStudentPanel());
        courseTab.addActionListener(e -> showCoursePanel());
        enrollTab.addActionListener(e -> showEnrollmentPanel());
        tabPanel.add(studentTab);
        tabPanel.add(courseTab);
        tabPanel.add(enrollTab);
        currentInputPanel.add(tabPanel, BorderLayout.NORTH);
        currentInputPanel.revalidate();
        currentInputPanel.repaint();
    }
//course panel
    private void showCoursePanel() {
        currentInputPanel.removeAll();
        currentInputPanel.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(220, 220, 220, 150)),
                new EmptyBorder(20, 20, 20, 20)));
        panel.setBackground(CARD);
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("COURSE OPERATIONS");
        title.setFont(SUBTITLE_FONT);
        title.setForeground(PRIMARY_DARK);
        titlePanel.add(title);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titlePanel);
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setOpaque(false);
        courseIdField = createInputField("Enter Course ID");
        JLabel courseLabel = createInputLabel("COURSE ID:");
        JPanel courseInputPanel = new JPanel(new BorderLayout(10, 10));
        courseInputPanel.setOpaque(false);
        courseInputPanel.add(courseLabel, BorderLayout.WEST);
        courseInputPanel.add(courseIdField, BorderLayout.CENTER);
        inputPanel.add(courseInputPanel, BorderLayout.NORTH);
        panel.add(inputPanel);
        panel.add(Box.createVerticalStrut(20));
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 2, 15, 15)); // Changed to 3 rows
        buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(createButton("ADD COURSE", SECONDARY, e -> handleCourseAction(true)));
        buttonsPanel.add(createButton("REMOVE COURSE", ACCENT, e -> handleCourseAction(false)));
        buttonsPanel.add(createButton("LIST STUDENTS", SECONDARY, e -> listStudents()));
        buttonsPanel.add(createButton("CHECK CAPACITY", SECONDARY, e -> checkCourseCapacity()));
        buttonsPanel.add(createButton("LAST ADDED COURSE", SECONDARY, e -> showLastCourseAdded()));
        buttonsPanel.add(new JLabel()); // Empty label for layout
        panel.add(buttonsPanel);
        currentInputPanel.add(panel, BorderLayout.CENTER);
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tabPanel.setOpaque(false);
        JButton studentTab = createTabButton("STUDENTS", false);
        JButton courseTab = createTabButton("COURSES", true);
        JButton enrollTab = createTabButton("ENROLLMENT", false);
        studentTab.addActionListener(e -> showStudentPanel());
        courseTab.addActionListener(e -> showCoursePanel());
        enrollTab.addActionListener(e -> showEnrollmentPanel());
        tabPanel.add(studentTab);
        tabPanel.add(courseTab);
        tabPanel.add(enrollTab);
        currentInputPanel.add(tabPanel, BorderLayout.NORTH);
        currentInputPanel.revalidate();
        currentInputPanel.repaint();
    }
    private void showEnrollmentPanel() {
        currentInputPanel.removeAll();
        currentInputPanel.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(220, 220, 220, 150)),
                new EmptyBorder(20, 20, 20, 20)));
        panel.setBackground(CARD);
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("ENROLLMENT OPERATIONS");
        title.setFont(SUBTITLE_FONT);
        title.setForeground(PRIMARY_DARK);
        titlePanel.add(title);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titlePanel);
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        inputPanel.setOpaque(false);
        studentIdField = createInputField("Enter Student ID");
        JLabel studentLabel = createInputLabel("STUDENT ID:");
        JPanel studentInputPanel = new JPanel(new BorderLayout(10, 10));
        studentInputPanel.setOpaque(false);
        studentInputPanel.add(studentLabel, BorderLayout.WEST);
        studentInputPanel.add(studentIdField, BorderLayout.CENTER);
        courseIdField = createInputField("Enter Course ID");
        JLabel courseLabel = createInputLabel("COURSE ID:");
        JPanel courseInputPanel = new JPanel(new BorderLayout(10, 10));
        courseInputPanel.setOpaque(false);
        courseInputPanel.add(courseLabel, BorderLayout.WEST);
        courseInputPanel.add(courseIdField, BorderLayout.CENTER);
        inputPanel.add(studentInputPanel);
        inputPanel.add(courseInputPanel);
        panel.add(inputPanel);
        panel.add(Box.createVerticalStrut(20));
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(createButton("ENROLL STUDENT", SECONDARY, e -> handleEnrollment(true)));
        buttonsPanel.add(createButton("REMOVE ENROLLMENT", ACCENT, e -> handleEnrollment(false)));
        buttonsPanel.add(createButton("SORT COURSES", SECONDARY, e -> sortCourses()));
        buttonsPanel.add(createButton("SORT STUDENTS", SECONDARY, e -> sortStudents()));
        panel.add(buttonsPanel);
        currentInputPanel.add(panel, BorderLayout.CENTER);
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tabPanel.setOpaque(false);
        JButton studentTab = createTabButton("STUDENTS", false);
        JButton courseTab = createTabButton("COURSES", false);
        JButton enrollTab = createTabButton("ENROLLMENT", true);
        studentTab.addActionListener(e -> showStudentPanel());
        courseTab.addActionListener(e -> showCoursePanel());
        enrollTab.addActionListener(e -> showEnrollmentPanel());
        tabPanel.add(studentTab);
        tabPanel.add(courseTab);
        tabPanel.add(enrollTab);
        currentInputPanel.add(tabPanel, BorderLayout.NORTH);
        currentInputPanel.revalidate();
        currentInputPanel.repaint();
    }
//show last student added
    private void showLastStudentAdded() {
        int lastStudentId = universitySystem.getLastStudentAdded();
        if (lastStudentId != -1) {
            outputArea.append("Last student added: " + lastStudentId + "\n");
        } else {
            outputArea.append("No students have been added yet\n");
        }
        updateOutput();
    }
//show last course added
    private void showLastCourseAdded() {
        int lastCourseId = universitySystem.getLastCourseAdded();
        if (lastCourseId != -1) {
            outputArea.append("Last course added: " + lastCourseId + "\n");
        } else {
            outputArea.append("No courses have been added yet\n");
        }
        updateOutput();
    }
//create tab button
    private JButton createTabButton(String text, boolean active) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(active ? PRIMARY_DARK : LIGHT_TEXT);
        button.setBackground(active ? new Color(230, 230, 230) : new Color(0, 0, 0, 0));
        button.setContentAreaFilled(active);
        button.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, active ? 3 : 0, 0, PRIMARY_LIGHT),
                new EmptyBorder(8, 20, 8, 20)
        ));
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active) {
                    button.setBackground(new Color(0, 0, 0, 10));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) {
                    button.setBackground(new Color(0, 0, 0, 0));
                }
            }
        });
        return button;
    }
    private JLabel createInputLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(INPUT_FONT.deriveFont(Font.BOLD));
        label.setForeground(LIGHT_TEXT);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }
    private JTextField createInputField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(180, 180, 180));
                    g2.setFont(INPUT_FONT.deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 15, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2);
                }
            }
        };
        field.setFont(INPUT_FONT);
        field.setForeground(TEXT);
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, PRIMARY_LIGHT),
                new EmptyBorder(10, 15, 10, 15)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
        return field;
    }
    private JButton createButton(String text, Color bgColor, ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
            @Override
            protected void paintBorder(Graphics g) {
                // No border
            }
        };
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(12, 15, 12, 15));
        button.setFocusPainted(false);
        button.addActionListener(action);
        return button;
    }
//Handle functions
    private void handleStudentAction(boolean isAdd) {
        try {
            int id = Integer.parseInt(studentIdField.getText());
            if (isAdd) {
                universitySystem.addStudent(id);
                existingStudents.add(id);
            } else {
                universitySystem.removeStudent(id);
                existingStudents.remove(id);
            }
            updateOutput();
        } catch (NumberFormatException ex) {
            outputArea.append("Error: Invalid student ID.\n");
        }
    }
    private void handleCourseAction(boolean isAdd) {
        try {
            int id = Integer.parseInt(courseIdField.getText());
            if (isAdd) {
                universitySystem.addCourse(id);
                existingCourses.add(id);
            } else {
                universitySystem.removeCourse(id);
                existingCourses.remove(id);
            }
            updateOutput();
        } catch (NumberFormatException ex) {
            outputArea.append("Error: Invalid course ID.\n");
        }
    }
    private void handleEnrollment(boolean isEnroll) {
        try {
            int studentId = Integer.parseInt(studentIdField.getText());
            int courseId = Integer.parseInt(courseIdField.getText());
            if (isEnroll) {
                universitySystem.enrollStudent(studentId, courseId);
            } else {
                universitySystem.removeEnrollment(studentId, courseId);
            }
            updateOutput();
        } catch (NumberFormatException ex) {
            outputArea.append("Error: Invalid ID format.\n");
        }
    }
    private void listCourses() {
        try {
            int studentId = Integer.parseInt(studentIdField.getText());
            universitySystem.listCoursesByStudent(studentId);
            updateOutput();
        } catch (NumberFormatException ex) {
            outputArea.append("Error: Invalid student ID.\n");
        }
    }
    private void listStudents() {
        try {
            int courseId = Integer.parseInt(courseIdField.getText());
            universitySystem.listStudentsByCourse(courseId);
            updateOutput();
        } catch (NumberFormatException ex) {
            outputArea.append("Error: Invalid course ID.\n");
        }
    }
    private void checkStudentStatus() {
        try {
            int studentId = Integer.parseInt(studentIdField.getText());
            boolean isNormal = universitySystem.isnormalstudent(studentId);
            outputArea.append("Student " + studentId + " status: " + (isNormal ? "Normal" : "Not Normal") + ".\n");
            updateOutput();
        } catch (NumberFormatException ex) {
            outputArea.append("Error: Invalid student ID.\n");
        }
    }
    private void checkCourseCapacity() {
        try {
            int courseId = Integer.parseInt(courseIdField.getText());
            boolean isFull = universitySystem.isfullCourse(courseId);
            outputArea.append("Course " + courseId + " capacity: " + (isFull ? "Full" : "Available") + ".\n");
            updateOutput();
        } catch (NumberFormatException ex) {
            outputArea.append("Error: Invalid course ID.\n");
        }
    }
    private void sortCourses() {
        try {
            int studentId = Integer.parseInt(studentIdField.getText());
            universitySystem.sortCoursesByID(studentId);
            updateOutput();
        } catch (NumberFormatException ex) {
            outputArea.append("Error: Invalid student ID.\n");
        }
    }
    private void sortStudents() {
        try {
            int courseId = Integer.parseInt(courseIdField.getText());
            universitySystem.sortStudentsByID(courseId);
            updateOutput();
        } catch (NumberFormatException ex) {
            outputArea.append("Error: Invalid course ID.\n");
        }
    }
    private void updateOutput() {
        String output = consoleOutput.toString();
        if (!output.isEmpty()) {
            outputArea.append(output);
            consoleOutput.reset();
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        }
    }
    @Override
    public void dispose() {
        System.setOut(originalOut);
        super.dispose();
    }
    //run of GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            UniversitySystemGUI gui = new UniversitySystemGUI();
            gui.setVisible(true);
        });
    }
}