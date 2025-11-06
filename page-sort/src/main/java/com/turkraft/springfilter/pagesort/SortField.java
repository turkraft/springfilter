package com.turkraft.springfilter.pagesort;

import java.util.Objects;

public class SortField {

  private final String fieldPath;
  private final SortDirection direction;

  public SortField(String fieldPath, SortDirection direction) {
    if (fieldPath == null || fieldPath.trim().isEmpty()) {
      throw new IllegalArgumentException("Field path cannot be null or empty");
    }
    this.fieldPath = fieldPath.trim();
    this.direction = direction != null ? direction : SortDirection.ASC;
  }

  public SortField(String fieldPath) {
    this(fieldPath, SortDirection.ASC);
  }

  public String getFieldPath() {
    return fieldPath;
  }

  public SortDirection getDirection() {
    return direction;
  }

  public org.springframework.data.domain.Sort.Order toSpringOrder() {
    return new org.springframework.data.domain.Sort.Order(
        direction.toSpringDirection(),
        fieldPath);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SortField sortField = (SortField) o;
    return Objects.equals(fieldPath, sortField.fieldPath) && direction == sortField.direction;
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldPath, direction);
  }

  @Override
  public String toString() {
    return direction == SortDirection.DESC ? "-" + fieldPath : fieldPath;
  }

}
