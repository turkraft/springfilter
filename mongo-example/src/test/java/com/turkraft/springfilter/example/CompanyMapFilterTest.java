package com.turkraft.springfilter.example;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.turkraft.springfilter.example.model.Company;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CompanyMapFilterTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MongoTemplate mongoTemplate;

  @BeforeEach
  void setUp() {
    mongoTemplate.dropCollection(Company.class);

    Company apple = new Company();
    apple.setName("Apple");
    apple.setWebsites(Map.of("someSiteDefaultName", "someUrl.com"));
    apple.setEmployeeCounts(Map.of("cupertino", 123));
    apple.setRefId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
    apple.setLinks(Map.of("home", UUID.fromString("550e8400-e29b-41d4-a716-446655440000")));
    apple.setTags(Map.of("colors", List.of("red", "silver")));
    mongoTemplate.insert(apple);

    Company microsoft = new Company();
    microsoft.setName("Microsoft");
    microsoft.setWebsites(Map.of("msn", "msn.com"));
    microsoft.setEmployeeCounts(Map.of("redmond", 456));
    microsoft.setRefId(UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8"));
    microsoft.setLinks(Map.of("home", UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8")));
    microsoft.setTags(Map.of("cloud", List.of("azure", "office365")));
    mongoTemplate.insert(microsoft);
  }

  @Test
  void filterByMapKeyAndValue()
      throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "websites.someSiteDefaultName : 'someUrl.com'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Apple"));
  }

  @Test
  void filterByMapValueWithIn()
      throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "websites.msn in ['msn.com', 'bing.com']"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Microsoft"));
  }

  @Test
  void filterByMapValueWithLike()
      throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "websites.someSiteDefaultName ~ '*Url*'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Apple"));
  }

  @Test
  void filterByMapValueCombinedWithOtherFields()
      throws Exception {
    mockMvc
        .perform(get("/company").param("filter",
            "name : 'Apple' and websites.someSiteDefaultName : 'someUrl.com'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Apple"));
  }

  @Test
  void filterByUnknownMapKeyReturnsNoResult()
      throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "websites.wrongKey : 'someUrl.com'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void filterByIntegerMapValueCoercesQuotedInput()
      throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "employeeCounts.cupertino : '123'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Apple"));
  }

  @Test
  void filterByIntegerMapValueWithComparison()
      throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "employeeCounts.cupertino > 100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Apple"));

    mockMvc
        .perform(get("/company").param("filter", "employeeCounts.redmond > 500"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void uuidMapValueBehavesExactlyLikePlainUuidField()
      throws Exception {
    String refId = "550e8400-e29b-41d4-a716-446655440000";

    String plainFieldBody = mockMvc
        .perform(get("/company").param("filter", "refId : '" + refId + "'"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String mapValueBody = mockMvc
        .perform(get("/company").param("filter", "links.home : '" + refId + "'"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    Assertions.assertEquals(plainFieldBody, mapValueBody);
    Assertions.assertTrue(plainFieldBody.contains("Apple"));
    Assertions.assertFalse(plainFieldBody.contains("Microsoft"));
  }

  @Test
  void filterByMapOfListValueReturnsNoMatchForScalarComparison()
      throws Exception {
    // map-of-list values compared to scalar behave like plain list fields (no match)
    mockMvc
        .perform(get("/company").param("filter", "tags.colors : 'red'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));

    mockMvc
        .perform(get("/company").param("filter", "tags.colors in ['red', 'silver']"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

}
