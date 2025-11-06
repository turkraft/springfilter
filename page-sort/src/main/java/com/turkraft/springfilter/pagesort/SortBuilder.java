package com.turkraft.springfilter.pagesort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SortBuilder {

  private final List<SortField> fields = new ArrayList<>();
  private String currentField;
  private SortDirection currentDirection = SortDirection.ASC;

  public SortBuilder field(String fieldPath) {
    Objects.requireNonNull(fieldPath, "fieldPath must not be null");
    if (currentField != null) {
      fields.add(new SortField(currentField, currentDirection));
      currentDirection = SortDirection.ASC;
    }
    currentField = fieldPath;
    return this;
  }

  public SortBuilder asc() {
    currentDirection = SortDirection.ASC;
    return this;
  }

  public SortBuilder desc() {
    currentDirection = SortDirection.DESC;
    return this;
  }

  public SortBuilder direction(SortDirection direction) {
    this.currentDirection = Objects.requireNonNull(direction, "direction must not be null");
    return this;
  }

  public SortExpression build() {

    if (currentField != null) {
      fields.add(new SortField(currentField, currentDirection));
      currentField = null;
      currentDirection = SortDirection.ASC;
    }

    if (fields.isEmpty()) {
      return SortExpression.unsorted();
    }

    return new SortExpression(new ArrayList<>(fields));

  }

  public org.springframework.data.domain.Sort buildSpringSort() {
    return build().toSpringSort();
  }

  public void reset() {
    fields.clear();
    currentField = null;
    currentDirection = SortDirection.ASC;
  }

}
