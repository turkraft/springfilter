package com.turkraft.springfilter.converter;

import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Service;

@Service
public class FilterPredicateConverterImpl implements FilterPredicateConverter {

  private final ConversionService conversionService;

  private final FilterNodeProcessorFactories filterNodeProcessorFactories;

  public FilterPredicateConverterImpl(
      @Qualifier("sfConversionService") ConversionService conversionService,
      FilterNodeProcessorFactories filterNodeProcessorFactories) {
    this.conversionService = conversionService;
    this.filterNodeProcessorFactories = filterNodeProcessorFactories;
  }

  @Override
  public <T> FilterPredicate<T> convert(String query, Class<T> entityType) {
    return convert(
        Objects.requireNonNull(conversionService.convert(query, FilterNode.class)),
        entityType);
  }

  @Override
  public <T> FilterPredicate<T> convert(FilterNode node, Class<T> entityType) {
    return new FilterPredicate<>() {
      @Override
      public FilterNode getFilter() {
        return node;
      }

      @SuppressWarnings("unchecked")
      @Override
      public boolean test(T entity) {
        Predicate<Object> predicate = new FilterPredicateTransformer(
            conversionService,
            filterNodeProcessorFactories,
            entityType).transform(node);
        return predicate.test(entity);
      }
    };
  }

  @Override
  public String convert(FilterPredicate<?> predicate) {
    return Objects.requireNonNull(
        conversionService.convert(predicate.getFilter(), String.class));
  }

  @Override
  public Set<ConvertiblePair> getConvertibleTypes() {
    return Set.of(new ConvertiblePair(String.class, FilterPredicate.class),
        new ConvertiblePair(FilterNode.class, FilterPredicate.class),
        new ConvertiblePair(FilterPredicate.class, String.class));
  }

  @Override
  public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    if (source instanceof String) {
      return convert((String) source, Object.class);
    }
    if (source instanceof FilterNode) {
      return convert((FilterNode) source, Object.class);
    }
    throw new UnsupportedOperationException(
        "Can't convert " + sourceType.getName() + " to " + targetType.getName());
  }

}
