package com.turkraft.springfilter.jpa;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;

@Entity
@Table
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String firstName;

  private String lastName;

  @Temporal(TemporalType.DATE)
  private Date birthDate;

  public MaritalStatus maritalStatus;

  public Integer salary;

  @JsonIgnoreProperties("employees")
  @ManyToOne
  private Company company;

  @JsonIgnoreProperties({"company", "manager", "staff", "payslips"})
  @ManyToOne
  private Employee manager;

  @JsonIgnoreProperties({"company", "manager", "staff", "payslips"})
  @OneToMany(mappedBy = "manager")
  private List<Employee> staff;

  @JsonIgnoreProperties({"employee"})
  // TODO: eager fetching is currently used to prevent lazy initialization exception,
  // @Transactional seems to not work in tests...
  @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER)
  private List<Payslip> payslips;

  public enum MaritalStatus {

    UNKNOWN, MARRIED, WIDOWED, DIVORCED, SINGLE, SEPARATED;

    @JsonValue
    @Override
    public String toString() {
      return name().toLowerCase();
    }

  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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

}
