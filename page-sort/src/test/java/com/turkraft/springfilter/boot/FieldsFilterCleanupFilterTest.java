package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.pagesort.FieldsExpression;
import com.turkraft.springfilter.pagesort.FieldsFilterContext;
import jakarta.servlet.DispatcherType;
import java.lang.reflect.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class FieldsFilterCleanupFilterTest {

  private final FieldsFilterCleanupFilter filter = new FieldsFilterCleanupFilter();

  @AfterEach
  void tearDown() {
    FieldsFilterContext.clear();
  }

  static class Exposed extends FieldsFilterCleanupFilter {

    boolean skipsAsync() {
      return shouldNotFilterAsyncDispatch();
    }

    boolean skipsError() {
      return shouldNotFilterErrorDispatch();
    }

  }

  private void dispatch(DispatcherType dispatcherType) throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setDispatcherType(dispatcherType);

    filter.doFilter(request, new MockHttpServletResponse(),
        (req, res) -> FieldsFilterContext.set(new FieldsExpression("", "id,name")));
  }

  @Test
  void runsOnAsyncAndErrorDispatchesRatherThanBeingSkipped() {

    Exposed exposed = new Exposed();

    Assertions.assertFalse(exposed.skipsAsync());
    Assertions.assertFalse(exposed.skipsError());

  }

  @Test
  void clearsTheProjectionOnAnAsyncDispatch() throws Exception {

    dispatch(DispatcherType.ASYNC);

    Assertions.assertNull(FieldsFilterContext.get());

  }

  @Test
  void clearsTheProjectionOnAnErrorDispatch() throws Exception {

    dispatch(DispatcherType.ERROR);

    Assertions.assertNull(FieldsFilterContext.get());

  }

  @Test
  void clearsTheProjectionOnAnOrdinaryRequest() throws Exception {

    dispatch(DispatcherType.REQUEST);

    Assertions.assertNull(FieldsFilterContext.get());

  }

  @Test
  void clearsTheProjectionEvenWhenTheHandlerFails() {

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setDispatcherType(DispatcherType.ASYNC);

    Assertions.assertThrows(Exception.class,
        () -> filter.doFilter(request, new MockHttpServletResponse(), (req, res) -> {
          FieldsFilterContext.set(new FieldsExpression("", "id,name"));
          throw new IllegalStateException("handler blew up");
        }));

    Assertions.assertNull(FieldsFilterContext.get());

  }

  @Test
  void clearsTheProjectionWhenOneRequestIsDispatchedTwice() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setDispatcherType(DispatcherType.REQUEST);
    filter.doFilter(request, response, (req, res) -> {
    });

    Assertions.assertNull(FieldsFilterContext.get());

    request.setDispatcherType(DispatcherType.ASYNC);
    filter.doFilter(request, response,
        (req, res) -> FieldsFilterContext.set(new FieldsExpression("", "id,name")));

    Assertions.assertNull(FieldsFilterContext.get());

  }

  @Test
  void clearsTheProjectionOnANestedErrorDispatch() throws Exception {

    Method method = OncePerRequestFilter.class
        .getDeclaredMethod("getAlreadyFilteredAttributeName");
    method.setAccessible(true);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setDispatcherType(DispatcherType.ERROR);
    request.setAttribute((String) method.invoke(filter), Boolean.TRUE);

    filter.doFilter(request, new MockHttpServletResponse(),
        (req, res) -> FieldsFilterContext.set(new FieldsExpression("", "id,name")));

    Assertions.assertNull(FieldsFilterContext.get());

  }

}
