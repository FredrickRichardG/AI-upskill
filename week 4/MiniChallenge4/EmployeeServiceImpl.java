import com.example.hrms.dto.EmployeeDTO;
import com.example.hrms.exception.EmployeeNotFoundException;
import com.example.hrms.repository.EmployeeRepository;
import com.example.hrms.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // 1. Register a new employee
    @Override
    public EmployeeDTO registerEmployee(EmployeeDTO employeeDTO) {
        if (employeeDTO.getName() == null || employeeDTO.getDepartment() == null) {
            throw new IllegalArgumentException("Employee name and department must not be null");
        }

        employeeDTO.setStatus("ACTIVE");
        employeeDTO.setRemainingLeaves(20); // default leave balance
        return employeeRepository.save(employeeDTO);
    }

    // 2. Fetch employee details by ID
    @Override
    public EmployeeDTO getEmployeeById(String employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));
    }

    // 3. Update employee status
    @Override
    public boolean updateEmployeeStatus(String employeeId, String status) {
        EmployeeDTO emp = getEmployeeById(employeeId);
        emp.setStatus(status);
        employeeRepository.save(emp);
        return true;
    }

    // 4. Calculate remaining leaves
    @Override
    public int calculateRemainingLeaves(String employeeId) {
        EmployeeDTO emp = getEmployeeById(employeeId);
        return emp.getRemainingLeaves(); // In real life, you might deduct based on attendance or holidays
    }

    // 5. Get employees by department
    @Override
    public List<EmployeeDTO> getEmployeesByDepartment(String department) {
        return employeeRepository.findAll().stream()
                .filter(emp -> department.equalsIgnoreCase(emp.getDepartment()))
                .collect(Collectors.toList());
    }
}