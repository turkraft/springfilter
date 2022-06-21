package com.turkraft.springfilter.parser.generator.bson;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;

public class BsonGeneratorUtils {

  private BsonGeneratorUtils() {}

  public static Document getDocumentFromBson(Bson bson) {

    if (bson == null) {
      return null;
    }

    BsonDocument bsonDocument =
        bson.toBsonDocument(BsonDocument.class, BsonGeneratorParameters.CODEC_REGISTRY);
    DocumentCodec codec = new DocumentCodec();
    DecoderContext decoderContext = DecoderContext.builder().build();
    return codec.decode(new BsonDocumentReader(bsonDocument), decoderContext);

  }

  public static Query getQueryFromDocument(Document doc) {
    return doc == null ? new Query() : new BasicQuery(doc);
  }

}
