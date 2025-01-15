package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import java.util.List;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Conditional(WebMvcConfigurerCondition.class)
public class FilterSpecificationArgumentResolverConfigurer implements WebMvcConfigurer {

  protected final FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper;

  protected final FilterSpecificationConverter filterSpecificationConverter;

  public FilterSpecificationArgumentResolverConfigurer(
      @Lazy FilterNodeArgumentResolverHelper filterNodeArgumentResolverHelper,
      @Lazy FilterSpecificationConverter filterSpecificationConverter) {
    this.filterNodeArgumentResolverHelper = filterNodeArgumentResolverHelper;
    this.filterSpecificationConverter = filterSpecificationConverter;
  }

  @Override
  public void addArgumentResolvers(
      List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new FilterSpecificationArgumentResolver(filterNodeArgumentResolverHelper,
        filterSpecificationConverter));
  }

}
