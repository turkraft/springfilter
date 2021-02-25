package com.turkraft.springfilter.test.app;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String name;

  @ManyToOne
  private Industry industry;

  @OneToMany(mappedBy = "company")
  private List<Employee> employees;

}
