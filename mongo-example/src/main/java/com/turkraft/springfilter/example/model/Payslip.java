package com.turkraft.springfilter.example.model;

import java.util.Date;

public class Payslip {

  private Employee employee;

  private Date date;

  public Employee getEmployee() {
    return employee;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

}
