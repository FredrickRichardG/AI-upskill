import com.example.hrms.dto.EmployeeDTO;
import com.example.hrms.exception.EmployeeNotFoundException;
import com.example.hrms.repository.EmployeeRepository;
import com.example.hrms.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * An improved implementation of the EmployeeService that uses efficient,
 * database-delegated queries for better performance.
 */
@Service("efficientEmployeeService")
public class EfficientEmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EfficientEmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // 1. Register a new employee (logic remains the same)
    @Override
    public EmployeeDTO registerEmployee(EmployeeDTO employeeDTO) {
        if (employeeDTO.getName() == null || employeeDTO.getDepartment() == null) {
            throw new IllegalArgumentException("Employee name and department must not be null");
        }
        employeeDTO.setStatus("ACTIVE");
        employeeDTO.setRemainingLeaves(20);
        return employeeRepository.save(employeeDTO);
    }

    // 2. Fetch employee details by ID (logic remains the same)
    @Override
    public EmployeeDTO getEmployeeById(String employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));
    }

    // 3. Update employee status (logic remains the same for this improvement)
    @Override
    public boolean updateEmployeeStatus(String employeeId, String status) {
        EmployeeDTO emp = getEmployeeById(employeeId);
        emp.setStatus(status);
        employeeRepository.save(emp);
        return true;
    }

    // 4. Calculate remaining leaves (logic remains the same)
    @Override
    public int calculateRemainingLeaves(String employeeId) {
        EmployeeDTO emp = getEmployeeById(employeeId);
        return emp.getRemainingLeaves();
    }

    /**
     * --- PERFORMANCE IMPROVEMENT IMPLEMENTED ---
     * Finds all employees in a given department using an efficient, database-delegated query.
     * This avoids loading the entire 'employees' table into application memory.
     *
     * @param department The department to search for.
     * @return A list of employees in the specified department.
     */
    @Override
    public List<EmployeeDTO> getEmployeesByDepartment(String department) {
        // This is now highly efficient. It pushes the filtering logic down to the
        // database, which can use an index on the 'department' column for a fast lookup.
        return employeeRepository.findByDepartmentIgnoreCase(department);
    }
} 