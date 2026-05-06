class CourseNode {
    private Course course;
    private CourseNode next;
    public CourseNode(Course course) {
        this.course = course;
        this.next = null;
    }
    public Course getCourse() {
        return course;
    }
    public CourseNode getNext() {
        return next;
    }
    public void setNext(CourseNode next) {
        this.next = next;
    }
}