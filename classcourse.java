class Course {
    private int Courseid;// id of course
    private StudentNode enrolledStudents; // to make connection from course class to student class to make change in it
                                          // togehter
    private Course next;// this pointer that connect all nodes together and point in the next one
// this constructor take course id only and put other of them by null
    public Course(int courseid) {
        this.Courseid = courseid;
        this.enrolledStudents = null;
        this.next = null;
    }
    public int getId() {
        return Courseid;
    }
    public StudentNode getEnrolledStudents() {
        return enrolledStudents;
    }
    public void setEnrolledStudents(StudentNode enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }
    public Course getNext() {
        return next;
    }
    public void setNext(Course next) {
        this.next = next;
    }
    public void addStudent(Student student) {
        StudentNode newNode = new StudentNode(student);
        newNode.setNext(enrolledStudents);
        enrolledStudents = newNode;
    }
    public boolean removeStudent(int studentId) {
        if (enrolledStudents == null)
            return false;
        if (enrolledStudents.getStudent().getId() == studentId) {
            enrolledStudents = enrolledStudents.getNext();
            return true;
        }
        StudentNode prev = enrolledStudents;
        StudentNode current = enrolledStudents.getNext();
        while (current != null) {
            if (current.getStudent().getId() == studentId) {
                prev.setNext(current.getNext());
                return true;
            }
            prev = current;
            current = current.getNext();
        }
        return false;
    }
    public int countStudents() {
        int count = 0;
        StudentNode current = enrolledStudents;
        while (current != null) {
            count++;
            current = current.getNext();
        }
        return count;
    }
}