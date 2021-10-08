package com.turkraft.springfilter.parser.generator.bson;

import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.MongoClientSettings;
import com.turkraft.springfilter.FilterUtils;

public class BsonGeneratorParameters {

  private BsonGeneratorParameters() {}

  public static CodecRegistry CODEC_REGISTRY;

  static {

    if (FilterUtils.isSpringDataMongoDbDependencyPresent()) {
      CODEC_REGISTRY = MongoClientSettings.getDefaultCodecRegistry();
    }

  }

}
