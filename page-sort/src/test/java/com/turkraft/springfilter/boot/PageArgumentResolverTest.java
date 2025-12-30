package com.turkraft.springfilter.boot;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.turkraft.springfilter.pagesort.SimpleSortParser;
import com.turkraft.springfilter.pagesort.SortParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.context.request.NativeWebRequest;

class PageArgumentResolverTest {

  private PageArgumentResolver resolver;
  private MethodParameter parameter;
  private NativeWebRequest webRequest;

  @BeforeEach
  void setUp() {
    SortParser sortParser = new SimpleSortParser();
    resolver = new PageArgumentResolver(sortParser);
    parameter = mock(MethodParameter.class);
    webRequest = mock(NativeWebRequest.class);
  }

  @Test
  void testSupportsParameterWithPageAnnotation() {
    when(parameter.hasParameterAnnotation(Pagination.class)).thenReturn(true);
    when(parameter.getParameterType()).thenReturn((Class) Pageable.class);

    assertTrue(resolver.supportsParameter(parameter));
  }

  @Test
  void testSupportsParameterWithoutPageAnnotation() {
    when(parameter.hasParameterAnnotation(Pagination.class)).thenReturn(false);

    assertFalse(resolver.supportsParameter(parameter));
  }

  @Test
  void testSupportsParameterWithWrongType() {
    when(parameter.hasParameterAnnotation(Pagination.class)).thenReturn(true);
    when(parameter.getParameterType()).thenReturn((Class) String.class);

    assertFalse(resolver.supportsParameter(parameter));
  }

  @Test
  void testResolveArgumentWithDefaults() throws Exception {
    Pagination pageAnnotation = createPageAnnotation(
        "page", "size", "sort", 0, 20, 100, 10);
    when(parameter.getParameterAnnotation(Pagination.class)).thenReturn(pageAnnotation);
    when(webRequest.getParameter("page")).thenReturn(null);
    when(webRequest.getParameter("size")).thenReturn(null);
    when(webRequest.getParameter("sort")).thenReturn(null);

    Pageable result = (Pageable) resolver.resolveArgument(parameter, null, webRequest, null);

    assertNotNull(result);
    assertEquals(0, result.getPageNumber());
    assertEquals(20, result.getPageSize());
    assertFalse(result.getSort().isSorted());
  }

  @Test
  void testResolveArgumentWithCustomPageAndSize() throws Exception {
    Pagination pageAnnotation = createPageAnnotation(
        "page", "size", "sort", 0, 20, 100, 10);
    when(parameter.getParameterAnnotation(Pagination.class)).thenReturn(pageAnnotation);
    when(webRequest.getParameter("page")).thenReturn("2");
    when(webRequest.getParameter("size")).thenReturn("50");
    when(webRequest.getParameter("sort")).thenReturn(null);

    Pageable result = (Pageable) resolver.resolveArgument(parameter, null, webRequest, null);

    assertNotNull(result);
    assertEquals(2, result.getPageNumber());
    assertEquals(50, result.getPageSize());
    assertFalse(result.getSort().isSorted());
  }

  @Test
  void testResolveArgumentWithSort() throws Exception {
    Pagination pageAnnotation = createPageAnnotation(
        "page", "size", "sort", 0, 20, 100, 10);
    when(parameter.getParameterAnnotation(Pagination.class)).thenReturn(pageAnnotation);
    when(webRequest.getParameter("page")).thenReturn("0");
    when(webRequest.getParameter("size")).thenReturn("20");
    when(webRequest.getParameter("sort")).thenReturn("name");

    Pageable result = (Pageable) resolver.resolveArgument(parameter, null, webRequest, null);

    assertNotNull(result);
    assertEquals(0, result.getPageNumber());
    assertEquals(20, result.getPageSize());
    assertTrue(result.getSort().isSorted());

    Sort.Order order = result.getSort().iterator().next();
    assertEquals("name", order.getProperty());
    assertEquals(Sort.Direction.ASC, order.getDirection());
  }

  @Test
  void testResolveArgumentWithDescendingSort() throws Exception {
    Pagination pageAnnotation = createPageAnnotation(
        "page", "size", "sort", 0, 20, 100, 10);
    when(parameter.getParameterAnnotation(Pagination.class)).thenReturn(pageAnnotation);
    when(webRequest.getParameter("page")).thenReturn("1");
    when(webRequest.getParameter("size")).thenReturn("10");
    when(webRequest.getParameter("sort")).thenReturn("-createdAt");

    Pageable result = (Pageable) resolver.resolveArgument(parameter, null, webRequest, null);

    assertNotNull(result);
    assertEquals(1, result.getPageNumber());
    assertEquals(10, result.getPageSize());
    assertTrue(result.getSort().isSorted());

    Sort.Order order = result.getSort().iterator().next();
    assertEquals("createdAt", order.getProperty());
    assertEquals(Sort.Direction.DESC, order.getDirection());
  }

  @Test
  void testResolveArgumentWithMultipleSortFields() throws Exception {
    Pagination pageAnnotation = createPageAnnotation(
        "page", "size", "sort", 0, 20, 100, 10);
    when(parameter.getParameterAnnotation(Pagination.class)).thenReturn(pageAnnotation);
    when(webRequest.getParameter("page")).thenReturn("0");
    when(webRequest.getParameter("size")).thenReturn("20");
    when(webRequest.getParameter("sort")).thenReturn("-year,name");

    Pageable result = (Pageable) resolver.resolveArgument(parameter, null, webRequest, null);

    assertNotNull(result);
    assertTrue(result.getSort().isSorted());

    var orders = result.getSort().toList();
    assertEquals(2, orders.size());

    assertEquals("year", orders.get(0).getProperty());
    assertEquals(Sort.Direction.DESC, orders.get(0).getDirection());

    assertEquals("name", orders.get(1).getProperty());
    assertEquals(Sort.Direction.ASC, orders.get(1).getDirection());
  }

  @Test
  void testResolveArgumentWithCustomParameterNames() throws Exception {
    Pagination pageAnnotation = createPageAnnotation(
        "p", "s", "order", 0, 20, 100, 10);
    when(parameter.getParameterAnnotation(Pagination.class)).thenReturn(pageAnnotation);
    when(webRequest.getParameter("p")).thenReturn("3");
    when(webRequest.getParameter("s")).thenReturn("15");
    when(webRequest.getParameter("order")).thenReturn("name");

    Pageable result = (Pageable) resolver.resolveArgument(parameter, null, webRequest, null);

    assertNotNull(result);
    assertEquals(3, result.getPageNumber());
    assertEquals(15, result.getPageSize());
    assertTrue(result.getSort().isSorted());
  }

  @Test
  void testResolveArgumentThrowsOnNegativePage() {
    Pagination pageAnnotation = createPageAnnotation(
        "page", "size", "sort", 0, 20, 100, 10);
    when(parameter.getParameterAnnotation(Pagination.class)).thenReturn(pageAnnotation);
    when(webRequest.getParameter("page")).thenReturn("-1");

    assertThrows(IllegalArgumentException.class,
        () -> resolver.resolveArgument(parameter, null, webRequest, null));
  }

  @Test
  void testResolveArgumentThrowsOnZeroSize() {
    Pagination pageAnnotation = createPageAnnotation(
        "page", "size", "sort", 0, 20, 100, 10);
    when(parameter.getParameterAnnotation(Pagination.class)).thenReturn(pageAnnotation);
    when(webRequest.getParameter("page")).thenReturn("0");
    when(webRequest.getParameter("size")).thenReturn("0");

    assertThrows(IllegalArgumentException.class,
        () -> resolver.resolveArgument(parameter, null, webRequest, null));
  }

  @Test
  void testResolveArgumentThrowsOnNegativeSize() {
    Pagination pageAnnotation = createPageAnnotation(
        "page", "size", "sort", 0, 20, 100, 10);
    when(parameter.getParameterAnnotation(Pagination.class)).thenReturn(pageAnnotation);
    when(webRequest.getParameter("page")).thenReturn("0");
    when(webRequest.getParameter("size")).thenReturn("-10");

    assertThrows(IllegalArgumentException.class,
        () -> resolver.resolveArgument(parameter, null, webRequest, null));
  }

  @Test
  void testResolveArgumentThrowsOnSizeExceedingMax() {
    Pagination pageAnnotation = createPageAnnotation(
        "page", "size", "sort", 0, 20, 100, 10);
    when(parameter.getParameterAnnotation(Pagination.class)).thenReturn(pageAnnotation);
    when(webRequest.getParameter("page")).thenReturn("0");
    when(webRequest.getParameter("size")).thenReturn("200");

    assertThrows(IllegalArgumentException.class,
        () -> resolver.resolveArgument(parameter, null, webRequest, null));
  }

  @Test
  void testResolveArgumentThrowsOnInvalidPageNumber() {
    Pagination pageAnnotation = createPageAnnotation(
        "page", "size", "sort", 0, 20, 100, 10);
    when(parameter.getParameterAnnotation(Pagination.class)).thenReturn(pageAnnotation);
    when(webRequest.getParameter("page")).thenReturn("invalid");

    assertThrows(IllegalArgumentException.class,
        () -> resolver.resolveArgument(parameter, null, webRequest, null));
  }

  @Test
  void testResolveArgumentThrowsOnInvalidSize() {
    Pagination pageAnnotation = createPageAnnotation(
        "page", "size", "sort", 0, 20, 100, 10);
    when(parameter.getParameterAnnotation(Pagination.class)).thenReturn(pageAnnotation);
    when(webRequest.getParameter("page")).thenReturn("0");
    when(webRequest.getParameter("size")).thenReturn("invalid");

    assertThrows(IllegalArgumentException.class,
        () -> resolver.resolveArgument(parameter, null, webRequest, null));
  }

  @Test
  void testResolveArgumentWithSortDisabled() throws Exception {
    Pagination pageAnnotation = createPageAnnotation(
        "page", "size", "sort", 0, 20, 100, 10, false);
    when(parameter.getParameterAnnotation(Pagination.class)).thenReturn(pageAnnotation);
    when(webRequest.getParameter("page")).thenReturn("1");
    when(webRequest.getParameter("size")).thenReturn("10");
    when(webRequest.getParameter("sort")).thenReturn("name");

    Pageable result = (Pageable) resolver.resolveArgument(parameter, null, webRequest, null);

    assertNotNull(result);
    assertEquals(1, result.getPageNumber());
    assertEquals(10, result.getPageSize());
    assertFalse(result.getSort().isSorted());
  }

  private Pagination createPageAnnotation(
      String pageParam, String sizeParam, String sortParam,
      int defaultPage, int defaultSize, int maxSize, int sortMaxFields) {
    return createPageAnnotation(pageParam, sizeParam, sortParam,
        defaultPage, defaultSize, maxSize, sortMaxFields, true);
  }

  private Pagination createPageAnnotation(
      String pageParam, String sizeParam, String sortParam,
      int defaultPage, int defaultSize, int maxSize, int sortMaxFields, boolean enableSort) {
    return new Pagination() {
      @Override
      public Class<Pagination> annotationType() {
        return Pagination.class;
      }

      @Override
      public String pageParameter() {
        return pageParam;
      }

      @Override
      public String sizeParameter() {
        return sizeParam;
      }

      @Override
      public String sortParameter() {
        return sortParam;
      }

      @Override
      public int defaultPage() {
        return defaultPage;
      }

      @Override
      public int defaultSize() {
        return defaultSize;
      }

      @Override
      public int maxSize() {
        return maxSize;
      }

      @Override
      public int sortMaxFields() {
        return sortMaxFields;
      }

      @Override
      public boolean enableSort() {
        return enableSort;
      }
    };
  }

}
