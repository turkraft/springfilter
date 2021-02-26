package com.turkraft.springfilter.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springdoc.core.SwaggerUiOAuthProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.github.javafaker.Faker;
import com.turkraft.springfilter.EntityFilter;
import com.turkraft.springfilter.app.Employee.MaritalStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

@SpringBootApplication
@RestController
@Import({org.springdoc.core.SpringDocConfigProperties.class,
    org.springdoc.webmvc.core.MultipleOpenApiSupportConfiguration.class,
    org.springdoc.core.SpringDocConfiguration.class,
    org.springdoc.webmvc.core.SpringDocWebMvcConfiguration.class, SwaggerUiConfigParameters.class,
    SwaggerUiOAuthProperties.class, org.springdoc.core.SwaggerUiConfigProperties.class,
    org.springdoc.core.SwaggerUiOAuthProperties.class, org.springdoc.webmvc.ui.SwaggerConfig.class,
    org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class})
public class Application implements ApplicationRunner {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Autowired
  private IndustryRepository industryRepository;

  @Autowired
  private CompanyRepository companyRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private PayslipRepository payslipRepository;

  @Override
  public void run(ApplicationArguments args) {

    Faker faker = new Faker(new Random(1));

    List<Industry> industries = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      industries.add(Industry.builder().name(faker.company().industry()).build());
    }
    industryRepository.saveAll(industries);

    List<Company> companies = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      companies.add(Company.builder().name(faker.company().name())
          .industry(faker.options().nextElement(industries)).build());
    }
    companyRepository.saveAll(companies);

    List<Employee> employees = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      employees.add(Employee.builder().firstName(faker.name().firstName())
          .lastName(faker.name().lastName()).birthDate(faker.date().birthday())
          .maritalStatus(faker.options().option(MaritalStatus.class))
          .salary(faker.random().nextInt(1000, 10000))
          .company(faker.options().nextElement(companies))
          .manager(employees.isEmpty() ? null : faker.options().nextElement(employees)).build());
    }
    employeeRepository.saveAll(employees);

    List<Payslip> payslips = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      payslips.add(Payslip.builder().employee(faker.options().nextElement(employees))
          .date(faker.date().past(360, TimeUnit.DAYS)).build());
    }
    payslipRepository.saveAll(payslips);

  }

  @GetMapping(value = "industry")
  public List<Industry> getIndustries(
      @RequestParam(required = false) @Parameter(
          schema = @Schema(type = "string")) @EntityFilter Specification<Industry> filter) {
    return industryRepository.findAll(filter);
  }

  @GetMapping(value = "company")
  public List<Company> getCompanies(
      @RequestParam(required = false) @Parameter(
          schema = @Schema(type = "string")) @EntityFilter Specification<Company> filter) {
    return companyRepository.findAll(filter);
  }

  @GetMapping(value = "employee")
  public List<Employee> getEmployees(
      @RequestParam(required = false) @Parameter(
          schema = @Schema(type = "string")) @EntityFilter Specification<Employee> filter) {
    return employeeRepository.findAll(filter);
  }

  @GetMapping(value = "payslip")
  public List<Payslip> getPayslips(
      @RequestParam(required = false) @Parameter(
          schema = @Schema(type = "string")) @EntityFilter Specification<Payslip> filter) {
    return payslipRepository.findAll(filter);
  }

}
