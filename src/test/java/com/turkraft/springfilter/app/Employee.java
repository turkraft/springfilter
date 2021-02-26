package com.turkraft.springfilter.app;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String firstName;

  private String lastName;

  @Temporal(TemporalType.DATE)
  private Date birthDate;

  public MaritalStatus maritalStatus;

  public Integer salary;

  @JsonIgnoreProperties("employees")
  @ManyToOne
  private Company company;

  @JsonIgnoreProperties({"company", "manager", "staff", "payslips"})
  @ManyToOne
  private Employee manager;

  @JsonIgnoreProperties({"company", "manager", "staff", "payslips"})
  @OneToMany(mappedBy = "manager")
  private List<Employee> staff;

  @JsonIgnoreProperties({"employee"})
  // TODO: eager fetching is currently used to prevent lazy initialization exception,
  // @Transactional seems to not work in tests...
  @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER)
  private List<Payslip> payslips;

  public enum MaritalStatus {

    UNKNOWN, MARRIED, WIDOWED, DIVORCED, SINGLE, SEPARATED;

    @JsonValue
    @Override
    public String toString() {
      return name().toLowerCase();
    }

  }

}
