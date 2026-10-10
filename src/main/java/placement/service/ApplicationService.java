
package placement.service;

import placement.dao.ApplicationDAO;
import placement.model.Application;
import placement.model.ApplicationStatus;
import placement.concurrency.ApplicationProcessor;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
public class ApplicationService {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private static final ApplicationProcessor applicationProcessor =
        new ApplicationProcessor();

    public List<Application> getApplicationsByCompanyId(int companyId) {

        if (companyId <= 0) {
            System.out.println("Invalid company ID.");
            return List.of();
        }

        return applicationDAO.getApplicationsByCompanyId(companyId);
    }

    
    public List<Application> getApplicationsSortedById(int companyId) {
        List<Application> applications =
                getApplicationsByCompanyId(companyId);

        applications.sort(
                java.util.Comparator.comparingInt(
                        Application::getApplicationId
                )
        );

        return applications;
    }


    public boolean updateStatus(
            int applicationId,
            int companyId,
            ApplicationStatus newStatus) {

        if (applicationId <= 0 || companyId <= 0) {
            System.out.println("Invalid application or company ID.");
            return false;
        }

        if (newStatus == null) {
            System.out.println("Invalid application status.");
            return false;
        }

        if (!applicationDAO.applicationBelongsToCompany(
                applicationId, companyId)) {
            System.out.println(
                    "Application not found for your company."
            );
            return false;
        }

        return applicationDAO.updateApplicationStatus(applicationId, newStatus);
    }

    
    public Map<ApplicationStatus, Integer> getApplicationStatusCounts(
            int companyId) {

        List<Application> applications =
                getApplicationsByCompanyId(companyId);

        Map<ApplicationStatus, Integer> statusCounts =
                new HashMap<>();

        for (Application application : applications) {
            ApplicationStatus status = application.getStatus();

            statusCounts.put(
                    status,
                    statusCounts.getOrDefault(status, 0) + 1
            );
        }

        return statusCounts;
    }

    

    public int submitApplication(Application application) {

        if (application == null
                || application.getStudent() == null
                || application.getJob() == null) {
            System.out.println("Invalid application.");
            return -1;
        }

        int studentId = application.getStudent().getStudentId();
        int jobId = application.getJob().getJobId();

        if (studentId <= 0 || jobId <= 0) {
            System.out.println("Invalid student or job ID.");
            return -1;
        }

        // Validate CGPA eligibility.
        if (application.getStudent().getCgpa()
                < application.getJob().getMinimumCgpa()) {
            System.out.println("Student is not eligible based on CGPA.");
            return -1;
        }

        // Validate branch eligibility.
        String eligibleBranch = application.getJob().getEligibleBranch();
        String studentBranch = application.getStudent().getBranch();

        if (eligibleBranch != null
                && !eligibleBranch.equalsIgnoreCase("ALL")
                && !eligibleBranch.equalsIgnoreCase(studentBranch)) {
            System.out.println(
                    "Student is not eligible for this job's branch."
            );
            return -1;
        }

        // Prevent duplicate applications.
        if (applicationDAO.hasStudentApplied(studentId, jobId)) {
            System.out.println(
                    "Student has already applied for this job."
            );
            return -1;
        }

        // Insert into MySQL.
        int applicationId = applicationDAO.addApplication(application);

        // Increment only after successful insertion.
        if (applicationId != -1) {
            applicationProcessor.incrementApplicationsProcessed();
        }

        return applicationId;
    }

    
    public int getTotalApplicationsProcessed() {
        return applicationProcessor.getTotalApplicationsProcessed();
    }




}
