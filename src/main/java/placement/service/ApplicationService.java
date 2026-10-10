
package placement.service;

import placement.dao.ApplicationDAO;
import placement.model.Application;
import placement.model.ApplicationStatus;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
public class ApplicationService {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

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

}
