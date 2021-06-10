package com.turkraft.springfilter.mongodb;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.turkraft.springfilter.boot.Filter;

@SpringBootApplication(exclude = EmbeddedMongoAutoConfiguration.class)
@RestController
public class Application implements ApplicationRunner {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public void run(ApplicationArguments args) throws IOException {
    mongoTemplate.save(Car.builder().name("Audi").someDate(Date.from(Instant.now())).build());
    mongoTemplate.save(Car.builder().name("Merco").someDate(Date.from(Instant.MIN)).build());
  }

  @Autowired
  private CarRepository carRepository;

  @GetMapping(value = "car")
  public List<Car> getCars(@Filter Document filter) {
    return carRepository.findAll(filter);
  }

}
