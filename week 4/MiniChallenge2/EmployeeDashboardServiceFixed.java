import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A refactored and secure version of the EmployeeDashboardService.
 * This version uses PreparedStatements to prevent SQL injection and try-with-resources
 * to ensure proper resource management.
 */
public class EmployeeDashboardServiceFixed {

    // In a real application, this would be managed by a dedicated configuration file.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/employees_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    /**
     * Views the employee dashboard in a secure and efficient manner.
     *
     * @param employeeId The ID of the employee to view.
     */
    public void viewEmployeeDashboard(String employeeId) {

        // --- PERFORMANCE BOTTLENECK FIXED: Inefficient Query ---
        // The query now selects only the specific columns needed, reducing network traffic
        // and memory usage compared to 'SELECT *'.
        // --- SECURITY VULNERABILITY FIXED: SQL Injection ---
        // A PreparedStatement is used to safely handle the user-provided employeeId.
        String sql = "SELECT employee_name, department, status FROM employee_dashboard WHERE employee_id = ?";

        // --- PERFORMANCE BOTTLENECK FIXED: Resource Leaks ---
        // A 'try-with-resources' block ensures that the Connection, PreparedStatement,
        // and ResultSet are all automatically closed, even if an exception occurs.
        // NOTE: In a production application, you would use a connection pool (e.g., HikariCP)
        // instead of DriverManager.getConnection() for better performance.
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // The '?' placeholder is safely replaced with the employeeId.
            // The JDBC driver ensures the value is treated as data, not executable SQL.
            pstmt.setString(1, employeeId);

            System.out.println("Executing query: " + pstmt);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No dashboard found for employee ID: " + employeeId);
                    return;
                }

                while (rs.next()) {
                    String name = rs.getString("employee_name");
                    String dept = rs.getString("department");
                    String status = rs.getString("status");

                    System.out.println("--- Dashboard for " + name + " ---");
                    System.out.println("Department: " + dept);
                    System.out.println("Status: " + status);
                    System.out.println("---------------------------------");
                }
            }

        } catch (SQLException e) {
            // Log the exception properly in a real application.
            System.err.println("Error retrieving dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EmployeeDashboardServiceFixed service = new EmployeeDashboardServiceFixed();

        System.out.println("--- Testing with a normal, valid ID ---");
        service.viewEmployeeDashboard("E123");

        System.out.println("\n--- Testing with a malicious input string ---");
        // This input will now fail safely and will not return any data,
        // preventing the SQL injection attack.
        service.viewEmployeeDashboard("E123' OR '1'='1");
    }
} 