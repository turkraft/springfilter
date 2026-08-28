package com.turkraft.springfilter.example;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.turkraft.springfilter.example.model.Company;
import com.turkraft.springfilter.example.model.Tag;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CompanyDbRefFilterTest {

  private static final String ALPHA_ID = "642ebb0e91ac8f778f5654b7";
  private static final String BETA_ID = "642ebb0e91ac8f778f5654b8";
  private static final String GAMMA_ID = "642ebb0e91ac8f778f5654b9";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MongoTemplate mongoTemplate;

  @BeforeEach
  void setUp() {
    mongoTemplate.dropCollection(Company.class);
    mongoTemplate.dropCollection(Tag.class);

    Tag alpha = new Tag(ALPHA_ID, "alpha");
    Tag beta = new Tag(BETA_ID, "beta");
    Tag gamma = new Tag(GAMMA_ID, "gamma");
    mongoTemplate.insert(alpha);
    mongoTemplate.insert(beta);
    mongoTemplate.insert(gamma);

    Company apple = new Company();
    apple.setName("Apple");
    apple.setPrimaryTag(alpha);
    apple.setCategories(List.of(alpha, beta));
    apple.setFeaturedTag(alpha);
    apple.setLabels(List.of(alpha, beta));
    mongoTemplate.insert(apple);

    Company microsoft = new Company();
    microsoft.setName("Microsoft");
    microsoft.setPrimaryTag(gamma);
    microsoft.setCategories(List.of(gamma));
    microsoft.setFeaturedTag(gamma);
    microsoft.setLabels(List.of(gamma));
    mongoTemplate.insert(microsoft);

    Company google = new Company();
    google.setName("Google");
    mongoTemplate.insert(google);
  }

  @Test
  void filterBySingleDbRefId() throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "primaryTag.$id : '" + ALPHA_ID + "'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Apple"));
  }

  @Test
  void filterBySingleDbRefIdNoMatch() throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "primaryTag.$id : '" + BETA_ID + "'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void filterByCollectionDbRefId() throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "categories.$id : '" + BETA_ID + "'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Apple"));
  }

  @Test
  void filterByCollectionDbRefIdDoesNotMatchCompanyWithoutCategories() throws Exception {
    mockMvc
        .perform(get("/company").param("filter",
            "categories.$id : '" + BETA_ID + "' and name : 'Google'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void filterByCollectionDbRefIdWithIn() throws Exception {
    mockMvc
        .perform(get("/company").param("filter",
            "categories.$id in ['" + BETA_ID + "', '" + GAMMA_ID + "']"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void filterByCollectionDbRefIdWithNotIn() throws Exception {
    mockMvc
        .perform(get("/company").param("filter",
            "categories.$id not in ['" + ALPHA_ID + "', '" + BETA_ID + "']"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void filterBySingleDbRefRef() throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "primaryTag.$ref : 'tag'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void filterByDbRefIdCombinedWithOtherFields() throws Exception {
    mockMvc
        .perform(get("/company").param("filter",
            "name : 'Apple' and categories.$id : '" + ALPHA_ID + "'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Apple"));
  }

  @Test
  void notInOnRegularFieldWorks() throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "name not in ['Apple', 'Google']"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Microsoft"));
  }

  @Test
  void xorOnRegularFieldsProducesValidAggregation() throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "name : 'Apple' xor name : 'Microsoft'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void filterByCollectionDbRefIdGreaterThan() throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "categories.$id > '" + BETA_ID + "'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Microsoft"));
  }

  @Test
  void filterByCollectionDbRefIdLessThanOrEqual() throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "categories.$id <= '" + BETA_ID + "'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Apple"));
  }

  @Test
  void filterByCollectionDbRefIdLike() throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "categories.$id ~ '*" + ALPHA_ID + "*'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Apple"));
  }

  @Test
  void filterBySingleDocumentReferenceId() throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "featuredTag.$id : '" + ALPHA_ID + "'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Apple"));
  }

  @Test
  void filterByCollectionDocumentReferenceId() throws Exception {
    mockMvc
        .perform(get("/company").param("filter", "labels.$id : '" + BETA_ID + "'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Apple"));
  }

  @Test
  void filterByCollectionDocumentReferenceIdWithIn() throws Exception {
    mockMvc
        .perform(get("/company").param("filter",
            "labels.$id in ['" + BETA_ID + "', '" + GAMMA_ID + "']"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void filterByCollectionDocumentReferenceIdWithNotIn() throws Exception {
    mockMvc
        .perform(get("/company").param("filter",
            "labels.$id not in ['" + ALPHA_ID + "', '" + BETA_ID + "']"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void filterByCollectionDocumentReferenceIdDoesNotMatchCompanyWithoutLabels() throws Exception {
    mockMvc
        .perform(get("/company").param("filter",
            "labels.$id : '" + BETA_ID + "' and name : 'Google'"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

}
