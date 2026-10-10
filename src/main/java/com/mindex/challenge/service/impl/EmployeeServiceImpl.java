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
        HashSet<String> visited = new HashSet<>();
        visited.add(employeeId);

        Employee employee = read(employeeId);
        int numberOfReports = recursiveNumReports(employee, visited);
        return new ReportingStructure(employee, numberOfReports);
    }

    public int recursiveNumReports(Employee employee, HashSet<String> visited){
        if (employee == null) {
            return 0;
        }

        Employee originalEmployee = read(employee.getEmployeeId());

        List<Employee> directReports = originalEmployee.getDirectReports();
        if (directReports == null || directReports.isEmpty()) {
            return 0;
        }

        int totalReports = 0;
        for(int i = 0; i < directReports.size(); i++){
            Employee directReport = directReports.get(i);
            if (directReport == null || directReport.getEmployeeId() == null
                    || directReport.getEmployeeId().isBlank()) {
                throw new IllegalStateException("Direct report is missing an employeeId");
            }

            if(visited.contains(directReport.getEmployeeId())){
                continue;
            }
            visited.add(directReport.getEmployeeId());
            totalReports += 1 + recursiveNumReports(directReport, visited);
        }
        return totalReports;
    }
}
