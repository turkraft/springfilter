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

  FilterNodeArgumentResolverHelper(@Lazy FilterStringConverter filterStringConverter,
      @Lazy FilterBuilder builder) {
    this.filterStringConverter = filterStringConverter;
    this.builder = builder;
  }

  Optional<FilterNode> resolve(MethodParameter methodParameter, NativeWebRequest nativeWebRequest,
      boolean entityClassRequired) {

    Filter annotation = methodParameter.getParameterAnnotation(Filter.class);

    if (entityClassRequired && (annotation == null || annotation.entityClass()
        .equals(Void.class))) {
      throw new IllegalArgumentException(
          "Entity class should be specified with the FilterQuery annotation");
    }

    String parameterName =
        annotation != null ? annotation.parameter() : Filter.DEFAULT_PARAMETER_NAME;

    if (nativeWebRequest.getParameterValues(parameterName) == null) {
      return Optional.empty();
    }

    List<FilterNode> nodes = Arrays.stream(nativeWebRequest.getParameterValues(parameterName))
        .filter(p -> p != null && !p.isBlank())
        .map(p -> filterStringConverter.convert(p.trim())).toList();

    if (nodes.isEmpty()) {
      return Optional.empty();
    }

    FilterNode node = builder.and(
        nodes.stream().map(builder::from).collect(Collectors.toList())).get();

    return Optional.of(node);

  }

}
