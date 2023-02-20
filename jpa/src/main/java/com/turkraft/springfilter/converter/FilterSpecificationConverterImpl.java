package com.turkraft.springfilter.converter;

import com.turkraft.springfilter.helper.ExistsExpressionHelper;
import com.turkraft.springfilter.helper.PathExpressionHelper;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Objects;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
class FilterSpecificationConverterImpl implements
    FilterSpecificationConverter {

  private final ConversionService conversionService;

  private final PathExpressionHelper pathExpressionHelper;

  private final ExistsExpressionHelper existsExpressionHelper;

  private final FilterNodeProcessorFactories filterNodeProcessorFactories;

  public FilterSpecificationConverterImpl(ConversionService conversionService,
      PathExpressionHelper pathExpressionHelper,
      ExistsExpressionHelper existsExpressionHelper,
      FilterNodeProcessorFactories filterNodeProcessorFactories) {
    this.conversionService = conversionService;
    this.pathExpressionHelper = pathExpressionHelper;
    this.existsExpressionHelper = existsExpressionHelper;
    this.filterNodeProcessorFactories = filterNodeProcessorFactories;
  }

  @Override
  public <T> FilterSpecification<T> convert(String query) {
    return convert(Objects.requireNonNull(conversionService.convert(query, FilterNode.class)));
  }

  @Override
  public <T> FilterSpecification<T> convert(FilterNode node) {
    return new FilterSpecification<>() {
      @Override
      public FilterNode getFilter() {
        return node;
      }

      @SuppressWarnings("unchecked")
      @Override
      public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
          CriteriaBuilder criteriaBuilder) {
        Expression<?> expression = new FilterExpressionTransformer(conversionService,
            pathExpressionHelper, existsExpressionHelper, filterNodeProcessorFactories, root, query,
            criteriaBuilder).registerTargetType(node, Boolean.class)
            .transform(node);
        return query.where((Expression<Boolean>) expression).getRestriction();
      }
    };
  }

  @Override
  public String convert(FilterSpecification<?> specification) {
    return Objects.requireNonNull(
        conversionService.convert(specification.getFilter(), String.class));
  }

  @Override
  public Set<ConvertiblePair> getConvertibleTypes() {
    return Set.of(new ConvertiblePair(String.class, FilterSpecification.class),
        new ConvertiblePair(FilterNode.class, Specification.class),
        new ConvertiblePair(FilterSpecification.class, String.class));
  }

  @Override
  public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    if (source instanceof String) {
      return convert((String) source);
    }
    if (source instanceof FilterNode) {
      return convert((FilterNode) source);
    }
    throw new UnsupportedOperationException(
        "Can't convert " + sourceType.getName() + " to " + targetType.getName());
  }

}
