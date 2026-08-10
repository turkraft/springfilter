package com.turkraft.springfilter.typesafe;

@Filterable
public class TestEntity {

  private int year;
  private String model;
  private double price;
  private boolean active;

  public int getYear() { return year; }
  public String getModel() { return model; }
  public double getPrice() { return price; }
  public boolean isActive() { return active; }

}
