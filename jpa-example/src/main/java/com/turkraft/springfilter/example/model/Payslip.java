package com.turkraft.springfilter.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Entity
@Table
public class Payslip {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @JsonIgnoreProperties({"company", "manager", "staff", "payslips", "children"})
  @ManyToOne
  private Employee employee;

  // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FilterConfig.DATE_FORMATTER.toPattern())
  @Temporal(TemporalType.DATE)
  private Date date;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

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
