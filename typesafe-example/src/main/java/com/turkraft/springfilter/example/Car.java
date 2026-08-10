package com.turkraft.springfilter.example;

import com.turkraft.springfilter.typesafe.Filterable;

@Filterable
public class Car {

  private int year;
  private String model;
  private double price;
  private boolean sold;

  public Car(int year, String model, double price, boolean sold) {
    this.year = year;
    this.model = model;
    this.price = price;
    this.sold = sold;
  }

  public int getYear() {
    return year;
  }

  public String getModel() {
    return model;
  }

  public double getPrice() {
    return price;
  }

  public boolean isSold() {
    return sold;
  }

}
