package com.turkraft.springfilter.example.model;

import org.springframework.data.annotation.Id;

public class Tag {

  @Id
  private String id;

  private String label;

  public Tag() {
  }

  public Tag(String id, String label) {
    this.id = id;
    this.label = label;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

}
