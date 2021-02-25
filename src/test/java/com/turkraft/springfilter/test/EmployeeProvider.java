package com.turkraft.springfilter.test;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import com.turkraft.springfilter.app.EmployeeRepository;

public class EmployeeProvider implements ArgumentsProvider {

  @Autowired
  private EmployeeRepository employeeRepository;

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
      throws Exception {
    return Stream
        .of(employeeRepository.random(), employeeRepository.random(), employeeRepository.random())
        .map(Arguments::of);
  }

}
