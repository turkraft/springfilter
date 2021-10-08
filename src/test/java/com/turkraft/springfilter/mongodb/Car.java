package com.turkraft.springfilter.mongodb;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Car {

  @Id
  private String id;

  private String name;

  private int someNumber;

  private Date someDate;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getSomeNumber() {
    return someNumber;
  }

  public void setSomeNumber(int someNumber) {
    this.someNumber = someNumber;
  }

  public Date getSomeDate() {
    return someDate;
  }

  public void setSomeDate(Date someDate) {
    this.someDate = someDate;
  }

}
