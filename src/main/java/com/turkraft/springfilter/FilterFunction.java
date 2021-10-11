package com.turkraft.springfilter;

public class FilterFunction {

  private String name;

  private Class<?>[] inputTypes;

  private Class<?> outputType;

  public FilterFunction(String name, Class<?>[] inputTypes, Class<?> outputType) {
    this.name = name;
    this.inputTypes = inputTypes;
    this.outputType = outputType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Class<?>[] getInputTypes() {
    return inputTypes;
  }

  public void setInputTypes(Class<?>[] inputTypes) {
    this.inputTypes = inputTypes;
  }

  public Class<?> getOutputType() {
    return outputType;
  }

  public void setOutputType(Class<?> outputType) {
    this.outputType = outputType;
  }

}
