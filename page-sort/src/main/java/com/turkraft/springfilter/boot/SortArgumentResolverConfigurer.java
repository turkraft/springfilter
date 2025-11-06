package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.pagesort.SortParser;
import java.util.List;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Conditional(WebMvcConfigurerCondition.class)
public class SortArgumentResolverConfigurer implements WebMvcConfigurer {

  protected final SortParser sortParser;

  public SortArgumentResolverConfigurer(@Lazy SortParser sortParser) {
    this.sortParser = sortParser;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new SortArgumentResolver(sortParser));
  }

}
