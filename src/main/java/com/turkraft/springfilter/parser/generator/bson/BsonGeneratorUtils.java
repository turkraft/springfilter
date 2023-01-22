package com.turkraft.springfilter.parser.generator.bson;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.Nullable;

public class BsonGeneratorUtils {

  protected BsonGeneratorUtils() {}

  public static Document getDocumentFromBson(Bson bson) {
    return getDocumentFromBson(bson, null);
  }

  public static Document getDocumentFromBson(Bson bson, @Nullable CodecRegistry codecRegistry) {

    if (bson == null) {
      return null;
    }

    if (codecRegistry == null) {
      codecRegistry = BsonGeneratorParameters.getCodecRegistry();
    }

    BsonDocument bsonDocument =
        bson.toBsonDocument(BsonDocument.class, codecRegistry);
    DocumentCodec codec = new DocumentCodec();
    DecoderContext decoderContext = DecoderContext.builder().build();
    return codec.decode(new BsonDocumentReader(bsonDocument), decoderContext);

  }

  public static Query getQueryFromDocument(Document doc) {
    return doc == null ? new Query() : new BasicQuery(doc);
  }

}
