
package placement.dao;

import placement.database.DatabaseConnection;
import placement.model.Job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {

    // Add a job and let MySQL generate its ID
    
    public int addJob(Job job, int companyId) {

        String sql = """
                INSERT INTO jobs
                (job_title, minimum_cgpa, eligible_branch, location, company_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, job.getJobTitle());
            statement.setDouble(2, job.getMinimumCgpa());
            statement.setString(3, job.getEligibleBranch());
            statement.setString(4, job.getLocation());
            statement.setInt(5, companyId);

            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            System.out.println("Job ID was not generated.");
            return -1;

        } catch (Exception e) {
            System.out.println("Failed to add job.");
            e.printStackTrace();
            return -1;
        }
    }


    // Retrieve all available jobs
    public List<Job> findAllJobs() {

        List<Job> jobs = new ArrayList<>();

        String sql = """
                SELECT job_id, job_title, minimum_cgpa,
                       eligible_branch, location
                FROM jobs
                ORDER BY job_id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Job job = new Job(
                        resultSet.getInt("job_id"),
                        resultSet.getString("job_title"),
                        resultSet.getDouble("minimum_cgpa"),
                        resultSet.getString("eligible_branch"),
                        resultSet.getString("location")
                );

                jobs.add(job);
            }

        } catch (Exception e) {
            System.out.println("Failed to retrieve jobs.");
            e.printStackTrace();
        }

        return jobs;
    }
}
