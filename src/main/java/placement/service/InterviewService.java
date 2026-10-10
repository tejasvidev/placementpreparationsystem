
package placement.service;

import placement.dao.ApplicationDAO;
import placement.dao.InterviewDAO;
import placement.model.Application;
import placement.model.Interview;

import java.time.LocalDateTime;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;

public class InterviewService {

    private final InterviewDAO interviewDAO = new InterviewDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    public List<Interview> getStudentInterviews(int studentId) {

        if (studentId <= 0) {
            System.out.println("Invalid student ID.");
            return List.of();
        }

        return interviewDAO.getInterviewsByStudentId(studentId);
    }

    public boolean scheduleInterview(
            int applicationId,
            int companyId,
            String interviewType,
            LocalDateTime scheduledAt) {

        if (applicationId <= 0 || companyId <= 0) {
            System.out.println("Invalid application or company ID.");
            return false;
        }

        if (interviewType == null || interviewType.isBlank()) {
            System.out.println("Interview type cannot be empty.");
            return false;
        }

        if (scheduledAt == null || scheduledAt.isBefore(LocalDateTime.now())) {
            System.out.println("Please provide a valid future date and time.");
            return false;
        }

        if (!applicationDAO.applicationBelongsToCompany(
                applicationId, companyId)) {
            System.out.println(
                    "Application not found for your company."
            );
            return false;
        }

        Application application =
                applicationDAO.getApplicationById(applicationId);

        if (application == null) {
            System.out.println("Application could not be retrieved.");
            return false;
        }

        Interview interview = new Interview(
                application,
                interviewType.trim(),
                scheduledAt
        );

        int interviewId = interviewDAO.addInterview(interview);

        return interviewId != -1;
    }

    
    public List<Interview> getStudentInterviewsChronologically(int studentId) {
        List<Interview> interviews = getStudentInterviews(studentId);

        PriorityQueue<Interview> interviewQueue =
                new PriorityQueue<>(
                        Comparator.comparing(
                                Interview::getScheduledAt
                        )
                );

        interviewQueue.addAll(interviews);

        List<Interview> sortedInterviews = new java.util.ArrayList<>();

        while (!interviewQueue.isEmpty()) {
            sortedInterviews.add(interviewQueue.poll());
        }

        return sortedInterviews;
    }

}
