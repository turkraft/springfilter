package com.turkraft.springfilter.pagesort;

public enum SortDirection {

  ASC("asc", "ascending"),
  DESC("desc", "descending");

  private final String shortName;
  private final String longName;

  SortDirection(String shortName, String longName) {
    this.shortName = shortName;
    this.longName = longName;
  }

  public String getShortName() {
    return shortName;
  }

  public String getLongName() {
    return longName;
  }

  public static SortDirection parse(String value) {

    if (value == null || value.trim().isEmpty()) {
      return ASC;
    }

    String normalized = value.trim().toLowerCase();

    return switch (normalized) {
      case "asc", "ascending" -> ASC;
      case "desc", "descending" -> DESC;
      default -> throw new IllegalArgumentException(
          "Invalid sort direction: " + value + ". Expected: asc, desc, ascending, or descending");
    };

  }

  public org.springframework.data.domain.Sort.Direction toSpringDirection() {
    return this == ASC
        ? org.springframework.data.domain.Sort.Direction.ASC
        : org.springframework.data.domain.Sort.Direction.DESC;
  }

}
