package com.turkraft.springfilter.example.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;

public class Employee {

  @Id
  private String id;

  private String firstName;

  private String lastName;

  private Date birthDate;

  public MaritalStatus maritalStatus;

  public Integer salary;

  private Company company;

  private Employee manager;

  private List<Employee> staff;

  private List<Payslip> payslips;

  private List<String> children;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public enum MaritalStatus {

    UNKNOWN, MARRIED, WIDOWED, DIVORCED, SINGLE, SEPARATED;

    @JsonValue
    @Override
    public String toString() {
      return name().toLowerCase();
    }

  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public MaritalStatus getMaritalStatus() {
    return maritalStatus;
  }

  public void setMaritalStatus(MaritalStatus maritalStatus) {
    this.maritalStatus = maritalStatus;
  }

  public Integer getSalary() {
    return salary;
  }

  public void setSalary(Integer salary) {
    this.salary = salary;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public Employee getManager() {
    return manager;
  }

  public void setManager(Employee manager) {
    this.manager = manager;
  }

  public List<Employee> getStaff() {
    return staff;
  }

  public void setStaff(List<Employee> staff) {
    this.staff = staff;
  }

  public List<Payslip> getPayslips() {
    return payslips;
  }

  public void setPayslips(List<Payslip> payslips) {
    this.payslips = payslips;
  }

  public List<String> getChildren() {
    return children;
  }

  public void setChildren(List<String> children) {
    this.children = children;
  }

}
