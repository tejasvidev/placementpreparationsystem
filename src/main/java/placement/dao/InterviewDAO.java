package placement.dao;

import placement.database.DatabaseConnection;
import placement.model.Application;
import placement.model.ApplicationStatus;
import placement.model.Interview;
import placement.model.InterviewStatus;
import placement.model.Job;
import placement.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class InterviewDAO {

    public int addInterview(Interview interview) {

        String sql = """
                INSERT INTO interviews
                (application_id, interview_type, scheduled_at, status)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(
                    1,
                    interview.getApplication().getApplicationId()
            );

            statement.setString(
                    2,
                    interview.getInterviewType()
            );

            statement.setTimestamp(
                    3,
                    java.sql.Timestamp.valueOf(
                            interview.getScheduledAt()
                    )
            );

            statement.setString(
                    4,
                    interview.getStatus().name()
            );

            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    int interviewId =
                            generatedKeys.getInt(1);

                    System.out.println(
                            "Interview added successfully."
                    );

                    System.out.println(
                            "Generated Interview ID: "
                                    + interviewId
                    );

                    return interviewId;
                }

                throw new RuntimeException(
                        "Interview ID was not generated."
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Failed to add interview."
            );

            e.printStackTrace();

            return -1;
        }
    }


    public Interview getInterviewById(int interviewId) {

        String sql = """
                SELECT
                    i.interview_id,
                    i.interview_type,
                    i.scheduled_at,
                    i.status,

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

                FROM interviews i

                JOIN applications a
                    ON i.application_id = a.application_id

                JOIN students s
                    ON a.student_id = s.student_id

                JOIN jobs j
                    ON a.job_id = j.job_id

                WHERE i.interview_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setInt(1, interviewId);

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

                    Interview interview = new Interview(
                            resultSet.getInt("interview_id"),
                            application,
                            resultSet.getString("interview_type"),
                            resultSet.getTimestamp("scheduled_at")
                                    .toLocalDateTime()
                    );

                    interview.setStatus(
                            InterviewStatus.valueOf(
                                    resultSet.getString("status")
                            )
                    );

                    return interview;
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Failed to fetch interview."
            );

            e.printStackTrace();
        }

        return null;
    }


    public void updateInterviewStatus(
        int interviewId,
        InterviewStatus status) {

        String sql = """
                UPDATE interviews
                SET status = ?
                WHERE interview_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setString(1, status.name());
            statement.setInt(2, interviewId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println(
                        "Interview status updated successfully."
                );
            } else {
                System.out.println(
                        "Interview not found."
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Failed to update interview status."
            );

            e.printStackTrace();
        }
    }

    
        public List<Interview> getInterviewsByStudentId(int studentId) {

                List<Interview> interviews = new ArrayList<>();

                String sql = """
                        SELECT
                                i.interview_id,
                                i.interview_type,
                                i.scheduled_at,
                                i.status,

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

                        FROM interviews i
                        JOIN applications a
                                ON i.application_id = a.application_id
                        JOIN students s
                                ON a.student_id = s.student_id
                        JOIN jobs j
                                ON a.job_id = j.job_id
                        WHERE s.student_id = ?
                        ORDER BY i.scheduled_at
                        """;

                try (Connection connection = DatabaseConnection.getConnection();
                        PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setInt(1, studentId);

                        try (var resultSet = statement.executeQuery()) {

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

                                Interview interview = new Interview(
                                        resultSet.getInt("interview_id"),
                                        application,
                                        resultSet.getString("interview_type"),
                                        resultSet.getTimestamp("scheduled_at")
                                                .toLocalDateTime()
                                );

                                interview.setStatus(
                                        InterviewStatus.valueOf(
                                                resultSet.getString("status")
                                        )
                                );

                                interviews.add(interview);
                        }
                        }

                } catch (Exception e) {
                        System.out.println("Failed to fetch student interviews.");
                        e.printStackTrace();
                }

                return interviews;
        }


}