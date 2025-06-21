import com.example.hrms.dto.EmployeeDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Spring Data JPA repository for Employee entities.
 * <p>
 * This interface includes a custom query method to efficiently find employees
 * by their department.
 */
public interface EmployeeRepository extends JpaRepository<EmployeeDTO, String> {

    /**
     * Finds all employees belonging to a specific department, ignoring case considerations.
     * <p>
     * By creating a method with this specific name, Spring Data JPA will automatically
     * generate and execute a query like:
     * {@code SELECT * FROM employees WHERE LOWER(department) = LOWER(?)}
     * This is highly performant as it delegates the filtering to the database,
     * which can leverage an index on the 'department' column for fast lookups.
     *
     * @param department The department name to search for.
     * @return A {@link List} of {@link EmployeeDTO} objects matching the department.
     *         Returns an empty list if no employees are found.
     */
    List<EmployeeDTO> findByDepartmentIgnoreCase(String department);
} 