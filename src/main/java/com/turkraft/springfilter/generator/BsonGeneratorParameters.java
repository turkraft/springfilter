package com.turkraft.springfilter.generator;

import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.MongoClientSettings;
import com.turkraft.springfilter.SpringFilterUtils;

public class BsonGeneratorParameters {

  private BsonGeneratorParameters() {}

  public static CodecRegistry CODEC_REGISTRY;

  static {

    if (SpringFilterUtils.isSpringDataMongoDbDependencyPresent()) {
      CODEC_REGISTRY = MongoClientSettings.getDefaultCodecRegistry();
    }

  }

}
