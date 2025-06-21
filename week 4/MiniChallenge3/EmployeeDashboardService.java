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
public class EmployeeDashboardService {

    // In a real application, this would be managed by a dedicated configuration file.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/employees_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    /**
     * Retrieves and displays the dashboard information for a specific employee from the database.
     * <p>
     * This method securely queries the 'employee_dashboard' table for an employee matching
     * the given ID. It prints the employee's name, department, and status to the console.
     * <p>
     * The method is designed to be safe from SQL injection attacks by using a
     * {@link java.sql.PreparedStatement}. It also ensures that all database resources
     * are properly closed using a try-with-resources statement to prevent resource leaks.
     *
     * @param employeeId The unique identifier for the employee whose dashboard is to be viewed.
     *                   This should be a non-null, non-empty String.
     *
     * @throws SQLException if a database access error occurs or this method is called on a
     *                      closed connection. The error details will be printed to the standard
     *                      error stream.
     *
     * @see java.sql.PreparedStatement
     * @see java.sql.Connection
     * @see java.sql.ResultSet
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * EmployeeDashboardService service = new EmployeeDashboardService();
     * // Displays dashboard for employee E456
     * service.viewEmployeeDashboard("E456");
     *
     * // Attempts to use a malicious string, which will be handled safely
     * service.viewEmployeeDashboard("E456' OR '1'='1");
     * }</pre>
     *
     * <h3>Performance Notes:</h3>
     * This implementation opens a new database connection for every call, which is inefficient
     * and not recommended for production environments. For better performance and scalability,
     * a connection pool (e.g., HikariCP, c3p0) should be used to manage and reuse database
     * connections.
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
        EmployeeDashboardService service = new EmployeeDashboardService();

        System.out.println("--- Testing with a normal, valid ID ---");
        service.viewEmployeeDashboard("E123");

        System.out.println("\n--- Testing with a malicious input string ---");
        // This input will now fail safely and will not return any data,
        // preventing the SQL injection attack.
        service.viewEmployeeDashboard("E123' OR '1'='1");
    }
} 