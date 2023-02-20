package com.turkraft.springfilter.example;

import com.github.javafaker.Faker;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.example.model.Company;
import com.turkraft.springfilter.example.model.Employee;
import com.turkraft.springfilter.example.model.Employee.MaritalStatus;
import com.turkraft.springfilter.example.model.Industry;
import com.turkraft.springfilter.example.model.Payslip;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
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

  @Override
  public void run(String... args) {

    Faker faker = new Faker(new Random(1));

    List<Industry> industries = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Industry industry = new Industry();
      industry.setName(faker.company().industry());
      industries.add(industry);
    }
    mongoTemplate.insertAll(industries);

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
    mongoTemplate.insertAll(companies);

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
      employees.add(employee);
    }
    mongoTemplate.insertAll(employees);

    List<Payslip> payslips = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      Payslip payslip = new Payslip();
      payslip.setEmployee(faker.options().nextElement(employees));
      payslip.setDate(faker.date().past(360, TimeUnit.DAYS));
      payslips.add(payslip);
    }
    mongoTemplate.insertAll(payslips);

  }

  @GetMapping(value = "industry")
  public List<Industry> getIndustries(@Filter(entityClass = Industry.class) Document filter) {
    return mongoTemplate.find(new BasicQuery(filter), Industry.class);
  }

  @GetMapping(value = "company")
  public List<Company> getCompanies(@Filter(entityClass = Company.class) Document filter) {
    return mongoTemplate.find(new BasicQuery(filter), Company.class);
  }

  @GetMapping(value = "employee")
  public List<Employee> getEmployees(@Filter(entityClass = Employee.class) Document filter) {
    return mongoTemplate.find(new BasicQuery(filter), Employee.class);
  }

  @GetMapping(value = "payslip")
  public List<Payslip> getPayslips(@Filter(entityClass = Payslip.class) Document filter) {
    return mongoTemplate.find(new BasicQuery(filter), Payslip.class);
  }

}
