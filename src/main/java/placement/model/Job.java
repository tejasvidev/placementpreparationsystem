package placement.model;

public class Job {

    private final int jobId;
    private final String jobTitle;
    private final double minimumCgpa;
    private final String eligibleBranch;
    private final String location;

    public Job(int jobId, String jobTitle, double minimumCgpa,
               String eligibleBranch, String location) {

        if (jobId <= 0) {
            throw new IllegalArgumentException("Job ID must be positive");
        }

        if (jobTitle == null || jobTitle.isBlank()) {
            throw new IllegalArgumentException("Job title cannot be empty");
        }

        if (minimumCgpa < 0 || minimumCgpa > 10) {
            throw new IllegalArgumentException("CGPA must be between 0 and 10");
        }

        if (eligibleBranch == null || eligibleBranch.isBlank()) {
            throw new IllegalArgumentException("Eligible branch cannot be empty");
        }

        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }

        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.minimumCgpa = minimumCgpa;
        this.eligibleBranch = eligibleBranch;
        this.location = location;
    }

    public int getJobId() {
        return jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public double getMinimumCgpa() {
        return minimumCgpa;
    }

    public String getEligibleBranch() {
        return eligibleBranch;
    }

    public String getLocation() {
        return location;
    }
}