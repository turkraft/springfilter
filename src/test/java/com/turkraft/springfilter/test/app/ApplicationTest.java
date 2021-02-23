package com.turkraft.springfilter.test.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ApplicationTest {

  @Autowired
  private BrandRepository brandRepository;

  @Autowired
  private CarRepository carRepository;

  @Before
  public void buildDatabase() {

    Brand audi = Brand.builder().name("audi").build();
    Brand landRover = Brand.builder().name("land rover").build();
    brandRepository.saveAll(Arrays.asList(audi, landRover));

    Car car1 = Car.builder().km(250000).brand(landRover).build();
    carRepository.saveAll(Arrays.asList(car1));

  }

  @Test
  public void test() {

    Iterable<?> cars = carRepository.findAll();
    assertThat(cars).isNotEmpty();

  }

}
