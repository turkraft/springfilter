package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.pagesort.SortExpression;
import com.turkraft.springfilter.pagesort.SortParser;
import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PageArgumentResolver implements HandlerMethodArgumentResolver {

  protected final SortParser sortParser;

  public PageArgumentResolver(SortParser sortParser) {
    this.sortParser = Objects.requireNonNull(sortParser, "sortParser must not be null");
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(Pagination.class)
        && Pageable.class.isAssignableFrom(parameter.getParameterType());
  }

  @Override
  public Object resolveArgument(MethodParameter parameter,
      ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {

    Pagination pageAnnotation = parameter.getParameterAnnotation(Pagination.class);
    if (pageAnnotation == null) {
      return PageRequest.of(Pagination.DEFAULT_PAGE, Pagination.DEFAULT_SIZE);
    }

    String pageParam = webRequest.getParameter(pageAnnotation.pageParameter());
    int page = pageAnnotation.defaultPage();
    if (pageParam != null && !pageParam.trim().isEmpty()) {
      try {
        page = Integer.parseInt(pageParam);
        if (page < 0) {
          throw new IllegalArgumentException("Page number must be >= 0, got: " + page);
        }
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid page number: " + pageParam, e);
      }
    }

    String sizeParam = webRequest.getParameter(pageAnnotation.sizeParameter());
    int size = pageAnnotation.defaultSize();
    if (sizeParam != null && !sizeParam.trim().isEmpty()) {
      try {
        size = Integer.parseInt(sizeParam);
        if (size <= 0) {
          throw new IllegalArgumentException("Page size must be > 0, got: " + size);
        }
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid page size: " + sizeParam, e);
      }
    }

    int maxSize = pageAnnotation.maxSize();
    if (size > maxSize) {
      throw new IllegalArgumentException(
          "Page size " + size + " exceeds maximum allowed: " + maxSize);
    }

    org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.unsorted();

    if (pageAnnotation.enableSort()) {
      String sortParam = webRequest.getParameter(pageAnnotation.sortParameter());

      if (sortParam != null && !sortParam.trim().isEmpty()) {
        SortExpression sortExpression = sortParser.parse(sortParam,
            pageAnnotation.sortMaxFields());
        sort = sortExpression.toSpringSort();
      }
    }

    return PageRequest.of(page, size, sort);

  }

}
