package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    @Test
    public void testReportingStructure(){
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        int numberOfReports = 4;

        ReportingStructure reportingStructure = new ReportingStructure(testEmployee, 4);

        assertEmployeeEquivalence(testEmployee, reportingStructure.getEmployee());

        assertEquals(numberOfReports, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testGetNumReportsForJohn() {
        String johnsEmployeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        ReportingStructure reportingStructure = employeeService.getNumReports(johnsEmployeeId);

        assertEquals(johnsEmployeeId, reportingStructure.getEmployee().getEmployeeId());
        assertEquals(4, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testSetReportingStructure(){
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        int numberOfReports = 4;

        ReportingStructure reportingStructure = new ReportingStructure(new Employee(), numberOfReports);

        assertEquals(numberOfReports, reportingStructure.getNumberOfReports());

        reportingStructure.setEmployee(testEmployee);

        assertEmployeeEquivalence(testEmployee, reportingStructure.getEmployee());

        reportingStructure.setNumberOfReports(100000);

        assertEquals(100000, reportingStructure.getNumberOfReports());

    }

    @Test
    public void testCreateAndReadCompensation() {
        Employee employee = new Employee();
        employee.setFirstName("Compensation");
        employee.setLastName("Test");
        Employee createdEmployee = employeeService.create(employee);

        Compensation compensation = new Compensation(99999, java.time.LocalDate.now(),
                createdEmployee.getEmployeeId());
        compensationService.create(compensation);

        Compensation readCompensation = compensationService.read(createdEmployee.getEmployeeId());

        assertNotNull(readCompensation);
        assertEquals(compensation.getEmployeeId(), readCompensation.getEmployeeId());
        assertEquals(compensation.getSalary(), readCompensation.getSalary());
        assertEquals(compensation.getEffectiveDate(), readCompensation.getEffectiveDate());
    }

    @Test
    public void testGetNumReportsCountsHierarchyWithMockedRepository() {
        Employee john = employeeWithReports("john", "paul", "ringo");
        Employee paul = employeeWithReports("paul");
        Employee ringo = employeeWithReports("ringo", "pete", "george");
        Employee pete = employeeWithReports("pete");
        Employee george = employeeWithReports("george");

        EmployeeRepository repository = mock(EmployeeRepository.class);
        when(repository.findByEmployeeId("john")).thenReturn(john);
        when(repository.findByEmployeeId("paul")).thenReturn(paul);
        when(repository.findByEmployeeId("ringo")).thenReturn(ringo);
        when(repository.findByEmployeeId("pete")).thenReturn(pete);
        when(repository.findByEmployeeId("george")).thenReturn(george);

        EmployeeServiceImpl service = new EmployeeServiceImpl();
        ReflectionTestUtils.setField(service, "employeeRepository", repository);

        ReportingStructure result = service.getNumReports("john");

        assertEquals(4, result.getNumberOfReports());
    }

    @Test
    public void testCreateCompensationRejectsUnknownEmployeeWithMockedRepositories() {
        Compensation compensation = new Compensation(99999, LocalDate.now(), "unknown");
        EmployeeRepository employees = mock(EmployeeRepository.class);
        CompensationRepository compensations = mock(CompensationRepository.class);
        when(employees.findByEmployeeId("unknown")).thenReturn(null);

        CompensationServiceImpl service = new CompensationServiceImpl();
        ReflectionTestUtils.setField(service, "employeeRepository", employees);
        ReflectionTestUtils.setField(service, "compensationRepository", compensations);

        assertThrows(IllegalArgumentException.class, () -> service.create(compensation));
        verify(compensations, never()).insert(compensation);
    }

    private static Employee employeeWithReports(String id, String... reportIds) {
        Employee employee = new Employee();
        employee.setEmployeeId(id);

        java.util.List<Employee> reports = new java.util.ArrayList<>();
        for (String reportId : reportIds) {
            Employee reportReference = new Employee();
            reportReference.setEmployeeId(reportId);
            reports.add(reportReference);
        }
        employee.setDirectReports(reports);

        return employee;
    }
}
