package com.mindex.challenge.data;

public class ReportingStructure{

    private Employee employee;

    // by default it is this value
    private int numberOfReports = 0;

    public ReportingStructure(Employee e, int numberOfReports){
        this.employee = e;
        this.numberOfReports = numberOfReports;
    }

    public int getNumberOfReports(){
        return numberOfReports;
    }

    public void setNumberOfReports(int numberOfReports){
        this.numberOfReports = numberOfReports;
    }

    public Employee getEmployee(){
        return this.employee;
    }

    public void setEmployee(Employee employee){
        this.employee = employee;
    }


}