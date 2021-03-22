package com.turkraft.springfilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

public class FilterUtils {

  private FilterUtils() {}

  public static Path<?> getDatabasePath(
      Root<?> table,
      Map<String, Join<?, ?>> joins,
      String fieldPath) {

    if (!fieldPath.contains(".")) {
      return table.get(fieldPath);
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
      String orderInput) {

    if (orderInput == null || orderInput.trim().isEmpty()) {
      return Collections.emptyList();
    }

    List<Order> orders = new ArrayList<Order>();

    String[] fields = orderInput.split(",");

    for (String field : fields) {
      Order order = generateOrderBy(table, criteriaBuilder, joins, field);
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
      String orderInput) {

    orderInput = orderInput.trim();

    boolean asc = true;

    if (orderInput.startsWith("-")) {
      asc = false;
      orderInput = orderInput.substring(1).trim();
    }

    if (asc) {
      return criteriaBuilder.asc(getDatabasePath(table, joins, orderInput));
    }

    return criteriaBuilder.desc(getDatabasePath(table, joins, orderInput));

  }

}
