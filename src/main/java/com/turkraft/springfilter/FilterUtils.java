package com.turkraft.springfilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.conversions.Bson;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import com.turkraft.springfilter.exception.UnauthorizedPathException;
import com.turkraft.springfilter.node.IExpression;

public class FilterUtils {

  private FilterUtils() {}

  public static Path<?> getDatabasePath(Root<?> table, String fieldPath) {
    return getDatabasePath(table, new HashMap<>(), null, fieldPath);
  }

  public static Path<?> getDatabasePath(
      Root<?> table,
      Map<String, Join<?, ?>> joins,
      String fieldPath) {
    return getDatabasePath(table, joins, null, fieldPath);
  }

  public static Path<?> getDatabasePath(
      Root<?> table,
      Map<String, Join<?, ?>> joins,
      Object payload,
      String fieldPath) {
    return getDatabasePath(table, joins, payload, fieldPath, null);
  }

  public static Path<?> getDatabasePath(
      Root<?> table,
      Map<String, Join<?, ?>> joins,
      Object payload,
      String fieldPath,
      BiFunction<Path<?>, Object, Boolean> authorizer) {

    if (!fieldPath.contains(".")) {
      return authorize(authorizer, table.get(fieldPath), payload, fieldPath);
    }

    Path<?> path = table;
    From<?, ?> from = table;

    String[] fields = fieldPath.split("\\.");

    String chain = null;

    for (String field : fields) {

      path = from.get(field);

      if (chain == null) {
        chain = field;
      } else {
        chain += "." + field;
      }

      authorize(authorizer, path, payload, chain);

      JoinType join = path instanceof PluralAttributePath ? JoinType.INNER
          : (path instanceof SingularAttributePath && ((SingularAttributePath<?>) path)
              .getAttribute().getPersistentAttributeType() != PersistentAttributeType.BASIC
                  ? JoinType.LEFT
                  : null);

      if (join != null) {
        if (!joins.containsKey(chain)) {
          joins.put(chain, from.join(field, join));
        }
        from = joins.get(chain);
      }

    }

    return path;

  }

  private static Path<?> authorize(
      BiFunction<Path<?>, Object, Boolean> authorizer,
      Path<?> path,
      Object payload,
      String fieldPath) {

    if (authorizer != null) {
      if (!Boolean.TRUE.equals(authorizer.apply(path, payload))) {
        throw new UnauthorizedPathException(path, fieldPath);
      }
    }

    return path;

  }

  public static Document getDocumentFromBson(Bson bson) {

    if (bson == null) {
      return null;
    }

    BsonDocument bsonDocument =
        bson.toBsonDocument(BsonDocument.class, FilterConfig.CODEC_REGISTRY);
    DocumentCodec codec = new DocumentCodec();
    DecoderContext decoderContext = DecoderContext.builder().build();
    return codec.decode(new BsonDocumentReader(bsonDocument), decoderContext);

  }

  public static IExpression getFilterFromInputs(String[] inputs) {

    if (inputs != null && inputs.length > 0) {

      Collection<IExpression> filters = new ArrayList<IExpression>();

      for (String input : inputs) {
        if (input.trim().length() > 0) {
          filters.add(FilterParser.parse(input.trim()));
        }
      }

      return FilterQueryBuilder.and(filters);

    }

    return null;

  }

  public static Query getQueryFromDocument(Document doc) {
    return doc == null ? new Query() : new BasicQuery(doc);
  }

}
