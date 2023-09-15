package com.turkraft.springfilter.example;

import com.github.javafaker.Faker;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.example.model.Address;
import com.turkraft.springfilter.example.model.Company;
import com.turkraft.springfilter.example.model.Employee;
import com.turkraft.springfilter.example.model.Employee.MaritalStatus;
import com.turkraft.springfilter.example.model.Industry;
import com.turkraft.springfilter.example.model.Payslip;
import com.turkraft.springfilter.example.repository.CompanyRepository;
import com.turkraft.springfilter.example.repository.EmployeeRepository;
import com.turkraft.springfilter.example.repository.IndustryRepository;
import com.turkraft.springfilter.example.repository.PayslipRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringFilterJpaExampleApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(SpringFilterJpaExampleApplication.class, args);
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
  public void run(String... args) {

    Faker faker = new Faker(new Random(1));

    List<Industry> industries = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Industry industry = new Industry();
      industry.setName(faker.company().industry());
      industries.add(industry);
    }
    industryRepository.saveAll(industries);

    List<Company> companies = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      Company company = new Company();
      company.setName(faker.company().name());
      company.setIndustry(faker.options().nextElement(industries));
      company.setWebsites(
          Stream.generate(() -> Map.entry(faker.company().buzzword(), faker.company().url()))
              .limit(3).skip(faker.random().nextInt(0, 3))
              .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
      companies.add(company);
    }
    companyRepository.saveAll(companies);

    List<Employee> employees = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      Employee employee = new Employee();
      employee.setFirstName(faker.name().firstName());
      employee.setLastName(faker.name().lastName());
      employee.setBirthDate(faker.date().birthday());
      employee.setMaritalStatus(faker.options().option(MaritalStatus.class));
      employee.setSalary(faker.random().nextInt(1000, 10000));
      employee.setCompany(faker.options().nextElement(companies));
      employee.setManager(employees.isEmpty() ? null : faker.options().nextElement(employees));
      employee.setChildren(
          Stream.generate(faker.name()::firstName).limit(5).skip(faker.random().nextInt(0, 5))
              .collect(Collectors.toList()));
      employee.setAddress(new Address() {{
        setCity(faker.address().city());
        setCountry(faker.address().country());
        setPostalCode(faker.address().zipCode());
        setStreetAndNumber(faker.address().streetAddress());
      }});
      employees.add(employee);
    }
    employeeRepository.saveAll(employees);

    List<Payslip> payslips = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      Payslip payslip = new Payslip();
      payslip.setEmployee(faker.options().nextElement(employees));
      payslip.setDate(faker.date().past(360, TimeUnit.DAYS));
      payslips.add(payslip);
    }
    payslipRepository.saveAll(payslips);

  }

  @Operation(hidden = true)
  @GetMapping("/")
  public void index(HttpServletResponse response) throws IOException {
    response.sendRedirect("swagger-ui.html");
  }

  @Operation(parameters = @Parameter(name = "filter", in = ParameterIn.QUERY, schema = @Schema(type = "string"),
      example = "size(companies.employees) > 5"))
  @GetMapping(value = "industry")
  public List<Industry> getIndustries(
      @Parameter(hidden = true) FilterSpecification<Industry> filter) {
    return industryRepository.findAll(filter);
  }

  @Operation(parameters = @Parameter(name = "filter", in = ParameterIn.QUERY, schema = @Schema(type = "string"),
      example = "id < 10 and employees is not empty"))
  @GetMapping(value = "company")
  public List<Company> getCompanies(@Parameter(hidden = true) FilterSpecification<Company> filter) {
    return companyRepository.findAll(filter);
  }

  @Operation(parameters = @Parameter(name = "filter", in = ParameterIn.QUERY,
      schema = @Schema(type = "string"),
      example = "maritalStatus in ['divorced', 'separated'] and (size(staff) > 2 or manager is not null)"))
  @GetMapping(value = "employee")
  public List<Employee> getEmployees(
      @Parameter(hidden = true) FilterSpecification<Employee> filter) {
    return employeeRepository.findAll(filter);
  }

  @Operation(parameters = @Parameter(name = "filter", in = ParameterIn.QUERY, schema = @Schema(type = "string"),
      example = "employee.salary > 3000"))
  @GetMapping(value = "payslip")
  public List<Payslip> getPayslips(@Parameter(hidden = true) FilterSpecification<Payslip> filter) {
    return payslipRepository.findAll(filter);
  }

}
