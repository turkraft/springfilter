package com.turkraft.springfilter.transformer;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Selection;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ListExpression<T> implements Expression<T>, ConvertibleExpression {

  private List<Expression<T>> values;

  public ListExpression(List<Expression<T>> values) {
    this.values = values;
  }

  public List<Expression<T>> getValues() {
    return values;
  }

  public void setValues(List<Expression<T>> values) {
    this.values = values;
  }

  @Override
  public Predicate isNull() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Predicate isNotNull() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Predicate in(Object... values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Predicate in(Expression<?>... values) {
    return null;
  }

  @Override
  public Predicate in(Collection<?> values) {
    return null;
  }

  @Override
  public Predicate in(Expression<Collection<?>> values) {
    return null;
  }

  @Override
  public <X> Expression<X> as(Class<X> type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Selection<T> alias(String name) {
    return null;
  }

  @Override
  public boolean isCompoundSelection() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Selection<?>> getCompoundSelectionItems() {
    return null;
  }

  @Override
  public Class<? extends T> getJavaType() {
    return null;
  }

  @Override
  public String getAlias() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Expression<T> convertTo(Class<?> type) {
    return new ListExpression(values.stream().map(
        v -> v != null && v.getJavaType() != null && !type.isAssignableFrom(v.getJavaType()) ? v.as(
            type) : v).collect(
        Collectors.toList()));
  }

}
