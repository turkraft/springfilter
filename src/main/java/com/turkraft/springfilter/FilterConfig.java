package com.turkraft.springfilter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.function.BiFunction;
import javax.persistence.criteria.Path;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.MongoClientSettings;

public class FilterConfig {

  public static SimpleDateFormat DATE_FORMATTER;

  public static NumberFormat NUMBER_FORMAT;

  public static BiFunction<Path<?>, Object, Boolean> FILTERING_AUTHORIZATION;

  public static boolean ENABLE_ASTERISK_WITH_LIKE_OPERATOR;

  public static CodecRegistry CODEC_REGISTRY;

  static {

    DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");

    NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    NUMBER_FORMAT.setGroupingUsed(false); // in order to not count commas as part of number

    FILTERING_AUTHORIZATION = null;

    ENABLE_ASTERISK_WITH_LIKE_OPERATOR = true;

    if (isMongoDBDepdendencyPresent()) {
      CODEC_REGISTRY = MongoClientSettings.getDefaultCodecRegistry();
    }

  }

  public static boolean isMongoDBDepdendencyPresent() {
    try {
      Class.forName("com.mongodb.MongoClientSettings");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

}
