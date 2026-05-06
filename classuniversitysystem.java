import java.util.Stack;
class UniversitySystem {
    private Student studentsHead;
    private Course coursesHead;
    private Stack<Action> undoStack;
    private Stack<Action> redoStack;
    private Student lastStudentAdded;
    private Course lastCourseAdded;

    public UniversitySystem() {
        studentsHead = null;
        coursesHead = null;
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        lastStudentAdded = null;
        lastCourseAdded = null;
    }

    public void addStudent(int id) {
        if (findStudent(id) != null) {
            System.out.println("Student " + id + " already exists");
            return;
        }
        Student newStudent = new Student(id);
        newStudent.setNext(studentsHead);
        studentsHead = newStudent;
        lastStudentAdded = newStudent;
        undoStack.push(new Action("addStudent", id, -1));
        redoStack.clear();
        System.out.println("Added student " + id);
    }

    public void removeStudent(int id) {
        Student student = findStudent(id);
        if (student == null) {
            System.out.println("Student " + id + " not found");
            return;
        }
        int[] enrolledCourses = getStudentCourseIds(student);
        CourseNode courseNode = student.getRegisteredCourses();
        while (courseNode != null) {
            removeEnrollmentInternal(id, courseNode.getCourse().getId());
            courseNode = courseNode.getNext();
        }
        Student prev = null;
        Student current = studentsHead;
        while (current != null && current.getId() != id) {
            prev = current;
            current = current.getNext();
        }
        if (prev == null) {
            studentsHead = current.getNext();
        } else {
            prev.setNext(current.getNext());
        }
        if (lastStudentAdded != null && lastStudentAdded.getId() == id) {
            lastStudentAdded = null;
        }
        undoStack.push(new Action("removeStudent", id, enrolledCourses));
        redoStack.clear();
        System.out.println("Removed student " + id);
    }

    public void addCourse(int id) {
        if (findCourse(id) != null) {
            System.out.println("Course " + id + " already exists");
            return;
        }
        Course newCourse = new Course(id);
        newCourse.setNext(coursesHead);
        coursesHead = newCourse;
        lastCourseAdded = newCourse;
        undoStack.push(new Action("addCourse", id, -1));
        redoStack.clear();
        System.out.println("Added course " + id);
    }

    public void removeCourse(int id) {
        Course course = findCourse(id);
        if (course == null) {
            System.out.println("Course " + id + " not found");
            return;
        }
        int[] enrolledStudents = getCourseStudentIds(course);
        StudentNode studentNode = course.getEnrolledStudents();
        while (studentNode != null) {
            removeEnrollmentInternal(studentNode.getStudent().getId(), id);
            studentNode = studentNode.getNext();
        }
        Course prev = null;
        Course current = coursesHead;
        while (current != null && current.getId() != id) {
            prev = current;
            current = current.getNext();
        }
        if (prev == null) {
            coursesHead = current.getNext();
        } else {
            prev.setNext(current.getNext());
        }
        if (lastCourseAdded != null && lastCourseAdded.getId() == id) {
            lastCourseAdded = null;
        }
        undoStack.push(new Action("removeCourse", id, enrolledStudents));
        redoStack.clear();
        System.out.println("Removed course " + id);
    }

    public void enrollStudent(int studentID, int courseID) {
        if (enrollWithConstraints(studentID, courseID)) {
            undoStack.push(new Action("enroll", studentID, courseID));
            redoStack.clear();
            System.out.println("Enrolled student " + studentID + " in course " + courseID);
        }
    }

    public void removeEnrollment(int studentID, int courseID) {
        if (removeWithConstraints(studentID, courseID)) {
            undoStack.push(new Action("remove", studentID, courseID));
            redoStack.clear();
            System.out.println("Removed student " + studentID + " from course " + courseID);
        }
    }

    public void undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo");
            return;
        }
        Action action = undoStack.pop();
        boolean success = false;
        switch (action.getType()) {
            case "enroll":
                success = removeEnrollmentInternal(action.getStudentID(), action.getCourseID());
                if (success) {
                    redoStack.push(new Action("enroll", action.getStudentID(), action.getCourseID()));
                    System.out.println("Undo: Removed student " + action.getStudentID() + " from course " + action.getCourseID());
                }
                break;
            case "remove":
                success = enrollWithoutConstraints(action.getStudentID(), action.getCourseID());
                if (success) {
                    redoStack.push(new Action("remove", action.getStudentID(), action.getCourseID()));
                    System.out.println("Undo: Enrolled student " + action.getStudentID() + " in course " + action.getCourseID());
                }
                break;
            case "addStudent":
                removeStudentInternal(action.getStudentID());
                redoStack.push(new Action("addStudent", action.getStudentID(), action.getAdditionalIds()));
                System.out.println("Undo: Removed student " + action.getStudentID());
                success = true;
                break;
            case "removeStudent":
                addStudentInternal(action.getStudentID());
                if (action.getAdditionalIds() != null) {
                    for (int courseId : action.getAdditionalIds()) {
                        enrollWithoutConstraints(action.getStudentID(), courseId);
                    }
                }
                redoStack.push(new Action("removeStudent", action.getStudentID(), action.getAdditionalIds()));
                System.out.println("Undo: Added student " + action.getStudentID());
                success = true;
                break;
            case "addCourse":
                removeCourseInternal(action.getStudentID());
                redoStack.push(new Action("addCourse", action.getStudentID(), action.getAdditionalIds()));
                System.out.println("Undo: Removed course " + action.getStudentID());
                success = true;
                break;
            case "removeCourse":
                addCourseInternal(action.getStudentID());
                if (action.getAdditionalIds() != null) {
                    for (int studentId : action.getAdditionalIds()) {
                        enrollWithoutConstraints(studentId, action.getStudentID());
                    }
                }
                redoStack.push(new Action("removeCourse", action.getStudentID(), action.getAdditionalIds()));
                System.out.println("Undo: Added course " + action.getStudentID());
                success = true;
                break;
        }
        if (!success) {
            undoStack.push(action);
            System.out.println("Undo operation failed");
        }
    }

    public void redo() {
        if (redoStack.isEmpty()) {
            System.out.println("Nothing to redo");
            return;
        }
        Action action = redoStack.pop();
        boolean success = false;
        switch (action.getType()) {
            case "enroll":
                success = enrollWithoutConstraints(action.getStudentID(), action.getCourseID());
                if (success) {
                    undoStack.push(new Action("enroll", action.getStudentID(), action.getCourseID()));
                    System.out.println("Redo: Enrolled student " + action.getStudentID() + " in course " + action.getCourseID());
                }
                break;
            case "remove":
                success = removeEnrollmentInternal(action.getStudentID(), action.getCourseID());
                if (success) {
                    undoStack.push(new Action("remove", action.getStudentID(), action.getCourseID()));
                    System.out.println("Redo: Removed student " + action.getStudentID() + " from course " + action.getCourseID());
                }
                break;
            case "addStudent":
                addStudentInternal(action.getStudentID());
                if (action.getAdditionalIds() != null) {
                    for (int courseId : action.getAdditionalIds()) {
                        enrollWithoutConstraints(action.getStudentID(), courseId);
                    }
                }
                undoStack.push(new Action("addStudent", action.getStudentID(), action.getAdditionalIds()));
                System.out.println("Redo: Added student " + action.getStudentID());
                success = true;
                break;
            case "removeStudent":
                removeStudentInternal(action.getStudentID());
                undoStack.push(new Action("removeStudent", action.getStudentID(), action.getAdditionalIds()));
                System.out.println("Redo: Removed student " + action.getStudentID());
                success = true;
                break;
            case "addCourse":
                addCourseInternal(action.getStudentID());
                if (action.getAdditionalIds() != null) {
                    for (int studentId : action.getAdditionalIds()) {
                        enrollWithoutConstraints(studentId, action.getStudentID());
                    }
                }
                undoStack.push(new Action("addCourse", action.getStudentID(), action.getAdditionalIds()));
                System.out.println("Redo: Added course " + action.getStudentID());
                success = true;
                break;
            case "removeCourse":
                removeCourseInternal(action.getStudentID());
                undoStack.push(new Action("removeCourse", action.getStudentID(), action.getAdditionalIds()));
                System.out.println("Redo: Removed course " + action.getStudentID());
                success = true;
                break;
        }
        if (!success) {
            redoStack.push(action);
            System.out.println("Redo operation failed");
        }
    }

    private int[] getStudentCourseIds(Student student) {
        CourseNode current = student.getRegisteredCourses();
        int count = 0;
        CourseNode temp = current;
        while (temp != null) {
            count++;
            temp = temp.getNext();
        }
        int[] courseIds = new int[count];
        int index = 0;
        while (current != null) {
            courseIds[index++] = current.getCourse().getId();
            current = current.getNext();
        }
        return courseIds;
    }

    private int[] getCourseStudentIds(Course course) {
        StudentNode current = course.getEnrolledStudents();
        int count = 0;
        StudentNode temp = current;
        while (temp != null) {
            count++;
            temp = temp.getNext();
        }
        int[] studentIds = new int[count];
        int index = 0;
        while (current != null) {
            studentIds[index++] = current.getStudent().getId();
            current = current.getNext();
        }
        return studentIds;
    }

    private Student findStudent(int id) {
        Student current = studentsHead;
        while (current != null) {
            if (current.getId() == id) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    private Course findCourse(int id) {
        Course current = coursesHead;
        while (current != null) {
            if (current.getId() == id) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    private boolean isEnrolled(Student student, Course course) {
        CourseNode current = student.getRegisteredCourses();
        while (current != null) {
            if (current.getCourse().getId() == course.getId()) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    private boolean enrollWithConstraints(int studentID, int courseID) {
        Student student = findStudent(studentID);
        Course course = findCourse(courseID);
        
        if (student == null && course == null) {
            System.out.println("Both student " + studentID + " and course " + courseID + " not found");
            return false;
        }
        if (student == null) {
            System.out.println("Student " + studentID + " not found");
            return false;
        }
        if (course == null) {
            System.out.println("Course " + courseID + " not found");
            return false;
        }
        if (isEnrolled(student, course)) {
            System.out.println("Student " + studentID + " is already enrolled in course " + courseID);
            return false;
        }
        if (student.countCourses() >= 7) {
            System.out.println("Cannot enroll student " + studentID + " - maximum courses reached (7)");
            return false;
        }
        if (course.countStudents() >= 30) {
            System.out.println("Cannot enroll student " + studentID + " - course " + courseID + " is full (30 students)");
            return false;
        }
        
        student.addCourse(course);
        course.addStudent(student);
        return true;
    }

    private boolean enrollWithoutConstraints(int studentID, int courseID) {
        Student student = findStudent(studentID);
        Course course = findCourse(courseID);
        if (student == null) {
            System.out.println("Student " + studentID + " not found");
            return false;
        }
        if (course == null) {
            System.out.println("Course " + courseID + " not found");
            return false;
        }
        if (isEnrolled(student, course)) {
            System.out.println("Student " + studentID + " is already enrolled in course " + courseID);
            return false;
        }
        student.addCourse(course);
        course.addStudent(student);
        return true;
    }

    private boolean removeWithConstraints(int studentID, int courseID) {
        Student student = findStudent(studentID);
        Course course = findCourse(courseID);
        if (student == null) {
            System.out.println("Student " + studentID + " not found");
            return false;
        }
        if (course == null) {
            System.out.println("Course " + courseID + " not found");
            return false;
        }
        if (!isEnrolled(student, course)) {
            System.out.println("Student " + studentID + " is not enrolled in course " + courseID);
            return false;
        }
        if (student.countCourses() <= 2) {
            System.out.println("Cannot remove enrollment - student " + studentID + " would have less than 2 courses");
            return false;
        }
        if (course.countStudents() <= 20) {
            System.out.println("Cannot remove enrollment - course " + courseID + " would have less than 20 students");
            return false;
        }
        student.removeCourse(courseID);
        course.removeStudent(studentID);
        return true;
    }

    private boolean removeEnrollmentInternal(int studentID, int courseID) {
        Student student = findStudent(studentID);
        Course course = findCourse(courseID);
        if (student == null) {
            System.out.println("Student " + studentID + " not found");
            return false;
        }
        if (course == null) {
            System.out.println("Course " + courseID + " not found");
            return false;
        }
        if (!isEnrolled(student, course)) {
            System.out.println("Student " + studentID + " is not enrolled in course " + courseID);
            return false;
        }
        student.removeCourse(courseID);
        course.removeStudent(studentID);
        return true;
    }

    private CourseNode sortCourses(CourseNode head) {
        if (head == null || head.getNext() == null) {
            return head;
        }
        
        CourseNode mid = getMiddleCourse(head);
        CourseNode left = head;
        CourseNode right = mid.getNext();
        mid.setNext(null);
        
        left = sortCourses(left);
        right = sortCourses(right);
        
        return mergeCourses(left, right);
    }
    
    private CourseNode getMiddleCourse(CourseNode head) {
        if (head == null) {
            return head;
        }
        
        CourseNode slow = head;
        CourseNode fast = head.getNext();
        
        while (fast != null && fast.getNext() != null) {
            slow = slow.getNext();
            fast = fast.getNext().getNext();
        }
        
        return slow;
    }
    
    private CourseNode mergeCourses(CourseNode left, CourseNode right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        
        CourseNode result;
        if (left.getCourse().getId() <= right.getCourse().getId()) {
            result = left;
            result.setNext(mergeCourses(left.getNext(), right));
        } else {
            result = right;
            result.setNext(mergeCourses(left, right.getNext()));
        }
        
        return result;
    }

    private StudentNode sortStudents(StudentNode head) {
        if (head == null || head.getNext() == null) {
            return head;
        }
        
        StudentNode mid = getMiddleStudent(head);
        StudentNode left = head;
        StudentNode right = mid.getNext();
        mid.setNext(null);
        
        left = sortStudents(left);
        right = sortStudents(right);
        
        return mergeStudents(left, right);
    }
    
    private StudentNode getMiddleStudent(StudentNode head) {
        if (head == null) {
            return head;
        }
        
        StudentNode slow = head;
        StudentNode fast = head.getNext();
        
        while (fast != null && fast.getNext() != null) {
            slow = slow.getNext();
            fast = fast.getNext().getNext();
        }
        
        return slow;
    }
    
    private StudentNode mergeStudents(StudentNode left, StudentNode right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        
        StudentNode result;
        if (left.getStudent().getId() <= right.getStudent().getId()) {
            result = left;
            result.setNext(mergeStudents(left.getNext(), right));
        } else {
            result = right;
            result.setNext(mergeStudents(left, right.getNext()));
        }
        
        return result;
    }

    private void addStudentInternal(int id) {
        Student newStudent = new Student(id);
        newStudent.setNext(studentsHead);
        studentsHead = newStudent;
        lastStudentAdded = newStudent;
    }

    private void removeStudentInternal(int id) {
        Student prev = null;
        Student current = studentsHead;
        while (current != null && current.getId() != id) {
            prev = current;
            current = current.getNext();
        }

        if (prev == null) {
            studentsHead = current.getNext();
        } else {
            prev.setNext(current.getNext());
        }

        if (lastStudentAdded != null && lastStudentAdded.getId() == id) {
            lastStudentAdded = null;
        }
    }

    private void addCourseInternal(int id) {
        Course newCourse = new Course(id);
        newCourse.setNext(coursesHead);
        coursesHead = newCourse;
        lastCourseAdded = newCourse;
    }

    private void removeCourseInternal(int id) {
        Course prev = null;
        Course current = coursesHead;
        while (current != null && current.getId() != id) {
            prev = current;
            current = current.getNext();
        }

        if (prev == null) {
            coursesHead = current.getNext();
        } else {
            prev.setNext(current.getNext());
        }

        if (lastCourseAdded != null && lastCourseAdded.getId() == id) {
            lastCourseAdded = null;
        }
    }

    public void listCoursesByStudent(int studentID) {
        Student student = findStudent(studentID);
        if (student == null) {
            System.out.println("Student " + studentID + " not found");
            return;
        }
        System.out.print("Courses for student " + studentID + ": ");
        CourseNode current = student.getRegisteredCourses();
        while (current != null) {
            System.out.print(current.getCourse().getId() + " ");
            current = current.getNext();
        }
        System.out.println();
    }

    public void listStudentsByCourse(int courseID) {
        Course course = findCourse(courseID);
        if (course == null) {
            System.out.println("Course " + courseID + " not found");
            return;
        }
        System.out.print("Students in course " + courseID + ": ");
        StudentNode current = course.getEnrolledStudents();
        while (current != null) {
            System.out.print(current.getStudent().getId() + " ");
            current = current.getNext();
        }
        System.out.println();
    }

    public void sortStudentsByID(int courseID) {
        Course course = findCourse(courseID);
        if (course == null) {
            System.out.println("Course " + courseID + " not found");
            return;
        }
        StudentNode sorted = sortStudents(course.getEnrolledStudents());
        course.setEnrolledStudents(sorted);
        System.out.println("Students in course " + courseID + " sorted by ID");
    }

    public void sortCoursesByID(int studentID) {
        Student student = findStudent(studentID);
        if (student == null) {
            System.out.println("Student " + studentID + " not found");
            return;
        }
        CourseNode sorted = sortCourses(student.getRegisteredCourses());
        student.setRegisteredCourses(sorted);
        System.out.println("Courses for student " + studentID + " sorted by ID");
    }

    public boolean isfullCourse(int courseID) {
        Course course = findCourse(courseID);
        if (course == null) {
            System.out.println("Course " + courseID + " not found");
            return false;
        }
        return course.countStudents() >= 30;
    }

    public boolean isnormalstudent(int studentID) {
        Student student = findStudent(studentID);
        if (student == null) {
            System.out.println("Student " + studentID + " not found");
            return false;
        }
        int count = student.countCourses();
        return count >= 2 && count <= 7;
    }

    public int getLastStudentAdded() {
        if (lastStudentAdded != null) {
            return lastStudentAdded.getId();
        } else {
            return -1;
        }
    }

    public int getLastCourseAdded() {
        if (lastCourseAdded != null) {
            return lastCourseAdded.getId();
        } else {
            return -1;
        }
    }
}