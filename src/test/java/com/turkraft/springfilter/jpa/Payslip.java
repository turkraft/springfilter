package com.turkraft.springfilter.jpa;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table
public class Payslip {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @JsonIgnoreProperties({"company", "manager", "staff", "payslips"})
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
