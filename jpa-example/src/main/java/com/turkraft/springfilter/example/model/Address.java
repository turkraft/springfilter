package com.turkraft.springfilter.example.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

  private String streetAndNumber;

  private String postalCode;

  private String city;

  private String country;

  public String getStreetAndNumber() {
    return streetAndNumber;
  }

  public void setStreetAndNumber(String streetAndNumber) {
    this.streetAndNumber = streetAndNumber;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

}
