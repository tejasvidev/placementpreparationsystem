package placement.dao;

import placement.database.DatabaseConnection;
import placement.model.Application;
import placement.model.ApplicationStatus;
import placement.model.Job;
import placement.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;

public class ApplicationDAO {

    public int addApplication(Application application) {

        String sql = """
                INSERT INTO applications
                (student_id, job_id, status, applied_at)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, application.getStudent().getStudentId());
            statement.setInt(2, application.getJob().getJobId());
            statement.setString(3, application.getStatus().name());
            statement.setTimestamp(
                    4,
                    java.sql.Timestamp.valueOf(application.getAppliedAt())
            );

            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    int applicationId = generatedKeys.getInt(1);

                    System.out.println(
                            "Application added successfully."
                    );

                    System.out.println(
                            "Generated Application ID: " + applicationId
                    );

                    return applicationId;
                }

                throw new RuntimeException(
                        "Application ID was not generated."
                );
            }

        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                System.out.println(
                        "Application rejected: this student may have already applied."
                );
                return -1;
        } catch (Exception e) {

            System.out.println(
                    "Failed to add application."
            );

            e.printStackTrace();

            return -1;
        }
    }


    public Application getApplicationById(int applicationId) {

        String sql = """
                SELECT
                        a.application_id,
                        a.status,
                        a.applied_at,

                        s.student_id,
                        s.username,
                        s.password_hash,
                        s.name,
                        s.email,
                        s.cgpa,
                        s.branch,

                        j.job_id,
                        j.job_title,
                        j.minimum_cgpa,
                        j.eligible_branch,
                        j.location

                FROM applications a

                JOIN students s
                        ON a.student_id = s.student_id

                JOIN jobs j
                        ON a.job_id = j.job_id

                WHERE a.application_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, applicationId);

                try (var resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                        Student student = new Student(
                                resultSet.getInt("student_id"),
                                resultSet.getString("username"),
                                resultSet.getString("password_hash"),
                                resultSet.getString("name"),
                                resultSet.getString("email"),
                                resultSet.getDouble("cgpa"),
                                resultSet.getString("branch")
                        );

                        Job job = new Job(
                                resultSet.getInt("job_id"),
                                resultSet.getString("job_title"),
                                resultSet.getDouble("minimum_cgpa"),
                                resultSet.getString("eligible_branch"),
                                resultSet.getString("location")
                        );

                        ApplicationStatus status =
                                ApplicationStatus.valueOf(
                                        resultSet.getString("status")
                                );

                        LocalDateTime appliedAt =
                                resultSet.getTimestamp("applied_at")
                                        .toLocalDateTime();

                        return new Application(
                                resultSet.getInt("application_id"),
                                student,
                                job,
                                status,
                                appliedAt
                        );
                }

                }

        } catch (Exception e) {

                System.out.println(
                        "Failed to fetch application."
                );

                e.printStackTrace();
        }

        return null;
        }

        
        public boolean updateApplicationStatus(
                int applicationId,
                ApplicationStatus status) {

                String sql = """
                        UPDATE applications
                        SET status = ?
                        WHERE application_id = ?
                        """;

                try (Connection connection = DatabaseConnection.getConnection();
                        PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, status.name());
                        statement.setInt(2, applicationId);

                        int rowsUpdated = statement.executeUpdate();

                        if (rowsUpdated > 0) {
                        System.out.println("Application status updated successfully.");
                        return true;
                        }

                        System.out.println("Application not found.");
                        return false;

                } catch (Exception e) {
                        System.out.println("Failed to update application status.");
                        e.printStackTrace();
                        return false;
                }
        }


        
        public boolean hasStudentApplied(int studentId, int jobId) {

        String sql = """
                SELECT 1
                FROM applications
                WHERE student_id = ? AND job_id = ?
                LIMIT 1
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, studentId);
                statement.setInt(2, jobId);

                try (var resultSet = statement.executeQuery()) {
                return resultSet.next();
                }

        } catch (Exception e) {
                System.out.println("Failed to check existing application.");
                e.printStackTrace();
                return true; // Fail safely: don't submit if the check fails.
        }
        }

        
        public List<Application> getAllApplications() {

        List<Application> applications = new ArrayList<>();

        String sql = """
                SELECT
                        a.application_id,
                        a.status AS application_status,
                        a.applied_at,

                        s.student_id,
                        s.username,
                        s.password_hash,
                        s.name,
                        s.email,
                        s.cgpa,
                        s.branch,

                        j.job_id,
                        j.job_title,
                        j.minimum_cgpa,
                        j.eligible_branch,
                        j.location

                FROM applications a
                JOIN students s ON a.student_id = s.student_id
                JOIN jobs j ON a.job_id = j.job_id
                ORDER BY a.applied_at DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                var resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                Student student = new Student(
                        resultSet.getInt("student_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password_hash"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getDouble("cgpa"),
                        resultSet.getString("branch")
                );

                Job job = new Job(
                        resultSet.getInt("job_id"),
                        resultSet.getString("job_title"),
                        resultSet.getDouble("minimum_cgpa"),
                        resultSet.getString("eligible_branch"),
                        resultSet.getString("location")
                );

                Application application = new Application(
                        resultSet.getInt("application_id"),
                        student,
                        job,
                        ApplicationStatus.valueOf(
                                resultSet.getString("application_status")
                        ),
                        resultSet.getTimestamp("applied_at")
                                .toLocalDateTime()
                );

                applications.add(application);
                }

        } catch (Exception e) {
                System.out.println("Failed to fetch applications.");
                e.printStackTrace();
        }

        return applications;
        }

        
        public List<Application> getApplicationsByCompanyId(int companyId) {

        List<Application> applications = new ArrayList<>();

        String sql = """
                SELECT
                        a.application_id,
                        a.status AS application_status,
                        a.applied_at,
                        s.student_id,
                        s.username,
                        s.password_hash,
                        s.name,
                        s.email,
                        s.cgpa,
                        s.branch,
                        j.job_id,
                        j.job_title,
                        j.minimum_cgpa,
                        j.eligible_branch,
                        j.location
                FROM applications a
                JOIN students s ON a.student_id = s.student_id
                JOIN jobs j ON a.job_id = j.job_id
                WHERE j.company_id = ?
                ORDER BY a.applied_at DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, companyId);

                try (var rs = statement.executeQuery()) {
                while (rs.next()) {

                        Student student = new Student(
                                rs.getInt("student_id"),
                                rs.getString("username"),
                                rs.getString("password_hash"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getDouble("cgpa"),
                                rs.getString("branch")
                        );

                        Job job = new Job(
                                rs.getInt("job_id"),
                                rs.getString("job_title"),
                                rs.getDouble("minimum_cgpa"),
                                rs.getString("eligible_branch"),
                                rs.getString("location")
                        );

                        Application application = new Application(
                                rs.getInt("application_id"),
                                student,
                                job,
                                ApplicationStatus.valueOf(
                                        rs.getString("application_status")
                                ),
                                rs.getTimestamp("applied_at").toLocalDateTime()
                        );

                        applications.add(application);
                }
                }

        } catch (Exception e) {
                System.out.println("Failed to fetch company applications.");
                e.printStackTrace();
        }

        return applications;
        }

        
        public boolean applicationBelongsToCompany(
                int applicationId, int companyId) {

        String sql = """
                SELECT 1
                FROM applications a
                JOIN jobs j ON a.job_id = j.job_id
                WHERE a.application_id = ?
                AND j.company_id = ?
                LIMIT 1
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, applicationId);
                statement.setInt(2, companyId);

                try (var rs = statement.executeQuery()) {
                return rs.next();
                }

        } catch (Exception e) {
                System.out.println("Failed to validate application ownership.");
                e.printStackTrace();
                return false;
        }
        }




}