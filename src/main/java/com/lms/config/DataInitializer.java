package com.lms.config;

import com.lms.model.Department;
import com.lms.model.Employee;
import com.lms.model.LeaveRequest;
import com.lms.model.LeaveTransaction;
import com.lms.repository.DepartmentRepository;
import com.lms.repository.EmployeeRepository;
import com.lms.repository.LeaveRequestRepository;
import com.lms.repository.LeaveTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data initialization component to populate the database with sample data
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveTransactionRepository leaveTransactionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (departmentRepository.count() == 0) {
            initializeData();
        }
    }

    private void initializeData() {
        // Create departments
        Department engineering = departmentRepository.save(new Department("Engineering", "Software Development and Engineering teams"));
        Department hr = departmentRepository.save(new Department("Human Resources", "HR operations and people management"));
        Department marketing = departmentRepository.save(new Department("Marketing", "Marketing and brand management"));
        Department finance = departmentRepository.save(new Department("Finance", "Financial operations and accounting"));
        Department operations = departmentRepository.save(new Department("Operations", "Business operations and support"));

        // Create employees (managers first)
        Employee avi = createEmployee("Avi Patel", "avi.patel@company.com", "2023-01-15", engineering, null, 18, 10, 5);
        Employee sneha = createEmployee("Sneha Gupta", "sneha.gupta@company.com", "2022-12-01", hr, null, 15, 8, 4);
        Employee anita = createEmployee("Anita Verma", "anita.verma@company.com", "2023-01-20", marketing, null, 17, 9, 6);
        Employee deepak = createEmployee("Deepak Agarwal", "deepak.agarwal@company.com", "2022-11-15", finance, null, 16, 7, 5);
        Employee suresh = createEmployee("Suresh Reddy", "suresh.reddy@company.com", "2023-02-15", operations, null, 19, 8, 6);

        // Create team members
        Employee rahul = createEmployee("Rahul Sharma", "rahul.sharma@company.com", "2023-02-01", engineering, avi, 21, 8, 7);
        Employee priya = createEmployee("Priya Singh", "priya.singh@company.com", "2023-03-10", engineering, avi, 19, 10, 6);
        Employee amit = createEmployee("Amit Kumar", "amit.kumar@company.com", "2023-04-05", engineering, avi, 21, 9, 7);
        Employee vikash = createEmployee("Vikash Yadav", "vikash.yadav@company.com", "2023-05-15", hr, sneha, 20, 10, 7);
        Employee rajesh = createEmployee("Rajesh Bhatt", "rajesh.bhatt@company.com", "2023-06-01", marketing, anita, 21, 10, 7);
        Employee kavya = createEmployee("Kavya Nair", "kavya.nair@company.com", "2023-07-10", finance, deepak, 21, 10, 7);
        Employee meera = createEmployee("Meera Shah", "meera.shah@company.com", "2023-08-01", operations, suresh, 21, 10, 7);

        // Create sample leave requests
        createSampleLeaveRequests(rahul, priya, amit, vikash);

        System.out.println("✅ Sample data initialized successfully!");
        System.out.println("📊 Created " + departmentRepository.count() + " departments");
        System.out.println("👥 Created " + employeeRepository.count() + " employees");
        System.out.println("📋 Created " + leaveRequestRepository.count() + " leave requests");
    }

    private Employee createEmployee(String name, String email, String joiningDate, Department department, Employee manager,
                                  int annual, int sick, int casual) {
        Employee employee = new Employee(name, email, LocalDate.parse(joiningDate), department);
        employee.setManager(manager);
        employee.setAnnualLeaveBalance(annual);
        employee.setSickLeaveBalance(sick);
        employee.setCasualLeaveBalance(casual);
        return employeeRepository.save(employee);
    }

    private void createSampleLeaveRequests(Employee rahul, Employee priya, Employee amit, Employee vikash) {
        // Pending requests
        LeaveRequest leave1 = new LeaveRequest(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19),
                LeaveRequest.LeaveType.ANNUAL, "Family vacation planned for new year", rahul);
        leaveRequestRepository.save(leave1);

        LeaveRequest leave2 = new LeaveRequest(LocalDate.of(2024, 1, 22), LocalDate.of(2024, 1, 24),
                LeaveRequest.LeaveType.SICK, "Doctor appointment and recovery", priya);
        leaveRequestRepository.save(leave2);

        LeaveRequest leave3 = new LeaveRequest(LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 9),
                LeaveRequest.LeaveType.ANNUAL, "Wedding ceremony to attend", amit);
        leaveRequestRepository.save(leave3);

        LeaveRequest leave4 = new LeaveRequest(LocalDate.of(2024, 1, 18), LocalDate.of(2024, 1, 18),
                LeaveRequest.LeaveType.CASUAL, "Personal work", vikash);
        leaveRequestRepository.save(leave4);

        // Approved request
        LeaveRequest approvedLeave = new LeaveRequest(LocalDate.of(2023, 12, 20), LocalDate.of(2023, 12, 22),
                LeaveRequest.LeaveType.ANNUAL, "Year end vacation", rahul);
        approvedLeave.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        approvedLeave.setApprovedBy(rahul.getManager());
        approvedLeave.setApprovedAt(LocalDateTime.now().minusDays(8));
        approvedLeave.setApproverComments("Approved for year-end break");
        leaveRequestRepository.save(approvedLeave);

        // Create transaction logs
        createTransaction(LeaveTransaction.TransactionType.LEAVE_APPLIED, 
                "Leave application submitted for 5 days", leave1, rahul);
        createTransaction(LeaveTransaction.TransactionType.LEAVE_APPLIED, 
                "Leave application submitted for 3 days", leave2, priya);
        createTransaction(LeaveTransaction.TransactionType.LEAVE_APPROVED,
                "Leave request approved: Approved for year-end break", approvedLeave, rahul.getManager());
    }

    private void createTransaction(LeaveTransaction.TransactionType type, String description,
                                 LeaveRequest leaveRequest, Employee performedBy) {
        LeaveTransaction transaction = new LeaveTransaction(type, description, leaveRequest, performedBy);
        leaveTransactionRepository.save(transaction);
    }
}