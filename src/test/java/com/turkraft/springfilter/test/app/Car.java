package com.turkraft.springfilter.test.app;


import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;

@Entity
@Table
@Data
@Builder
public class Car {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private Integer year;

  private Integer km;

  @Enumerated
  private Color color;

  @ManyToOne
  private Brand brand;

  @OneToMany
  private List<Accident> accidents;

  @ElementCollection
  private List<Double> ratings;

}
