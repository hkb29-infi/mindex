package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest
public class CompensationServiceImplTest {

    @Autowired
    private CompensationService compensationService;

    @Autowired
    private EmployeeService employeeService;

    @Test
    public void testCreateAndReadCompensation() {
        Employee employee = new Employee();
        employee.setFirstName("Compensation");
        employee.setLastName("Test");
        Employee createdEmployee = employeeService.create(employee);

        Compensation compensation = new Compensation(99999, LocalDate.now(),
                createdEmployee.getEmployeeId());
        compensationService.create(compensation);

        Compensation readCompensation = compensationService.read(createdEmployee.getEmployeeId());

        assertNotNull(readCompensation);
        assertEquals(compensation.getEmployeeId(), readCompensation.getEmployeeId());
        assertEquals(compensation.getSalary(), readCompensation.getSalary());
        assertEquals(compensation.getEffectiveDate(), readCompensation.getEffectiveDate());
    }

    @Test
    public void testCreateRejectsCompensationForUnknownEmployee() {
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
}
