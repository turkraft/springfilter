package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.converter.FilterPredicateConverter;
import java.util.List;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Conditional(WebMvcConfigurerCondition.class)
public class FilterPredicateArgumentResolverConfigurer implements WebMvcConfigurer {

  protected final FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper;

  protected final FilterPredicateConverter filterPredicateConverter;

  public FilterPredicateArgumentResolverConfigurer(
      @Lazy FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper,
      @Lazy FilterPredicateConverter filterPredicateConverter) {
    this.filterNodeArgumentResolverHelper = filterNodeArgumentResolverHelper;
    this.filterPredicateConverter = filterPredicateConverter;
  }

  @Override
  public void addArgumentResolvers(
      List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new FilterPredicateArgumentResolver(filterNodeArgumentResolverHelper,
        filterPredicateConverter));
  }

}
