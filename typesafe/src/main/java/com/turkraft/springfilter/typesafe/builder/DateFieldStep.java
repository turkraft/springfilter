package com.turkraft.springfilter.typesafe.builder;

import com.turkraft.springfilter.parser.node.FilterNode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class DateFieldStep<P extends FilterChain<P>> {

  private final P chain;
  private final String path;

  public DateFieldStep(P chain, String path) {
    this.chain = chain;
    this.path = path;
  }

  public P equal(LocalDate value) {
    return apply(chain.fb.field(path).equal(chain.fb.input(value)).get());
  }

  public P equal(LocalDateTime value) {
    return apply(chain.fb.field(path).equal(chain.fb.input(value)).get());
  }

  public P equal(Date value) {
    return apply(chain.fb.field(path).equal(chain.fb.input(value)).get());
  }

  public P notEqual(LocalDate value) {
    return apply(chain.fb.field(path).notEqual(chain.fb.input(value)).get());
  }

  public P notEqual(LocalDateTime value) {
    return apply(chain.fb.field(path).notEqual(chain.fb.input(value)).get());
  }

  public P notEqual(Date value) {
    return apply(chain.fb.field(path).notEqual(chain.fb.input(value)).get());
  }

  public P greaterThan(LocalDate value) {
    return apply(chain.fb.field(path).greaterThan(chain.fb.input(value)).get());
  }

  public P greaterThan(LocalDateTime value) {
    return apply(chain.fb.field(path).greaterThan(chain.fb.input(value)).get());
  }

  public P greaterThan(Date value) {
    return apply(chain.fb.field(path).greaterThan(chain.fb.input(value)).get());
  }

  public P greaterThanOrEqual(LocalDate value) {
    return apply(chain.fb.field(path).greaterThanOrEqual(chain.fb.input(value)).get());
  }

  public P greaterThanOrEqual(LocalDateTime value) {
    return apply(chain.fb.field(path).greaterThanOrEqual(chain.fb.input(value)).get());
  }

  public P greaterThanOrEqual(Date value) {
    return apply(chain.fb.field(path).greaterThanOrEqual(chain.fb.input(value)).get());
  }

  public P lessThan(LocalDate value) {
    return apply(chain.fb.field(path).lessThan(chain.fb.input(value)).get());
  }

  public P lessThan(LocalDateTime value) {
    return apply(chain.fb.field(path).lessThan(chain.fb.input(value)).get());
  }

  public P lessThan(Date value) {
    return apply(chain.fb.field(path).lessThan(chain.fb.input(value)).get());
  }

  public P lessThanOrEqual(LocalDate value) {
    return apply(chain.fb.field(path).lessThanOrEqual(chain.fb.input(value)).get());
  }

  public P lessThanOrEqual(LocalDateTime value) {
    return apply(chain.fb.field(path).lessThanOrEqual(chain.fb.input(value)).get());
  }

  public P lessThanOrEqual(Date value) {
    return apply(chain.fb.field(path).lessThanOrEqual(chain.fb.input(value)).get());
  }

  public P between(LocalDate lower, LocalDate upper) {
    return apply(chain.fb.field(path).between(chain.fb.input(lower), chain.fb.input(upper)).get());
  }

  public P between(LocalDateTime lower, LocalDateTime upper) {
    return apply(chain.fb.field(path).between(chain.fb.input(lower), chain.fb.input(upper)).get());
  }

  public P between(Date lower, Date upper) {
    return apply(chain.fb.field(path).between(chain.fb.input(lower), chain.fb.input(upper)).get());
  }

  public P isNull() {
    return apply(chain.fb.field(path).isNull().get());
  }

  public P isNotNull() {
    return apply(chain.fb.field(path).isNotNull().get());
  }

  private P apply(FilterNode condition) {
    chain.apply(condition);
    return chain;
  }

}
