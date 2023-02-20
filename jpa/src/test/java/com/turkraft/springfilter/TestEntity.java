package com.turkraft.springfilter;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.List;

@Entity
public class TestEntity {

  @Id
  @GeneratedValue
  private Long id;

  private String string;

  @ElementCollection
  private List<Integer> integers;

  private int integer;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public List<Integer> getIntegers() {
    return integers;
  }

  public void setIntegers(List<Integer> integers) {
    this.integers = integers;
  }

  public int getInteger() {
    return integer;
  }

  public void setInteger(int integer) {
    this.integer = integer;
  }

}
