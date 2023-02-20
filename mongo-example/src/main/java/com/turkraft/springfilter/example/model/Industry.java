package com.turkraft.springfilter.example.model;

import java.util.List;

public class Industry {

  private String name;

  private List<Company> companies;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Company> getCompanies() {
    return companies;
  }

  public void setCompanies(List<Company> companies) {
    this.companies = companies;
  }

}
