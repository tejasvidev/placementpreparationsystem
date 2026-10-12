package placement.dao;

import placement.database.DatabaseConnection;
import placement.model.Company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CompanyDAO {


    public Company findByUsername(String username) {

        String sql = """
                SELECT company_id, username, password_hash,
                    name, email, industry
                FROM companies
                WHERE username = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                return new Company(
                        resultSet.getInt("company_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password_hash"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("industry")
                );
            }

        } catch (Exception e) {
            System.out.println("Failed to find company.");
            e.printStackTrace();
        }

        return null;
    }

    public int registerCompany(Company company) {

        String sql = """
                INSERT INTO companies
                (username, password_hash, name, email, industry)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, company.getUsername());
            statement.setString(2, company.getPasswordHash());
            statement.setString(3, company.getName());
            statement.setString(4, company.getEmail());
            statement.setString(5, company.getIndustry());

            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Failed to register company."
            );

            e.printStackTrace();
        }

        return -1;
    }


}