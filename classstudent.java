class Student {
    private int Studentid; // id of student 
    private CourseNode registeredCourses; // to make connection from student class to class course to make change in it togehter
    private Student next; // this pointer that connect all nodes together and point in the next one
    public Student(int Studentid) {
        this.Studentid = Studentid;
        this.registeredCourses = null;
        this.next = null;
    } // this constructor take student id only and put other of them by null
    public int getId() {
        return Studentid;
    } // to get id
    public CourseNode getRegisteredCourses() {
        return registeredCourses;
    } //method to get registered courses of the student
    public void setRegisteredCourses(CourseNode registeredCourses) {
        this.registeredCourses = registeredCourses;
    } //set registered course
    public Student getNext() {
        return next;
    }
    // get the next one 
    public void setNext(Student next) {
        this.next = next;
    }
    // set the next one 
    public void addCourse(Course course) {
        CourseNode newNode = new CourseNode(course);
        newNode.setNext(registeredCourses);
        registeredCourses = newNode;
    }// to add new course
    public boolean removeCourse(int courseId) {
        if (registeredCourses == null) return false;
        if (registeredCourses.getCourse().getId() == courseId) {
            registeredCourses = registeredCourses.getNext();
            return true;
        }// to remove course if it first
// make the first element the previous and second current and make to it update in while loop for search to the element we would to remove
        CourseNode prev = registeredCourses;
        CourseNode current = registeredCourses.getNext();
        while (current != null) {
            if (current.getCourse().getId() == courseId) {
                prev.setNext(current.getNext());
                return true;
            }
            prev = current;
            current = current.getNext();
        }
        return false;
    }//if course not in the first element while loop to move in each element to find it .
    public int countCourses() {
        int count = 0;
        CourseNode current = registeredCourses;
        while (current != null) {
            count++;
            current = current.getNext();
        }
        return count;
    } // to count number of courses by while loop
}