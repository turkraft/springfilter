package com.turkraft.springfilter.app;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.turkraft.springfilter.FilterConfig;
import com.turkraft.springfilter.FilterSpecification;
import com.turkraft.springfilter.app.Employee.MaritalStatus;

@TestInstance(Lifecycle.PER_CLASS)
@DataJpaTest
public class ApplicationTest {

  @Autowired
  private EmployeeRepository employeeRepository;

  private Stream<Employee> randomEmployees() {
    return StreamSupport.stream(employeeRepository.findAll().spliterator(), false);
    // return Stream.of(employeeRepository.random(), employeeRepository.random(),
    // employeeRepository.random());
  }

  private void validate(String input, Predicate<Employee> predicate, boolean expectsResults) {

    // TODO
    // Entities which should theoretically match the input query but didn't
    // are currently out of the scope of this validation method
    // if some entities are missed, that should be considered as a failure
    // ideally we should compare the input matches with real query matches

    List<Employee> inputResults =
        employeeRepository.findAll(new FilterSpecification<Employee>(input));

    if (expectsResults) {
      assertFalse(inputResults.isEmpty());
    }

    for (int i = 0; i < inputResults.size(); i++) {
      Hibernate.initialize(inputResults.get(i).getPayslips());
      assertTrue(predicate.test(inputResults.get(i)));
    }

  }

  private void validate(String input, Predicate<Employee> predicate) {
    validate(input, predicate, true);
  }

  @Test
  public void databaseTest() {
    assertTrue(employeeRepository.count() > 0);
  }

  @ParameterizedTest
  @MethodSource("randomEmployees")
  public void equalStringTest(Employee employee) {
    validate(String.format("firstName : '%s'", employee.getFirstName().replace("'", "\\'")),
        e -> e.getFirstName().equals(employee.getFirstName()));
  }

  @ParameterizedTest
  @MethodSource("randomEmployees")
  public void equalEnumTest(Employee employee) {
    validate(String.format("maritalStatus : '%s'", employee.getMaritalStatus()),
        e -> e.getMaritalStatus().equals(employee.getMaritalStatus()));
  }

  @ParameterizedTest
  @MethodSource("randomEmployees")
  public void equalNumberTest(Employee employee) {
    validate(String.format("salary : %d", employee.getSalary()),
        e -> e.getSalary().equals(employee.getSalary()));
  }

  @ParameterizedTest
  @MethodSource("randomEmployees")
  public void equalDateTest(Employee employee) {
    validate(
        String.format("birthDate : '%s'",
            FilterConfig.DATE_FORMATTER.format(employee.getBirthDate())),
        e -> e.getBirthDate().equals(employee.getBirthDate()));
  }

  @ParameterizedTest
  @MethodSource("randomEmployees")
  public void notEqualTest(Employee employee) {
    validate(String.format("lastName ! '%s'", employee.getLastName().replace("'", "\\'")),
        e -> !e.getLastName().equals(employee.getLastName()));
  }

  @ParameterizedTest
  @MethodSource("randomEmployees")
  public void greaterThanTest(Employee employee) {
    validate(String.format("salary > %d", employee.getSalary()),
        e -> e.getSalary() > employee.getSalary(), false);
  }

  @ParameterizedTest
  @MethodSource("randomEmployees")
  public void greaterThanOrEqualTest(Employee employee) {
    validate(String.format("salary >: %d", employee.getSalary()),
        e -> e.getSalary() >= employee.getSalary());
  }

  @ParameterizedTest
  @MethodSource("randomEmployees")
  public void lessThanTest(Employee employee) {
    validate(String.format("salary < %d", employee.getSalary()),
        e -> e.getSalary() < employee.getSalary(), false);
  }

  @ParameterizedTest
  @MethodSource("randomEmployees")
  public void lessThanOrEqualTest(Employee employee) {
    validate(String.format("salary <: %d", employee.getSalary()),
        e -> e.getSalary() <= employee.getSalary());
  }

  @Test
  public void isNullTest() {
    validate(String.format("manager is null"), e -> e.getManager() == null);
  }

  @Test
  public void isNotNullTest() {
    validate(String.format("manager is not null"), e -> e.getManager() != null);
  }

  @Test
  public void isEmptyTest() {
    validate(String.format("payslips is empty"), e -> e.getPayslips().isEmpty());
  }

  @Test
  public void isNotEmptyTest() {
    validate(String.format("payslips is not empty"), e -> !e.getPayslips().isEmpty());
  }

  @Test
  public void inTest() {
    validate(
        String.format("maritalStatus in ('%s', '%s')", MaritalStatus.DIVORCED,
            MaritalStatus.SEPARATED),
        e -> e.getMaritalStatus() == MaritalStatus.DIVORCED
            || e.getMaritalStatus() == MaritalStatus.SEPARATED);
  }

  @ParameterizedTest
  @MethodSource("randomEmployees")
  public void andTest(Employee employee) {
    validate(
        String.format("company.id : %d and manager is not null", employee.getCompany().getId()),
        e -> e.getCompany().getId().equals(employee.getCompany().getId())
            && e.getManager() != null);
  }

  @ParameterizedTest
  @MethodSource("randomEmployees")
  public void orTest(Employee employee) {
    validate(
        String.format("maritalStatus : '%s' or payslips is empty", employee.getMaritalStatus()),
        e -> e.getMaritalStatus() == employee.getMaritalStatus() || e.getPayslips().isEmpty());
  }

  @ParameterizedTest
  @MethodSource("randomEmployees")
  public void notTest(Employee employee) {
    validate(String.format("id ! %d", employee.getId()), e -> !e.getId().equals(employee.getId()));
  }

  @ParameterizedTest
  @MethodSource("randomEmployees")
  public void sizeTest(Employee employee) {
    validate(String.format("size(payslips) : %d", employee.getPayslips().size()),
        e -> e.getPayslips().size() == employee.getPayslips().size());
  }

}
