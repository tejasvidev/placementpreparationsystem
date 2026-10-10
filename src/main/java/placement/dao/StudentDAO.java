package placement.dao;

import placement.database.DatabaseConnection;
import placement.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class StudentDAO {

    public void addStudent(Student student) {

        String sql = """
                INSERT INTO students
                (student_id, username, password_hash, name, email, cgpa, branch)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, student.getStudentId());
            statement.setString(2, student.getUsername());
            statement.setString(3, student.getPasswordHash());
            statement.setString(4, student.getName());
            statement.setString(5, student.getEmail());
            statement.setDouble(6, student.getCgpa());
            statement.setString(7, student.getBranch());

            statement.executeUpdate();

            System.out.println("Student added successfully.");

        } catch (Exception e) {
            System.out.println("Failed to add student.");
            e.printStackTrace();
        }
    }

    public Student findByUsername(String username) {

        String sql = """
                SELECT
                    student_id,
                    username,
                    password_hash,
                    name,
                    email,
                    cgpa,
                    branch
                FROM students
                WHERE username = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (var resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Student(
                            resultSet.getInt("student_id"),
                            resultSet.getString("username"),
                            resultSet.getString("password_hash"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getDouble("cgpa"),
                            resultSet.getString("branch")
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Failed to find student."
            );

            e.printStackTrace();
        }

        return null;
    }

    public int registerStudent(Student student) {

        String sql = """
                INSERT INTO students
                (username, password_hash, name, email, cgpa, branch)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, student.getUsername());
            statement.setString(2, student.getPasswordHash());
            statement.setString(3, student.getName());
            statement.setString(4, student.getEmail());
            statement.setDouble(5, student.getCgpa());
            statement.setString(6, student.getBranch());

            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            System.out.println(
                    "Registration failed: username already exists."
            );
            return -1;
        } catch (Exception e) {

            System.out.println(
                    "Failed to register student."
            );

            e.printStackTrace();
            return -1;
        } 

        return -1;


    }
}