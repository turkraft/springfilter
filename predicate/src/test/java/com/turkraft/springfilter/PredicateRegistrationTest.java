package com.turkraft.springfilter;

import com.turkraft.springfilter.transformer.processor.FilterNodeProcessor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AssignableTypeFilter;

public class PredicateRegistrationTest {

  @Test
  void everyDeclaredProcessorIsListedForAutoConfiguration() throws Exception {

    ClassPathScanningCandidateComponentProvider scanner =
        new ClassPathScanningCandidateComponentProvider(false) {
          @Override
          protected boolean isCandidateComponent(AnnotatedBeanDefinition definition) {
            return definition
                .getMetadata()
                .isIndependent();
          }
        };

    scanner.addIncludeFilter(new AssignableTypeFilter(FilterNodeProcessor.class));

    Set<String> declared = scanner
        .findCandidateComponents("com.turkraft.springfilter.transformer.processor")
        .stream()
        .map(definition -> definition.getBeanClassName())
        .filter(PredicateRegistrationTest::isConcrete)
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

    for (String processor : declared) {
      Assertions.assertTrue(listed.contains(processor),
          processor + " is declared but not listed for autoconfiguration, so it resolves only "
              + "under a component scan and is missing in real applications");
    }

  }

  private static boolean isConcrete(String className) {
    try {
      Class<?> type = Class.forName(className);
      return !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
    } catch (Throwable e) {
      return false;
    }
  }

}
