package com.turkraft.springfilter.test.app;

import static com.turkraft.springfilter.FilterQueryBuilder.and;
import static com.turkraft.springfilter.FilterQueryBuilder.equal;
import static com.turkraft.springfilter.FilterQueryBuilder.greaterThan;
import static com.turkraft.springfilter.FilterQueryBuilder.greaterThanOrEqual;
import static com.turkraft.springfilter.FilterQueryBuilder.in;
import static com.turkraft.springfilter.FilterQueryBuilder.input;
import static com.turkraft.springfilter.FilterQueryBuilder.isEmpty;
import static com.turkraft.springfilter.FilterQueryBuilder.isNotEmpty;
import static com.turkraft.springfilter.FilterQueryBuilder.isNotNull;
import static com.turkraft.springfilter.FilterQueryBuilder.isNull;
import static com.turkraft.springfilter.FilterQueryBuilder.lessThan;
import static com.turkraft.springfilter.FilterQueryBuilder.lessThanOrEqual;
import static com.turkraft.springfilter.FilterQueryBuilder.not;
import static com.turkraft.springfilter.FilterQueryBuilder.notEqual;
import static com.turkraft.springfilter.FilterQueryBuilder.or;
import static com.turkraft.springfilter.FilterQueryBuilder.size;
import static com.turkraft.springfilter.FilterQueryBuilder.strings;
import static org.assertj.core.api.Assertions.assertThat;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.turkraft.springfilter.FilterSpecification;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional
public class ApplicationTest {

  @Autowired
  private CarRepository carRepository;

  @Test
  public void equalStringTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("brand.name : 'audi'"))).hasSize(1);
    assertThat(carRepository.findAll(new FilterSpecification<>(equal("brand.name", "audi")))).hasSize(1);
  }

  @Test
  public void equalEnumTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("color : 'black'"))).hasSize(2);
    assertThat(carRepository.findAll(new FilterSpecification<>(equal("color", Color.BLACK)))).hasSize(2);
  }

  @Test
  public void equalNumberTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("km : 250000"))).hasSize(1);
    assertThat(carRepository.findAll(new FilterSpecification<>(equal("km", 250000)))).hasSize(1);
  }

  @Test
  public void equalDateTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("accidents.date : '5/07/2000'"))).hasSize(1);
    assertThat(
        carRepository.findAll(new FilterSpecification<>(equal("accidents.date", Application.getDate(5, 7, 2000)))))
            .hasSize(1);
  }

  @Test
  public void notEqualTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("brand.name ! 'audi'"))).hasSize(2);
    assertThat(carRepository.findAll(new FilterSpecification<>(notEqual("brand.name", "audi")))).hasSize(2);
  }

  @Test
  public void greaterThanTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("km > 7000"))).hasSize(1);
    assertThat(carRepository.findAll(new FilterSpecification<>(greaterThan("km", 7000)))).hasSize(1);
  }

  @Test
  public void greaterThanOrEqualTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("km >: 7000"))).hasSize(2);
    assertThat(carRepository.findAll(new FilterSpecification<>(greaterThanOrEqual("km", 7000)))).hasSize(2);
  }

  @Test
  public void lessThanTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("km < 7000"))).hasSize(0);
    assertThat(carRepository.findAll(new FilterSpecification<>(lessThan("km", 7000)))).hasSize(0);
  }

  @Test
  public void lessThanOrEqualTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("km <: 7000"))).hasSize(1);
    assertThat(carRepository.findAll(new FilterSpecification<>(lessThanOrEqual("km", 7000)))).hasSize(1);
  }

  @Test
  public void isNullTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("year is null"))).hasSize(3);
    assertThat(carRepository.findAll(new FilterSpecification<>(isNull("year")))).hasSize(3);
  }

  @Test
  public void isNotNullTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("year is not null"))).hasSize(1);
    assertThat(carRepository.findAll(new FilterSpecification<>(isNotNull("year")))).hasSize(1);
  }

  @Test
  public void isEmptyTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("accidents is empty"))).hasSize(3);
    assertThat(carRepository.findAll(new FilterSpecification<>(isEmpty("accidents")))).hasSize(3);
  }

  @Test
  public void isNotEmptyTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("accidents is not empty"))).hasSize(1);
    assertThat(carRepository.findAll(new FilterSpecification<>(isNotEmpty("accidents")))).hasSize(1);
  }

  @Test
  public void inTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("brand.name in ('audi', 'land rover')"))).hasSize(3);
    assertThat(carRepository.findAll(new FilterSpecification<>(in("brand.name", strings("audi", "land rover")))))
        .hasSize(3);
  }

  @Test
  public void andTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("year : 2011 and color : 'black'"))).hasSize(1);
    assertThat(carRepository.findAll(new FilterSpecification<>(and(equal("year", 2011), equal("color", Color.BLACK)))))
        .hasSize(1);
  }

  @Test
  public void orTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("year : 2011 or color : 'black'"))).hasSize(2);
    assertThat(carRepository.findAll(new FilterSpecification<>(or(equal("year", 2011), equal("color", Color.BLACK)))))
        .hasSize(2);
  }

  @Test
  public void notTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("not color : 'white'"))).hasSize(2);
    assertThat(carRepository.findAll(new FilterSpecification<>(not(equal("color", Color.WHITE))))).hasSize(2);
  }

  @Test
  public void sizeTest() {
    assertThat(carRepository.findAll(new FilterSpecification<>("size(accidents) >: 1"))).hasSize(1);
    assertThat(carRepository.findAll(new FilterSpecification<>((greaterThanOrEqual(size("accidents"), input(1))))))
        .hasSize(1);
  }

}
