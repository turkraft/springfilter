package com.turkraft.springfilter.parser.generator.bson;

import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.MongoClientSettings;
import com.turkraft.springfilter.FilterUtils;

public class BsonGeneratorParameters {

  protected BsonGeneratorParameters() {}

  private static CodecRegistry CODEC_REGISTRY;

  static {

    if (FilterUtils.isSpringDataMongoDbDependencyPresent()) {
      CODEC_REGISTRY = MongoClientSettings.getDefaultCodecRegistry();
    }

  }

  public static CodecRegistry getCodecRegistry() {
    return CODEC_REGISTRY;
  }

  public static void setCodecRegistry(CodecRegistry codecRegistry) {
    CODEC_REGISTRY = codecRegistry;
  }
}
