package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }
    
    @Override
    public ReportingStructure getNumReports(String employeeId){
        Set<String> visited = new HashSet<>();
        visited.add(employeeId);

        Employee employee = read(employeeId);
        int numberOfReports = countReports(employee, visited);
        return new ReportingStructure(employee, numberOfReports);
    }

    private int countReports(Employee employee, Set<String> visited){
        List<Employee> directReports = employee.getDirectReports();
        if (directReports == null || directReports.isEmpty()) {
            return 0;
        }

        int totalReports = 0;
        for (Employee reportReference : directReports) {
            if (reportReference == null || reportReference.getEmployeeId() == null
                    || reportReference.getEmployeeId().isBlank()) {
                throw new IllegalStateException("Direct report is missing an employeeId");
            }

            String reportId = reportReference.getEmployeeId();
            if (!visited.add(reportId)) {
                continue;
            }

            Employee fullReport = read(reportId);
            totalReports += 1 + countReports(fullReport, visited);
        }
        return totalReports;
    }
}
