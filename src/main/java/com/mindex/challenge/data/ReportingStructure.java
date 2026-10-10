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
        // return recursiveNumReports(employee);
        return numberOfReports;
    }

    public Employee getEmployee(){
        return this.employee;
    }

    public void setEmployee(Employee employee){
        this.employee = employee;
    }

    // later we will add this logic to the service part of the API
    // public int recursiveNumReports(Employee employee){
    //     if (employee == null) {
    //         return 0;
    //     }

    //     List<Employee> directReports = employee.getDirectReports();
    //     if (directReports == null || directReports.isEmpty()) {
    //         return 0;
    //     }

    //     int totalReports = 0;
    //     for(int i = 0; i < directReports.size(); i++){
    //         totalReports += 1 + recursiveNumReports(directReports.get(i));
    //     }
    //     return totalReports;
    // }

    

}