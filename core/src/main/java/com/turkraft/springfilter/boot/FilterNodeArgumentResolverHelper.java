package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.parser.node.FilterNode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

@Service
public class FilterNodeArgumentResolverHelper {

  protected final FilterStringConverter filterStringConverter;

  protected final FilterBuilder builder;

  public FilterNodeArgumentResolverHelper(@Lazy FilterStringConverter filterStringConverter,
      @Lazy FilterBuilder builder) {
    this.filterStringConverter = filterStringConverter;
    this.builder = builder;
  }

  public Optional<FilterNode> resolve(MethodParameter methodParameter,
      NativeWebRequest nativeWebRequest,
      boolean entityClassRequired) {

    Filter annotation = methodParameter.getParameterAnnotation(Filter.class);

    if (entityClassRequired && (annotation == null || annotation
        .entityClass()
        .equals(Void.class))) {
      throw new IllegalArgumentException(
          "Entity class should be specified with the FilterQuery annotation");
    }

    String parameterName =
        annotation != null ? annotation.parameter() : Filter.DEFAULT_PARAMETER_NAME;

    String[] parameterValues = nativeWebRequest.getParameterValues(parameterName);

    List<String> values = extractValues(parameterValues);

    if (values.isEmpty()) {
      return handleMissingParameter(annotation, parameterName);
    }

    if (annotation != null && annotation.maxLength() > 0) {
      for (String value : values) {
        if (value.length() > annotation.maxLength()) {
          throw new IllegalArgumentException(
              "Filter parameter '" + parameterName + "' exceeds maximum length of "
                  + annotation.maxLength());
        }
      }
    }

    return parseAndCombine(values);

  }

  private List<String> extractValues(String[] parameterValues) {
    if (parameterValues == null || parameterValues.length == 0) {
      return List.of();
    }
    return Arrays
        .stream(parameterValues)
        .filter(p -> p != null && !p.isBlank())
        .map(String::trim)
        .toList();
  }

  private Optional<FilterNode> handleMissingParameter(Filter annotation,
      String parameterName) {
    if (annotation != null && !annotation.defaultValue().isEmpty()) {
      return parseAndCombine(List.of(annotation.defaultValue()));
    }
    if (annotation != null && annotation.required()) {
      throw new IllegalArgumentException(
          "Filter parameter '" + parameterName + "' is required but was not provided");
    }
    return Optional.empty();
  }

  private Optional<FilterNode> parseAndCombine(List<String> values) {
    List<FilterNode> nodes = values
        .stream()
        .map(filterStringConverter::convert)
        .toList();

    if (nodes.isEmpty()) {
      return Optional.empty();
    }

    FilterNode node = builder
        .and(
            nodes
                .stream()
                .map(builder::from)
                .collect(Collectors.toList()))
        .get();

    return Optional.of(node);
  }

}
