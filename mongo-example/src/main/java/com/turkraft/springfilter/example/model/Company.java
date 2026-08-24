package com.turkraft.springfilter.example.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Company {

  private String name;

  private Industry industry;

  private List<Employee> employees;

  private Map<String, String> websites;

  private Map<String, Integer> employeeCounts;

  private UUID refId;

  private Map<String, UUID> links;

  private Map<String, List<String>> tags;

  private HashMap<String, String> socialMedia;

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

  public Map<String, Integer> getEmployeeCounts() {
    return employeeCounts;
  }

  public void setEmployeeCounts(Map<String, Integer> employeeCounts) {
    this.employeeCounts = employeeCounts;
  }

  public UUID getRefId() {
    return refId;
  }

  public void setRefId(UUID refId) {
    this.refId = refId;
  }

  public Map<String, UUID> getLinks() {
    return links;
  }

  public void setLinks(Map<String, UUID> links) {
    this.links = links;
  }

  public Map<String, List<String>> getTags() {
    return tags;
  }

  public void setTags(Map<String, List<String>> tags) {
    this.tags = tags;
  }

  public HashMap<String, String> getSocialMedia() {
    return socialMedia;
  }

  public void setSocialMedia(HashMap<String, String> socialMedia) {
    this.socialMedia = socialMedia;
  }

}
