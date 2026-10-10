package placement.model;

import java.time.LocalDateTime;

public class Application {

    private final int applicationId;
    private final Student student;
    private final Job job;
    private ApplicationStatus status;
    private final LocalDateTime appliedAt;

    public Application(int applicationId,
                       Student student,
                       Job job) {

        if (applicationId <= 0) {
            throw new IllegalArgumentException(
                    "Application ID must be positive"
            );
        }

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student cannot be null"
            );
        }

        if (job == null) {
            throw new IllegalArgumentException(
                    "Job cannot be null"
            );
        }

        this.applicationId = applicationId;
        this.student = student;
        this.job = job;
        this.status = ApplicationStatus.SUBMITTED;
        this.appliedAt = LocalDateTime.now();
    }

    public Application(Student student, Job job) {

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student cannot be null"
            );
        }

        if (job == null) {
            throw new IllegalArgumentException(
                    "Job cannot be null"
            );
        }

        this.applicationId = 0;
        this.student = student;
        this.job = job;
        this.status = ApplicationStatus.SUBMITTED;
        this.appliedAt = LocalDateTime.now();
    }

    public Application(int applicationId,
                    Student student,
                    Job job,
                    ApplicationStatus status,
                    LocalDateTime appliedAt) {

        if (applicationId <= 0) {
            throw new IllegalArgumentException(
                    "Application ID must be positive"
            );
        }

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student cannot be null"
            );
        }

        if (job == null) {
            throw new IllegalArgumentException(
                    "Job cannot be null"
            );
        }

        if (status == null) {
            throw new IllegalArgumentException(
                    "Status cannot be null"
            );
        }

        if (appliedAt == null) {
            throw new IllegalArgumentException(
                    "Applied time cannot be null"
            );
        }

        this.applicationId = applicationId;
        this.student = student;
        this.job = job;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public Student getStudent() {
        return student;
    }

    public Job getJob() {
        return job;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setStatus(ApplicationStatus status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Status cannot be null"
            );
        }

        this.status = status;
    }
}