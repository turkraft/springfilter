package com.turkraft.springfilter.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.javafaker.Faker;
import com.turkraft.springfilter.EntityFilter;
import com.turkraft.springfilter.app.Employee.MaritalStatus;

@SpringBootApplication
@RestController
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
      payslips.add(Payslip.builder().employee(faker.options().nextElement(employees)).build());
    }
    payslipRepository.saveAll(payslips);

  }

  @GetMapping(value = "industry")
  public List<Industry> getIndustries(@EntityFilter Specification<Industry> spec) {
    return industryRepository.findAll(spec);
  }

  @GetMapping(value = "company")
  public List<Company> getCompanies(@EntityFilter Specification<Company> spec) {
    return companyRepository.findAll(spec);
  }

  @GetMapping(value = "employee")
  public List<Employee> getEmployees(@EntityFilter Specification<Employee> spec) {
    return employeeRepository.findAll(spec);
  }

  @GetMapping(value = "payslip")
  public List<Payslip> getPayslips(@EntityFilter Specification<Payslip> spec) {
    return payslipRepository.findAll(spec);
  }

}
