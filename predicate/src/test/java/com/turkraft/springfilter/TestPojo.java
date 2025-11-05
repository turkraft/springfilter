package com.turkraft.springfilter;

import java.util.List;
import java.util.Map;

public class TestPojo {

  private String string;

  private List<Integer> integers;

  private int integer;

  private Map<String, Object> metadata;

  private NestedPojo nested;

  public TestPojo(String string, List<Integer> integers, int integer) {
    this.string = string;
    this.integers = integers;
    this.integer = integer;
  }

  public TestPojo(String string, List<Integer> integers, int integer, Map<String, Object> metadata,
      NestedPojo nested) {
    this.string = string;
    this.integers = integers;
    this.integer = integer;
    this.metadata = metadata;
    this.nested = nested;
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

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public NestedPojo getNested() {
    return nested;
  }

  public void setNested(NestedPojo nested) {
    this.nested = nested;
  }

  public static class NestedPojo {

    private String name;

    private int value;

    public NestedPojo(String name, int value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }

  }

}
