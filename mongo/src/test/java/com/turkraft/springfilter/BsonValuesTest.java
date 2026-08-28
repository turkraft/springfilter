package com.turkraft.springfilter;

import com.turkraft.springfilter.converter.StringCustomObjectIdConverter.CustomObjectId;
import com.turkraft.springfilter.converter.StringCustomUUIDConverter.CustomUUID;
import com.turkraft.springfilter.helper.BsonValues;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.convert.Jsr310Converters;

class BsonValuesTest {

  @Test
  void nullStaysNull() {
    Assertions.assertNull(BsonValues.toBson(null));
  }

  @Test
  void customObjectIdUnwrapsToObjectId() {
    Assertions.assertEquals(new ObjectId("642ebb0e91ac8f778f5654b7"),
        BsonValues.toBson(new CustomObjectId("642ebb0e91ac8f778f5654b7")));
  }

  @Test
  void customUuidUnwrapsToUuid() {
    UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Assertions.assertEquals(uuid, BsonValues.toBson(new CustomUUID(uuid.toString())));
  }

  @Test
  void instantBecomesDate() {
    Instant instant = Instant.parse("2023-01-01T00:00:00Z");
    Assertions.assertEquals(Date.from(instant), BsonValues.toBson(instant));
  }

  @Test
  void localDateBecomesDateAtStartOfDayInSystemZone() {
    LocalDate localDate = LocalDate.of(2023, 1, 1);
    Assertions.assertEquals(
        Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
        BsonValues.toBson(localDate));
  }

  @Test
  void localDateTimeBecomesDateInSystemZone() {
    LocalDateTime localDateTime = LocalDateTime.of(2023, 1, 1, 12, 30);
    Assertions.assertEquals(
        Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()),
        BsonValues.toBson(localDateTime));
  }

  @Test
  void localTimeBecomesDateOnTodayInSystemZone() {
    // Spring Data places a LocalTime on the current day when persisting it.
    LocalTime localTime = LocalTime.of(12, 30);
    Assertions.assertEquals(
        Jsr310Converters.LocalTimeToDateConverter.INSTANCE.convert(localTime),
        BsonValues.toBson(localTime));
  }

  @Test
  void offsetAndZonedDateTimesBecomeDates() {
    OffsetDateTime offsetDateTime = OffsetDateTime.of(2023, 1, 1, 1, 0, 0, 0, ZoneOffset.ofHours(1));
    ZonedDateTime zonedDateTime = offsetDateTime.toZonedDateTime();
    Date expected = Date.from(Instant.parse("2023-01-01T00:00:00Z"));
    Assertions.assertEquals(expected, BsonValues.toBson(offsetDateTime));
    Assertions.assertEquals(expected, BsonValues.toBson(zonedDateTime));
  }

  @Test
  void calendarBecomesDate() {
    Calendar calendar = Calendar.getInstance();
    Assertions.assertEquals(calendar.getTime(), BsonValues.toBson(calendar));
  }

  @Test
  void enumBecomesName() {
    Assertions.assertEquals("ACTIVE", BsonValues.toBson(TestEntity.Status.ACTIVE));
  }

  @Test
  void characterBecomesString() {
    Assertions.assertEquals("x", BsonValues.toBson('x'));
  }

  @Test
  void collectionsAndArraysAreNormalizedElementWise() {
    Instant instant = Instant.parse("2023-01-01T00:00:00Z");
    List<Object> expected = Arrays.asList(Date.from(instant), "ACTIVE", null);
    Assertions.assertEquals(expected,
        BsonValues.toBson(Arrays.asList(instant, TestEntity.Status.ACTIVE, null)));
    Assertions.assertEquals(expected,
        BsonValues.toBson(new Object[] {instant, TestEntity.Status.ACTIVE, null}));
  }

  @Test
  void driverNativeValuesPassThrough() {
    Date date = new Date();
    ObjectId objectId = new ObjectId();
    UUID uuid = UUID.randomUUID();
    BigDecimal amount = new BigDecimal("12.50");
    Assertions.assertSame(date, BsonValues.toBson(date));
    Assertions.assertSame(objectId, BsonValues.toBson(objectId));
    Assertions.assertSame(uuid, BsonValues.toBson(uuid));
    Assertions.assertSame(amount, BsonValues.toBson(amount));
    Assertions.assertEquals("text", BsonValues.toBson("text"));
    Assertions.assertEquals(Boolean.TRUE, BsonValues.toBson(true));
    Assertions.assertEquals(42L, BsonValues.toBson(42L));
  }

}
