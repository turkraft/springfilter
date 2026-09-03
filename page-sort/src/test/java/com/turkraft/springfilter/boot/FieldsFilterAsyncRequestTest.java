package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.pagesort.FieldsFilterContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;

public class FieldsFilterAsyncRequestTest {

  @AfterEach
  void tearDown() {
    FieldsFilterContext.clear();
  }

  @RestController
  static class AsyncController {

    @Fields
    @GetMapping("/things")
    Callable<Map<String, String>> things() {
      return () -> Map.of("id", "1", "name", "a");
    }

  }

  static class DefaultDispatchFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
      try {
        filterChain.doFilter(request, response);
      } finally {
        FieldsFilterContext.clear();
      }
    }

  }

  private boolean projectionSurvivesAsyncRequest(OncePerRequestFilter filter) throws Exception {

    MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new AsyncController())
        .setControllerAdvice(new FieldsFilterAdvice())
        .addFilters(filter)
        .build();

    FieldsFilterContext.clear();

    MvcResult started = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/things")
            .param("fields", "id"))
        .andReturn();

    Assertions.assertTrue(started
        .getRequest()
        .isAsyncStarted());

    mockMvc
        .perform(MockMvcRequestBuilders.asyncDispatch(started))
        .andReturn();

    boolean survived = FieldsFilterContext.get() != null;
    FieldsFilterContext.clear();
    return survived;
  }

  @Test
  void theScenarioReallyDoesLeakWithoutTheDispatchOverrides() throws Exception {

    Assertions.assertTrue(projectionSurvivesAsyncRequest(new DefaultDispatchFilter()));

  }

  @Test
  void theProjectionIsReleasedAfterAnAsynchronousRequest() throws Exception {

    Assertions.assertFalse(projectionSurvivesAsyncRequest(new FieldsFilterCleanupFilter()));

  }

}
