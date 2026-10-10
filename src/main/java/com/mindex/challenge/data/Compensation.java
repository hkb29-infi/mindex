package com.mindex.challenge.data;

import java.util.List;
import java.time.LocalDate;

public class Compensation {
    private int salary;
    private LocalDate effectiveDate;

    public Compensation(int salary, LocalDate effectiveDate) {
        this.salary = salary;
        this.effectiveDate = effectiveDate;
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

    public boolean isEffectiveDate() {
        return !effectiveDate.isAfter(LocalDate.now());
    }
}
