package placement.service;

import placement.dao.CompanyDAO;
import placement.dao.StudentDAO;
import placement.database.DatabaseConnection;
import placement.model.Company;
import placement.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthenticationService {

    public Student loginStudent(String username, String password) {

        StudentDAO studentDAO = new StudentDAO();

        Student student = studentDAO.findByUsername(username);

        if (student == null) {
            System.out.println("Student not found.");
            return null;
        }

        if (!student.getPasswordHash().equals(password)) {
            System.out.println("Invalid password.");
            return null;
        }

        System.out.println("Student login successful.");

        return student;
    }

    public void registerStudent(Student student) {

        if (usernameExists(student.getUsername())) {
            System.out.println("Username already exists.");
            return;
        }

        StudentDAO studentDAO = new StudentDAO();

        int generatedId = studentDAO.registerStudent(student);

        if (generatedId != -1) {
            System.out.println("Student registered successfully.");
            System.out.println(
                    "Generated Student ID: " + generatedId
            );
        }
    }

    public boolean usernameExists(String username) {

        String sql = """
                SELECT 1
                FROM students
                WHERE username = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (Exception e) {
            System.out.println("Could not check username.");
            e.printStackTrace();
            return false;
        }
    }

    public Company loginCompany(String username, String password) {

        CompanyDAO companyDAO = new CompanyDAO();

        Company company =
                companyDAO.findByUsername(username);

        if (company == null) {
            System.out.println("Company not found.");
            return null;
        }

        if (!company.getPasswordHash().equals(password)) {
            System.out.println("Invalid password.");
            return null;
        }

        System.out.println("Company login successful.");

        return company;
    }

    public void registerCompany(Company company) {

        if (companyUsernameExists(company.getUsername())) {
            System.out.println("Company username already exists.");
            return;
        }

        CompanyDAO companyDAO = new CompanyDAO();

        int generatedId = companyDAO.registerCompany(company);

        if (generatedId != -1) {

            System.out.println(
                    "Company registered successfully."
            );

            System.out.println(
                    "Generated Company ID: " + generatedId
            );
        }
    }

    public boolean companyUsernameExists(String username) {

        String sql = """
                SELECT 1
                FROM companies
                WHERE username = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (Exception e) {

            System.out.println(
                    "Could not check company username."
            );

            e.printStackTrace();
            return false;
        }
    }


}