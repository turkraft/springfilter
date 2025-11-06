package com.turkraft.springfilter.example;

import com.github.javafaker.Faker;
import com.turkraft.springfilter.boot.Fields;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.boot.Page;
import com.turkraft.springfilter.example.model.Company;
import com.turkraft.springfilter.example.model.Employee;
import com.turkraft.springfilter.example.model.Employee.MaritalStatus;
import com.turkraft.springfilter.example.model.Industry;
import com.turkraft.springfilter.example.model.Payslip;
import io.swagger.v3.oas.annotations.Operation;
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
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringFilterMongoExampleApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(SpringFilterMongoExampleApplication.class, args);
  }

  @Autowired
  private MongoTemplate mongoTemplate;

  private static final Faker faker = new Faker(new Random(1));

  @Override
  public void run(String... args) {

    List<Industry> industries = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Industry industry = generateIndustry();
      industry.setCompanies(
          Stream
              .generate(this::generateCompany)
              .limit(5)
              .skip(faker
                  .random()
                  .nextInt(0, 5))
              .toList());
      industries.add(industry);
    }
    mongoTemplate.insertAll(industries);

    List<Company> companies = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      Company company = generateCompany();
      company.setIndustry(generateIndustry());
      company.setEmployees(
          Stream
              .generate(this::generateEmployee)
              .limit(10)
              .skip(faker
                  .random()
                  .nextInt(0, 10))
              .toList());
      companies.add(company);
    }
    mongoTemplate.insertAll(companies);

    List<Employee> employees = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      Employee employee = generateEmployee();
      employee.setCompany(generateCompany());
      employee.setManager(faker
          .number()
          .numberBetween(0, 10) < 2 ? null : generateEmployee());
      employee.setStaff(
          Stream
              .generate(this::generateEmployee)
              .limit(5)
              .skip(faker
                  .random()
                  .nextInt(0, 5))
              .toList());
      employees.add(employee);
    }
    mongoTemplate.insertAll(employees);

    List<Payslip> payslips = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      Payslip payslip = generatePayslip();
      payslip.setEmployee(generateEmployee());
      payslips.add(payslip);
    }
    mongoTemplate.insertAll(payslips);

  }

  private static Industry generateIndustry() {
    Industry industry = new Industry();
    industry.setName(faker
        .company()
        .industry());
    return industry;
  }

  private Company generateCompany() {
    Company company = new Company();
    company.setName(faker
        .company()
        .name());
    company.setWebsites(
        Stream
            .generate(() -> Map.entry(faker
                .company()
                .buzzword(), faker
                .company()
                .url()))
            .limit(3)
            .skip(faker
                .random()
                .nextInt(0, 3))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
    return company;
  }

  private Employee generateEmployee() {
    Employee employee = new Employee();
    employee.setFirstName(faker
        .name()
        .firstName());
    employee.setLastName(faker
        .name()
        .lastName());
    employee.setBirthDate(faker
        .date()
        .birthday());
    employee.setMaritalStatus(faker
        .options()
        .option(MaritalStatus.class));
    employee.setSalary(faker
        .random()
        .nextInt(1000, 10000));
    employee.setChildren(
        Stream
            .generate(faker.name()::firstName)
            .limit(5)
            .skip(faker
                .random()
                .nextInt(0, 5))
            .collect(Collectors.toList()));
    return employee;
  }

  private static Payslip generatePayslip() {
    Payslip payslip = new Payslip();
    payslip.setDate(faker
        .date()
        .past(360, TimeUnit.DAYS));
    return payslip;
  }

  @Operation(hidden = true)
  @GetMapping("/")
  public void index(HttpServletResponse response)
      throws IOException {
    response.sendRedirect("swagger-ui.html");
  }

  @GetMapping(value = "industry")
  @Fields
  public List<Industry> getIndustries(
      @Filter(entityClass = Industry.class) Document filter,
      @Page Pageable pageable) {
    Query query = new BasicQuery(filter);
    query.with(pageable);
    return mongoTemplate.find(query, Industry.class);
  }

  @GetMapping(value = "company")
  @Fields
  public List<Company> getCompanies(
      @Filter(entityClass = Company.class) Document filter,
      @Page Pageable pageable) {
    Query query = new BasicQuery(filter);
    query.with(pageable);
    return mongoTemplate.find(query, Company.class);
  }

  @GetMapping(value = "employee")
  @Fields
  public List<Employee> getEmployees(
      @Filter(entityClass = Employee.class) Document filter,
      @Page Pageable pageable) {
    Query query = new BasicQuery(filter);
    query.with(pageable);
    return mongoTemplate.find(query, Employee.class);
  }

  @GetMapping(value = "payslip")
  @Fields
  public List<Payslip> getPayslips(
      @Filter(entityClass = Payslip.class) Document filter,
      @Page Pageable pageable) {
    Query query = new BasicQuery(filter);
    query.with(pageable);
    return mongoTemplate.find(query, Payslip.class);
  }

}
