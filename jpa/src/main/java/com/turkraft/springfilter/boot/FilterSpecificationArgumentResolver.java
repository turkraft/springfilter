package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.node.FilterNode;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class FilterSpecificationArgumentResolver implements HandlerMethodArgumentResolver {

  private final FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper;

  private final FilterSpecificationConverter filterSpecificationConverter;

  FilterSpecificationArgumentResolver(
      FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper,
      FilterSpecificationConverter filterSpecificationConverter) {
    this.filterNodeArgumentResolverHelper = filterNodeArgumentResolverHelper;
    this.filterSpecificationConverter = filterSpecificationConverter;
  }

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return methodParameter.getParameterType().equals(FilterSpecification.class) || (
        methodParameter.hasParameterAnnotation(Filter.class)
            && methodParameter.getParameterType().equals(Specification.class))
        || isOptionalParameter(methodParameter);
  }

  @NonNull
  @Override
  public Object resolveArgument(MethodParameter methodParameter,
      ModelAndViewContainer modelAndViewContainer,
      NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {

    Optional<FilterNode> result = filterNodeArgumentResolverHelper.resolve(methodParameter,
        nativeWebRequest,
        false);

    if (isOptionalParameter(methodParameter)) {
      if (result.isEmpty()) {
        return Optional.empty();
      } else {
        return Optional.of(filterSpecificationConverter.convert(result.get()));
      }
    } else {
      if (result.isEmpty()) {
        return new FilterSpecification<>() {
          @Override
          public Predicate toPredicate(Root<Object> root, CriteriaQuery<?> query,
              CriteriaBuilder criteriaBuilder) {
            return null;
          }

          @Nullable
          @Override
          public FilterNode getFilter() {
            return null;
          }
        };
      } else {
        return filterSpecificationConverter.convert(result.get());
      }
    }

  }

  private boolean isOptionalParameter(MethodParameter methodParameter) {
    return methodParameter.getParameterType().equals(
        Optional.class) && (methodParameter.getGenericParameterType().getTypeName()
        .equals(Optional.class.getName() + "<" + FilterSpecification.class.getName() + ">") || (
        methodParameter.hasParameterAnnotation(Filter.class)
            && methodParameter.getGenericParameterType().getTypeName()
            .equals(Optional.class.getName() + "<" + Specification.class.getName())));
  }

}
