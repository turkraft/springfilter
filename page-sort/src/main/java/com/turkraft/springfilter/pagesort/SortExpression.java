package com.turkraft.springfilter.pagesort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SortExpression {

  private final List<SortField> fields;

  public SortExpression(List<SortField> fields) {
    if (fields == null || fields.isEmpty()) {
      this.fields = Collections.emptyList();
    } else {
      this.fields = Collections.unmodifiableList(new ArrayList<>(fields));
    }
  }

  public SortExpression(SortField... fields) {
    this(fields != null ? List.of(fields) : Collections.emptyList());
  }

  public static SortExpression unsorted() {
    return new SortExpression(Collections.emptyList());
  }

  public List<SortField> getFields() {
    return fields;
  }

  public boolean isEmpty() {
    return fields.isEmpty();
  }

  public int size() {
    return fields.size();
  }

  public org.springframework.data.domain.Sort toSpringSort() {

    if (isEmpty()) {
      return org.springframework.data.domain.Sort.unsorted();
    }

    List<org.springframework.data.domain.Sort.Order> orders = fields.stream()
        .map(SortField::toSpringOrder)
        .collect(Collectors.toList());

    return org.springframework.data.domain.Sort.by(orders);

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SortExpression that = (SortExpression) o;
    return Objects.equals(fields, that.fields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fields);
  }

  @Override
  public String toString() {
    if (isEmpty()) {
      return "unsorted";
    }
    return fields.stream()
        .map(SortField::toString)
        .collect(Collectors.joining(","));
  }

}
