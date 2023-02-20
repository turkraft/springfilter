package com.turkraft.springfilter.example.model;

import java.util.List;
import java.util.Map;

public class Company {

  private String name;

  private Industry industry;

  private List<Employee> employees;

  private Map<String, String> websites;

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
