package com.turkraft.springfilter.pagesort;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SimpleSortParser implements SortParser {

  private static final Pattern FIELD_PATH_PATTERN =
      Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*$");

  @Override
  public SortExpression parse(String expression)
      throws SortParseException {
    return parse(expression, Integer.MAX_VALUE);
  }

  @Override
  public SortExpression parse(String expression, int maxFields)
      throws SortParseException {

    if (expression == null || expression
        .trim()
        .isEmpty()) {
      return SortExpression.unsorted();
    }

    String normalized = normalizeExpression(expression);
    if (normalized.isEmpty()) {
      return SortExpression.unsorted();
    }

    String[] parts = normalized.split(",");

    if (parts.length > maxFields) {
      throw new SortParseException(
          "Too many sort fields: " + parts.length + " (max: " + maxFields + ")");
    }

    List<SortField> fields = new ArrayList<>();

    for (int i = 0; i < parts.length; i++) {
      String part = parts[i].trim();
      if (part.isEmpty()) {
        throw new SortParseException("Empty sort field at position " + i);
      }
      fields.add(parseSingleField(part, i));
    }

    return new SortExpression(fields);

  }

  private String normalizeExpression(String expression) {
    return expression
        .replaceAll("\\s+", "")
        .trim();
  }

  private SortField parseSingleField(String fieldExpr, int position)
      throws SortParseException {

    if (fieldExpr == null || fieldExpr.isEmpty()) {
      throw new SortParseException("Invalid sort field at position " + position);
    }

    String fieldPath;
    SortDirection direction;

    if (fieldExpr.startsWith("-")) {
      direction = SortDirection.DESC;
      fieldPath = fieldExpr.substring(1);
    } else {
      direction = SortDirection.ASC;
      fieldPath = fieldExpr;
    }

    validateFieldPath(fieldPath, position);

    return new SortField(fieldPath, direction);

  }

  private void validateFieldPath(String fieldPath, int position)
      throws SortParseException {

    if (fieldPath == null || fieldPath.isEmpty()) {
      throw new SortParseException("Empty field path at position " + position);
    }

    if (fieldPath.startsWith(".")) {
      throw new SortParseException(
          "Field path cannot start with '.' at position " + position + ": " + fieldPath);
    }

    if (fieldPath.endsWith(".")) {
      throw new SortParseException(
          "Field path cannot end with '.' at position " + position + ": " + fieldPath);
    }

    if (fieldPath.contains("..")) {
      throw new SortParseException(
          "Field path cannot contain consecutive dots at position " + position + ": " + fieldPath);
    }

    if (!FIELD_PATH_PATTERN
        .matcher(fieldPath)
        .matches()) {
      throw new SortParseException(
          "Invalid field path at position " + position + ": " + fieldPath);
    }

  }

}
