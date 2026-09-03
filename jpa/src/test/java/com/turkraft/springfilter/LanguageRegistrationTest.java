package com.turkraft.springfilter;

import com.turkraft.springfilter.definition.FilterDefinition;
import com.turkraft.springfilter.definition.FilterFunction;
import com.turkraft.springfilter.definition.FilterFunctions;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AssignableTypeFilter;

@SpringBootTest(classes = LanguageRegistrationTest.Config.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class LanguageRegistrationTest {

  @SpringBootConfiguration
  @EnableAutoConfiguration
  @Import(EntityManagerConfiguration.class)
  static class Config {

  }

  @Autowired
  private FilterFunctions filterFunctions;

  @Test
  void everyDeclaredLanguageElementIsListedForAutoConfiguration() throws Exception {

    ClassPathScanningCandidateComponentProvider scanner =
        new ClassPathScanningCandidateComponentProvider(false);
    scanner.addIncludeFilter(new AssignableTypeFilter(FilterDefinition.class));

    Set<String> declared = scanner
        .findCandidateComponents("com.turkraft.springfilter.language")
        .stream()
        .map(definition -> definition.getBeanClassName())
        .collect(Collectors.toCollection(TreeSet::new));

    Assertions.assertFalse(declared.isEmpty());

    Set<String> listed = new TreeSet<>();

    Resource[] resources = new PathMatchingResourcePatternResolver().getResources(
        "classpath*:META-INF/spring/org.springframework.boot.autoconfigure."
            + "AutoConfiguration.imports");

    for (Resource resource : resources) {
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
        reader
            .lines()
            .map(String::trim)
            .filter(line -> !line.isEmpty() && !line.startsWith("#"))
            .forEach(listed::add);
      }
    }

    for (String element : declared) {
      Assertions.assertTrue(listed.contains(element),
          element + " is declared but not listed for autoconfiguration, so it resolves only "
              + "under a component scan and is missing in real applications");
    }

  }

  @Test
  void everyDeclaredFunctionResolvesByNameInAnApplicationContext() {

    List<String> names = filterFunctions
        .getFunctions()
        .stream()
        .map(FilterFunction::getName)
        .collect(Collectors.toList());

    Assertions.assertTrue(names.contains("jsonText"));

    for (String name : names) {
      Assertions.assertNotNull(filterFunctions.getFunction(name));
    }

  }

}
