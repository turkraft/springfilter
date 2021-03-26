package com.turkraft.springfilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;
import com.turkraft.springfilter.exception.UnauthorizedPathException;

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

    if (payload == null) {
      System.out.println("null");
    }

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

  public static List<Order> generateOrderBys(
      Root<?> table,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins,
      Object payload,
      String orderInput) {

    if (orderInput == null || orderInput.trim().isEmpty()) {
      return Collections.emptyList();
    }

    List<Order> orders = new ArrayList<Order>();

    String[] fields = orderInput.split(",");

    for (String field : fields) {
      Order order = generateOrderBy(table, criteriaBuilder, joins, payload, field);
      if (order != null) {
        orders.add(order);
      }
    }

    return orders;

  }

  private static Order generateOrderBy(
      Root<?> table,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins,
      Object payload,
      String orderInput) {

    orderInput = orderInput.trim();

    boolean asc = true;

    if (orderInput.startsWith("-")) {
      asc = false;
      orderInput = orderInput.substring(1).trim();
    }

    if (asc) {
      return criteriaBuilder.asc(
          getDatabasePath(table, joins, payload, orderInput, FilterConfig.SORTING_AUTHORIZATION));
    }

    return criteriaBuilder.desc(
        getDatabasePath(table, joins, payload, orderInput, FilterConfig.SORTING_AUTHORIZATION));

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
