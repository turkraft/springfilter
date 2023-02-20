package com.turkraft.springfilter.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Map;

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

  @JsonIgnoreProperties({"company", "manager", "staff", "payslips", "children"})
  @OneToMany(mappedBy = "company")
  private List<Employee> employees;

  @ElementCollection
  private Map<String, String> websites;

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

  public Map<String, String> getWebsites() {
    return websites;
  }

  public void setWebsites(Map<String, String> websites) {
    this.websites = websites;
  }
  
}
