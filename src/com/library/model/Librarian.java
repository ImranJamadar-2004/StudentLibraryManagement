package com.library.model;

public class Librarian extends User {
    private String employeeCode;

    public Librarian(int id, String name, String email, String employeeCode) {
        super(id, name, email);
        this.employeeCode = employeeCode;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    @Override
    public void displayDetails() {
        System.out.println("----------------------------------------");
        System.out.println("Librarian ID  : " + id);
        System.out.println("Name          : " + name);
        System.out.println("Email         : " + email);
        System.out.println("Employee Code : " + employeeCode);
        System.out.println("----------------------------------------");
    }
}
