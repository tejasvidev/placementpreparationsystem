package placement.model;

import java.time.LocalDateTime;

public class Interview {

    private final int interviewId;
    private final Application application;
    private final String interviewType;
    private final LocalDateTime scheduledAt;
    private InterviewStatus status;

    public Interview(int interviewId,
                     Application application,
                     String interviewType,
                     LocalDateTime scheduledAt) {

        if (interviewId <= 0) {
            throw new IllegalArgumentException(
                    "Interview ID must be positive"
            );
        }

        if (application == null) {
            throw new IllegalArgumentException(
                    "Application cannot be null"
            );
        }

        if (interviewType == null || interviewType.isBlank()) {
            throw new IllegalArgumentException(
                    "Interview type cannot be empty"
            );
        }

        if (scheduledAt == null) {
            throw new IllegalArgumentException(
                    "Interview date and time cannot be null"
            );
        }

        this.interviewId = interviewId;
        this.application = application;
        this.interviewType = interviewType;
        this.scheduledAt = scheduledAt;
        this.status = InterviewStatus.SCHEDULED;
    }

    public Interview(
        Application application,
        String interviewType,
        LocalDateTime scheduledAt) {

        if (application == null) {
            throw new IllegalArgumentException(
                    "Application cannot be null"
            );
        }

        if (interviewType == null || interviewType.isBlank()) {
            throw new IllegalArgumentException(
                    "Interview type cannot be empty"
            );
        }

        if (scheduledAt == null) {
            throw new IllegalArgumentException(
                    "Interview date and time cannot be null"
            );
        }

        this.interviewId = 0;
        this.application = application;
        this.interviewType = interviewType;
        this.scheduledAt = scheduledAt;
        this.status = InterviewStatus.SCHEDULED;
    }

    public int getInterviewId() {
        return interviewId;
    }

    public Application getApplication() {
        return application;
    }

    public String getInterviewType() {
        return interviewType;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Interview status cannot be null"
            );
        }

        this.status = status;
    }
}