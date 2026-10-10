
package placement.service;

import placement.dao.JobDAO;
import placement.model.Job;

import java.util.List;

public class JobService {

    private final JobDAO jobDAO = new JobDAO();

    public List<Job> getAvailableJobs() {
        return jobDAO.findAllJobs();
    }


    public boolean postJob(Job job, int companyId) {

        if (job == null) {
            System.out.println("Job cannot be null.");
            return false;
        }

        if (companyId <= 0) {
            System.out.println("Invalid company ID.");
            return false;
        }

        if (job.getJobTitle() == null || job.getJobTitle().isBlank()) {
            System.out.println("Job title cannot be empty.");
            return false;
        }

        if (job.getMinimumCgpa() < 0 || job.getMinimumCgpa() > 10) {
            System.out.println("CGPA must be between 0 and 10.");
            return false;
        }

        int generatedId = jobDAO.addJob(job, companyId);

        if (generatedId == -1) {
            return false;
        }

        System.out.println("Job posted successfully.");
        System.out.println("Generated Job ID: " + generatedId);

        return true;
    }

}
