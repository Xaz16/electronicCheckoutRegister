package edu.istu.achipiga;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    private static final String DB_URL = "jdbc:sqlite:checkout_register.db";

    public static void initializeDatabase() {
        // Check if tables already exist
        if (databaseNeedsInitialization()) {
            System.out.println("Initializing database...");
            boolean isSuccess = executeSqlScript("/init_database.sql");
            if (isSuccess) {
                System.out.println("Database initialized successfully!");
            }
        } else {
            System.out.println("Database already initialized.");
        }
    }

    private static boolean databaseNeedsInitialization() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Check if products table exists
            return !conn.getMetaData().getTables(null, null, "products", null).next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    private static boolean executeSqlScript(String resourcePath) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Read SQL script from resources
            String sqlScript = readResourceFile(resourcePath);

            if (!sqlScript.trim().isEmpty()) {
                stmt.executeUpdate(sqlScript);
            }
            return true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String readResourceFile(String resourcePath) throws IOException {
        try (InputStream is = DatabaseInitializer.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
    }
}