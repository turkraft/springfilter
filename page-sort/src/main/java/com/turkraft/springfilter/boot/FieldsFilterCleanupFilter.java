package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.pagesort.FieldsFilterContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

public class FieldsFilterCleanupFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } finally {
      FieldsFilterContext.clear();
    }
  }

  @Override
  protected boolean shouldNotFilterAsyncDispatch() {
    return false;
  }

  @Override
  protected boolean shouldNotFilterErrorDispatch() {
    return false;
  }

  @Override
  protected void doFilterNestedErrorDispatch(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      super.doFilterNestedErrorDispatch(request, response, filterChain);
    } finally {
      FieldsFilterContext.clear();
    }
  }

}
