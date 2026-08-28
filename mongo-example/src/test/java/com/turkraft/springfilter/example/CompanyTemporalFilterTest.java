package com.turkraft.springfilter.example;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.turkraft.springfilter.example.model.Company;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CompanyTemporalFilterTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MongoTemplate mongoTemplate;

  @BeforeEach
  void setUp() {
    mongoTemplate.dropCollection(Company.class);

    Company oldCompany = new Company();
    oldCompany.setName("Old");
    oldCompany.setCreatedAt(Instant.parse("2020-06-01T00:00:00Z"));
    oldCompany.setRefId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
    mongoTemplate.insert(oldCompany);

    Company recentCompany = new Company();
    recentCompany.setName("Recent");
    recentCompany.setCreatedAt(Instant.parse("2026-02-01T00:00:00Z"));
    recentCompany.setRefId(UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8"));
    mongoTemplate.insert(recentCompany);
  }

  @Test
  void greaterThanMatchesOnlyLaterDates()
      throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "createdAt > '2023-01-01T00:00:00Z'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Recent"));
  }

  @Test
  void lessThanMatchesOnlyEarlierDates()
      throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "createdAt < '2023-01-01T00:00:00Z'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Old"));
  }

  @Test
  void equalityMatchesTheExactInstant()
      throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "createdAt : '2020-06-01T00:00:00Z'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Old"));
  }

  @Test
  void inMatchesListedInstants()
      throws Exception {
    mockMvc
        .perform(get("/company").param("filter",
            "createdAt in ['2020-06-01T00:00:00Z', '1999-01-01T00:00:00Z']"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Old"));
  }

  @Test
  void betweenMatchesTheEnclosedInstant()
      throws Exception {
    mockMvc
        .perform(get("/company").param("filter",
            "createdAt >= '2026-01-01T00:00:00Z' and createdAt <= '2026-12-31T00:00:00Z'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Recent"));
  }

  @Test
  void uuidFieldStillMatches()
      throws Exception {
    mockMvc
        .perform(get("/company").param("filter",
            "refId : '550e8400-e29b-41d4-a716-446655440000'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Old"));
  }

}
