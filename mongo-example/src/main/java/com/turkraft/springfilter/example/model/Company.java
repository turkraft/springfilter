package com.turkraft.springfilter.example.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

public class Company {

  private String name;

  private Industry industry;

  private List<Employee> employees;

  @DBRef
  private Tag primaryTag;

  @DBRef
  private List<Tag> categories;

  @DocumentReference
  private Tag featuredTag;

  @DocumentReference
  private List<Tag> labels;

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

  public Tag getPrimaryTag() {
    return primaryTag;
  }

  public void setPrimaryTag(Tag primaryTag) {
    this.primaryTag = primaryTag;
  }

  public List<Tag> getCategories() {
    return categories;
  }

  public void setCategories(List<Tag> categories) {
    this.categories = categories;
  }

  public Tag getFeaturedTag() {
    return featuredTag;
  }

  public void setFeaturedTag(Tag featuredTag) {
    this.featuredTag = featuredTag;
  }

  public List<Tag> getLabels() {
    return labels;
  }

  public void setLabels(List<Tag> labels) {
    this.labels = labels;
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
