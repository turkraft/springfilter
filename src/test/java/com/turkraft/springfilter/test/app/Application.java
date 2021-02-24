package com.turkraft.springfilter.test.app;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements ApplicationRunner {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Autowired
  private BrandRepository brandRepository;

  @Autowired
  private CarRepository carRepository;

  @Override
  public void run(ApplicationArguments args) {

    Brand audi = Brand.builder().name("audi").build();
    Brand landRover = Brand.builder().name("land rover").build();
    brandRepository.saveAll(Arrays.asList(audi, landRover));

    Car car1 = Car.builder().km(250000).brand(landRover).build();
    Car car2 = Car.builder().color(Color.BLACK).year(2011).km(7000).brand(landRover).build();
    Car car3 = Car.builder().color(Color.BLACK).brand(audi).build();
    Car car4 = Car.builder().accidents(Arrays.asList(Accident.builder().date(getDate(5, 7, 2000)).build())).build();
    carRepository.saveAll(Arrays.asList(car1, car2, car3, car4));

  }

  public static Date getDate(int day, int month, int year) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, month - 1, day);
    return cal.getTime();
  }

}
