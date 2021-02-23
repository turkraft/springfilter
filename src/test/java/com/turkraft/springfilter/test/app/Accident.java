package com.turkraft.springfilter.test.app;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;

@Table
@Entity
@Data
@Builder
public class Accident {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

}
