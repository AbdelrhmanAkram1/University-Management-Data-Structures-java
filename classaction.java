class Action {
    private String type;
    private int studentID;
    private int courseID;
    private int[] additionalIds;
    public Action(String type, int studentID, int courseID) {
        this.type = type;
        this.studentID = studentID;
        this.courseID = courseID;
    }
    public Action(String type, int id, int[] additionalIds) {
        this.type = type;
        this.studentID = id;
        this.courseID = -1;
        this.additionalIds = additionalIds;
    }
    public String getType() { return type; }
    public int getStudentID() { return studentID; }
    public int getCourseID() { return courseID; }
    public int[] getAdditionalIds() { return additionalIds; }
}
