package com.turkraft.springfilter.jpa;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String name;

  @JsonIgnoreProperties("companies")
  @ManyToOne
  private Industry industry;

  @JsonIgnoreProperties({"company", "manager", "staff", "payslips"})
  @OneToMany(mappedBy = "company")
  private List<Employee> employees;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Industry getIndustry() {
    return industry;
  }

  public void setIndustry(Industry industry) {
    this.industry = industry;
  }

  public List<Employee> getEmployees() {
    return employees;
  }

  public void setEmployees(List<Employee> employees) {
    this.employees = employees;
  }

}
