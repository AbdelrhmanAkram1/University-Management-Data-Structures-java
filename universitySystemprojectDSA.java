public class universitySystemprojectDSA {
    public static void main(String[] args) {
        UniversitySystem system = new UniversitySystem();
        
        // Test 1: Add students
        System.out.println("\n=== Test 1: Add students ===");
        system.addStudent(101);  // Added student 101
        system.addStudent(102);  // Added student 102
        system.addStudent(101);  // Student 101 already exists
        
        // Test 2: Add courses
        System.out.println("\n=== Test 2: Add courses ===");
        system.addCourse(501);   // Added course 501
        system.addCourse(502);   // Added course 502
        system.addCourse(501);   // Course 501 already exists
        
        // Test 3: Enroll students in courses
        System.out.println("\n=== Test 3: Enroll students ===");
        system.enrollStudent(101, 501);  // Enrolled student 101 in course 501
        system.enrollStudent(102, 501);  // Enrolled student 102 in course 501
        system.enrollStudent(101, 502);  // Enrolled student 101 in course 502
        system.enrollStudent(101, 501);  // Student 101 is already enrolled in course 501
        
        // Test 4: List courses by student and students by course
        System.out.println("\n=== Test 4: Listing ===");
        system.listCoursesByStudent(101);  // Courses for student 101: 501 502
        system.listStudentsByCourse(501);  // Students in course 501: 101 102
        
        // Test 5: Remove enrollments
        System.out.println("\n=== Test 5: Remove enrollments ===");
        system.removeEnrollment(101, 501);  // Removed student 101 from course 501
        system.removeEnrollment(101, 501);  // Student 101 is not enrolled in course 501
        
        // Test 6: Undo operations
        System.out.println("\n=== Test 6: Undo operations ===");
        system.undo();  // Undo: Enrolled student 101 in course 501
        system.listCoursesByStudent(101);  // Courses for student 101: 501 502
        
        // Test 7: Redo operations
        System.out.println("\n=== Test 7: Redo operations ===");
        system.redo();  // Redo: Removed student 101 from course 501
        system.listCoursesByStudent(101);  // Courses for student 101: 502
        
        // Test 8: Remove students and courses
        System.out.println("\n=== Test 8: Remove students and courses ===");
        system.removeStudent(101);  // Removed student 101
        system.removeCourse(501);   // Removed course 501
        system.listStudentsByCourse(501);  // Course 501 not found
        
        // Test 9: Course capacity constraints
        System.out.println("\n=== Test 9: Course capacity ===");
        // Add 30 students
        for (int i = 200; i < 230; i++) {
            system.addStudent(i);
            system.enrollStudent(i, 502);
        }
        system.enrollStudent(102, 502);  // Cannot enroll student 102 - course 502 is full (30 students)
        
        // Test 10: Student course load constraints
        System.out.println("\n=== Test 10: Student course load ===");
        system.addCourse(503);
        system.addCourse(504);
        system.addCourse(505);
        system.addCourse(506);
        system.addCourse(507);
        system.addCourse(508);
       
        system.enrollStudent(102, 5030);  // Enrolled student 102 in course 503
        system.enrollStudent(102, 504);  // Enrolled student 102 in course 504
        system.enrollStudent(102, 505);  // Enrolled student 102 in course 505
        system.enrollStudent(102, 506);  // Enrolled student 102 in course 506
        system.enrollStudent(102, 507);  // Enrolled student 102 in course 507
        system.enrollStudent(102, 508);  // Cannot enroll student 102 - maximum courses reached (7)
        
        // Test 11: Minimum course constraints
        System.out.println("\n=== Test 11: Minimum course constraints ===");
        system.removeEnrollment(102, 503);  // Removed student 102 from course 503
        system.removeEnrollment(102, 504);  // Removed student 102 from course 504
        system.removeEnrollment(102, 505);  // Removed student 102 from course 505
        system.removeEnrollment(102, 506);  // Cannot remove enrollment - student 102 would have less than 2 courses
        
        // Test 12: Last added student/course
        System.out.println("\n=== Test 12: Last added ===");
        system.addStudent(999);
        system.addCourse(999);
        System.out.println("Last student added: " + system.getLastStudentAdded());  // 999
        System.out.println("Last course added: " + system.getLastCourseAdded());    // 999
    }
}