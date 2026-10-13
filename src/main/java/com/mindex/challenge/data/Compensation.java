package com.mindex.challenge.data;

import java.time.LocalDate;

public class Compensation {
    private int salary;
    private LocalDate effectiveDate;
    private String employeeId;

    public Compensation(){
    }

    public Compensation(int salary, LocalDate effectiveDate, String employeeId) {
        this.salary = salary;
        this.effectiveDate = effectiveDate;
        this.employeeId = employeeId;
    }

    public int getSalary(){
        return this.salary;
    }

    public void setSalary(int newSalary){
        this.salary = newSalary;
    }

    public LocalDate getEffectiveDate(){
        return this.effectiveDate;
    }

    public void setEffectiveDate(LocalDate newEffectiveDate){
        this.effectiveDate = newEffectiveDate;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public void setEmployeeId(String employeeId){
        this.employeeId = employeeId;
    }

    public boolean isCurrentlyEffective() {
        return !effectiveDate.isAfter(LocalDate.now());
    }
}
