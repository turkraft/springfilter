package com.turkraft.springfilter.pagesort;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.util.AntPathMatcher;

public class AntPathPropertyFilter extends SimpleBeanPropertyFilter {

  private static final AntPathMatcher MATCHER = new AntPathMatcher(".");

  protected final Set<String> propertiesToInclude;
  protected final Set<String> propertiesToExclude;
  private final Map<String, Boolean> matchCache;

  public AntPathPropertyFilter(FieldsExpression fields) {
    Objects.requireNonNull(fields, "fields must not be null");
    this.propertiesToInclude = fields.getIncludePatterns();
    this.propertiesToExclude = fields.getExcludePatterns();
    this.matchCache = new HashMap<>();
  }

  private String getPathToTest(PropertyWriter writer, JsonGenerator generator) {
    StringBuilder nestedPath = new StringBuilder();
    nestedPath.append(writer.getName());
    JsonStreamContext sc = generator.getOutputContext();
    if (sc != null) {
      sc = sc.getParent();
    }
    while (sc != null) {
      if (sc.getCurrentName() != null) {
        if (nestedPath.length() > 0) {
          nestedPath.insert(0, ".");
        }
        nestedPath.insert(0, sc.getCurrentName());
      }
      sc = sc.getParent();
    }
    return nestedPath.toString();
  }

  @Override
  protected boolean include(BeanPropertyWriter writer) {
    throw new UnsupportedOperationException("Cannot call include without JsonGenerator");
  }

  @Override
  protected boolean include(PropertyWriter writer) {
    throw new UnsupportedOperationException("Cannot call include without JsonGenerator");
  }

  protected boolean include(PropertyWriter writer, JsonGenerator generator) {

    String pathToTest = getPathToTest(writer, generator);

    if (matchCache.containsKey(pathToTest)) {
      return matchCache.get(pathToTest);
    }

    if (propertiesToInclude.isEmpty()) {
      for (String pattern : propertiesToExclude) {
        if (onlyExcludesMatchPath(pathToTest, pattern)) {
          matchCache.put(pathToTest, false);
          return false;
        }
      }
      matchCache.put(pathToTest, true);
      return true;
    }

    boolean include = false;
    for (String pattern : propertiesToInclude) {
      if (includesMatchPath(pathToTest, pattern)) {
        include = true;
        break;
      }
    }

    if (include && !propertiesToExclude.isEmpty()) {
      for (String pattern : propertiesToExclude) {
        if (excludesMatchPath(pathToTest, pattern)) {
          include = false;
          break;
        }
      }
    }

    matchCache.put(pathToTest, include);
    return include;

  }

  private boolean onlyExcludesMatchPath(String pathToTest, String pattern) {
    if (pattern.contains("*")) {
      return MATCHER.match(pattern, pathToTest);
    } else {
      return pattern.equals(pathToTest);
    }
  }

  private boolean excludesMatchPath(String pathToTest, String pattern) {
    if (pattern.contains("*")) {
      return MATCHER.match(pattern, pathToTest);
    } else {
      return pattern.equals(pathToTest)
          || pathToTest.startsWith(pattern + ".");
    }
  }

  private boolean includesMatchPath(String pathToTest, String pattern) {
    if (pattern.equals("*")) {
      return true;
    }
    if (pattern.contains("*")) {
      if (MATCHER.match(pattern, pathToTest)) {
        return true;
      }
      String patternPrefix = pattern.substring(0, pattern.indexOf('*'));
      if (patternPrefix.endsWith(".")) {
        patternPrefix = patternPrefix.substring(0, patternPrefix.length() - 1);
      }
      return !patternPrefix.isEmpty() && (patternPrefix.equals(pathToTest)
          || patternPrefix.startsWith(pathToTest + "."));
    } else {
      return pattern.equals(pathToTest) || pattern.startsWith(pathToTest + ".")
          || pathToTest.startsWith(pattern + ".");
    }
  }

  @Override
  public void serializeAsField(Object pojo, JsonGenerator jgen,
      SerializerProvider provider,
      PropertyWriter writer)
      throws Exception {
    if (include(writer, jgen)) {
      writer.serializeAsField(pojo, jgen, provider);
    } else if (!jgen.canOmitFields()) {
      writer.serializeAsOmittedField(pojo, jgen, provider);
    }
  }

}
