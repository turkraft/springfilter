package com.turkraft.springfilter.helper;

import com.turkraft.springfilter.converter.StringCustomObjectIdConverter.CustomObjectId;
import com.turkraft.springfilter.converter.StringCustomUUIDConverter.CustomUUID;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.lang.Nullable;

public final class BsonValues {

  private BsonValues() {
  }

  @Nullable
  public static Object toBson(@Nullable Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof CustomObjectId customObjectId) {
      return new ObjectId(customObjectId.getValue());
    }
    if (value instanceof CustomUUID customUUID) {
      return UUID.fromString(customUUID.getValue());
    }
    if (value instanceof Instant instant) {
      return Jsr310Converters.InstantToDateConverter.INSTANCE.convert(instant);
    }
    if (value instanceof LocalDateTime localDateTime) {
      return Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(localDateTime);
    }
    if (value instanceof LocalDate localDate) {
      return Jsr310Converters.LocalDateToDateConverter.INSTANCE.convert(localDate);
    }
    if (value instanceof LocalTime localTime) {
      return Jsr310Converters.LocalTimeToDateConverter.INSTANCE.convert(localTime);
    }
    if (value instanceof OffsetDateTime offsetDateTime) {
      return Date.from(offsetDateTime.toInstant());
    }
    if (value instanceof ZonedDateTime zonedDateTime) {
      return Date.from(zonedDateTime.toInstant());
    }
    if (value instanceof Calendar calendar) {
      return calendar.getTime();
    }
    if (value instanceof Enum<?> enumValue) {
      return enumValue.name();
    }
    if (value instanceof Character character) {
      return String.valueOf(character);
    }
    if (value instanceof Collection<?> collection) {
      List<Object> result = new ArrayList<>(collection.size());
      for (Object element : collection) {
        result.add(toBson(element));
      }
      return result;
    }
    if (value instanceof Object[] array) {
      List<Object> result = new ArrayList<>(array.length);
      for (Object element : array) {
        result.add(toBson(element));
      }
      return result;
    }
    return value;
  }

}
