package com.turkraft.springfilter.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.List;

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

  @JsonIgnoreProperties({"company", "manager", "staff", "payslips", "children"})
  @ManyToOne
  private Employee manager;

  @JsonIgnoreProperties({"company", "manager", "staff", "payslips", "children"})
  @OneToMany(mappedBy = "manager")
  private List<Employee> staff;

  @JsonIgnoreProperties({"employee"})
  // TODO: eager fetching is currently used to prevent lazy initialization exception,
  // @Transactional seems to not work in tests...
  @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER)
  private List<Payslip> payslips;

  @ElementCollection
  private List<String> children;

  @Embedded
  private Address address;

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

  public List<String> getChildren() {
    return children;
  }

  public void setChildren(List<String> children) {
    this.children = children;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

}
