package com.turkraft.springfilter.pagesort;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FieldsExpression {

  private static final Pattern FIELD_PATTERN =
      Pattern.compile("^-?!?[a-zA-Z_*][a-zA-Z0-9_.*]*$");

  private final String[] patterns;
  private final Set<String> includePatterns;
  private final Set<String> excludePatterns;

  public FieldsExpression(String expression) {
    if (expression == null || expression.trim().isEmpty()) {
      this.patterns = new String[0];
      this.includePatterns = Collections.emptySet();
      this.excludePatterns = Collections.emptySet();
      return;
    }

    String[] parsed = parsePatterns(expression);
    if (parsed.length == 0) {
      this.patterns = new String[0];
      this.includePatterns = Collections.emptySet();
      this.excludePatterns = Collections.emptySet();
      return;
    }

    this.patterns = parsed;

    Set<String> includes = new HashSet<>();
    Set<String> excludes = new HashSet<>();

    for (String pattern : parsed) {
      if (pattern.startsWith("-") || pattern.startsWith("!")) {
        excludes.add(pattern.substring(1));
      } else {
        includes.add(pattern);
      }
    }

    this.includePatterns = Collections.unmodifiableSet(includes);
    this.excludePatterns = Collections.unmodifiableSet(excludes);
  }

  public FieldsExpression(String... patterns) {
    Objects.requireNonNull(patterns, "patterns must not be null");
    this.patterns = Arrays.stream(patterns)
        .filter(p -> p != null && !p.trim().isEmpty())
        .map(String::trim)
        .toArray(String[]::new);

    Set<String> includes = new HashSet<>();
    Set<String> excludes = new HashSet<>();

    for (String pattern : this.patterns) {
      validatePattern(pattern);
      if (pattern.startsWith("-") || pattern.startsWith("!")) {
        excludes.add(pattern.substring(1));
      } else {
        includes.add(pattern);
      }
    }

    this.includePatterns = Collections.unmodifiableSet(includes);
    this.excludePatterns = Collections.unmodifiableSet(excludes);
  }

  private String[] parsePatterns(String expression) {
    String[] parts = expression.split(",");
    String[] result = new String[parts.length];
    int index = 0;
    for (String part : parts) {
      String trimmed = part.trim();
      if (!trimmed.isEmpty()) {
        validatePattern(trimmed);
        result[index++] = trimmed;
      }
    }
    return Arrays.copyOf(result, index);
  }

  private void validatePattern(String pattern) {
    if (pattern == null || pattern.isEmpty()) {
      throw new FieldsParseException("Pattern cannot be null or empty");
    }

    if (!FIELD_PATTERN.matcher(pattern).matches()) {
      throw new FieldsParseException("Invalid field pattern: " + pattern);
    }
  }

  public String[] getPatterns() {
    return Arrays.copyOf(patterns, patterns.length);
  }

  public Set<String> getIncludePatterns() {
    return includePatterns;
  }

  public Set<String> getExcludePatterns() {
    return excludePatterns;
  }

  public boolean isEmpty() {
    return patterns.length == 0;
  }

  public int size() {
    return patterns.length;
  }

  public boolean hasIncludes() {
    return !includePatterns.isEmpty();
  }

  public boolean hasExcludes() {
    return !excludePatterns.isEmpty();
  }

  public static FieldsExpression empty() {
    return new FieldsExpression((String) null);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FieldsExpression that = (FieldsExpression) o;
    return Arrays.equals(patterns, that.patterns);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(patterns);
  }

  @Override
  public String toString() {
    if (isEmpty()) {
      return "empty";
    }
    return Arrays.stream(patterns).collect(Collectors.joining(", "));
  }

}
