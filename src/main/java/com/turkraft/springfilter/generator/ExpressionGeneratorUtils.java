package com.turkraft.springfilter.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;
import com.turkraft.springfilter.SpringFilterUtils;
import com.turkraft.springfilter.exception.UnauthorizedPathException;

public class ExpressionGeneratorUtils {

  private ExpressionGeneratorUtils() {}

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

    if (!SpringFilterUtils.isHibernateCoreDependencyPresent()) {
      throw new UnsupportedOperationException(
          "The Hibernate Core dependency should be added in order to filter nested fields");
      // TODO: instead of throwing an exception, try to join with JPA only
    }

    Path<?> path = table;
    From<?, ?> from = table;

    String[] fields = fieldPath.split("\\.");

    String chain = null;

    for (int i = 0; i < fields.length; i++) {

      String field = fields[i];

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

      if (join != null && i < fields.length - 1) {
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

}
