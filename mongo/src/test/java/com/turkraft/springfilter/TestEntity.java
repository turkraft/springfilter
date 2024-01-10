package com.turkraft.springfilter;

import java.util.List;
import org.springframework.data.annotation.Id;

public class TestEntity {

  @Id
  private String id;

  private String string;

  private List<Integer> integers;

  private int integer;

  private NestedTestEntity nested;

  public String getId() {
    return id;
  }

  public void setId(String id) {
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

  public NestedTestEntity getNested() {
    return nested;
  }

  public void setNested(NestedTestEntity nested) {
    this.nested = nested;
  }
  
}
