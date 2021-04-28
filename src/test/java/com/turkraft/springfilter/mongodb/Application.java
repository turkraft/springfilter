package com.turkraft.springfilter.mongodb;

import java.io.IOException;
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
import com.turkraft.springfilter.EntityFilter;

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
    mongoTemplate.save(Car.builder().name("Audi").build());
    mongoTemplate.save(Car.builder().name("Merco").build());
  }

  @Autowired
  private CarRepository carRepository;

  @GetMapping(value = "car")
  public List<Car> getCars(@EntityFilter Document filter) {
    return carRepository.findAll(filter);
  }

}
