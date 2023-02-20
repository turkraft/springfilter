package com.turkraft.springfilter.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table
public class Industry {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String name;

  @JsonIgnoreProperties({"employees", "industry"})
  @OneToMany(mappedBy = "industry")
  private List<Company> companies;

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

  public List<Company> getCompanies() {
    return companies;
  }

  public void setCompanies(List<Company> companies) {
    this.companies = companies;
  }

}
