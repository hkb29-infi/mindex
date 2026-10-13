package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompensationServiceImplUnitTest {

    @Mock
    private CompensationRepository compensationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private CompensationServiceImpl compensationService;

    @Test
    public void createRejectsCompensationForUnknownEmployee() {
        Compensation compensation = new Compensation(99999, LocalDate.now(), "unknown");
        when(employeeRepository.findByEmployeeId("unknown")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> compensationService.create(compensation));

        verify(compensationRepository, never()).insert(compensation);
    }
}
