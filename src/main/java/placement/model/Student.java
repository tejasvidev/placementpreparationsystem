package placement.model;

import placement.interfaces.Eligible;

public class Student extends User implements Eligible {

    private final int studentId;
    private static int totalStudents = 0;
    private final double cgpa;
    private final String branch;

    public Student(int studentId,
                   String username,
                   String passwordHash,
                   String name,
                   String email,
                   double cgpa,
                   String branch) {

        super(username, passwordHash, name, email);

        if (studentId <= 0) {
            throw new IllegalArgumentException("Student ID must be positive");
        }

        if (cgpa < 0 || cgpa > 10) {
            throw new IllegalArgumentException("CGPA must be between 0 and 10");
        }

        if (branch == null || branch.isBlank()) {
            throw new IllegalArgumentException("Branch cannot be empty");
        }

        this.studentId = studentId;
        this.cgpa = cgpa;
        this.branch = branch;

        totalStudents++;
    }

    public Student(
            String username,
            String passwordHash,
            String name,
            String email,
            double cgpa,
            String branch
    ) {
        super(username, passwordHash, name, email);

        this.studentId = 0;
        this.cgpa = cgpa;
        this.branch = branch;
    }

    public int getStudentId() {
        return studentId;
    }

    public static int getTotalStudents() {
        return totalStudents;
    }

    public double getCgpa() {
        return cgpa;
    }

    public String getBranch() {
        return branch;
    }

    @Override
    public void displayProfile() {
        System.out.println("Student ID: " + studentId);
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("CGPA: " + cgpa);
        System.out.println("Branch: " + branch);
    }

    @Override
    public boolean checkEligibility(Job job) {
        return this.cgpa >= job.getMinimumCgpa()
                && this.branch.equalsIgnoreCase(job.getEligibleBranch());
    }


    public boolean isValidEmail() {
        String email = getEmail();

        return email.contains("@")
                && email.contains(".")
                && email.indexOf("@") < email.lastIndexOf(".");
    }
}