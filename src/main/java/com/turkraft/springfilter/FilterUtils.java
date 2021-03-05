package com.turkraft.springfilter;

import java.util.Map;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;

public class FilterUtils {

  private FilterUtils() {}

  public static Path<?> getDatabasePath(
      Root<?> table,
      Map<String, Join<Object, Object>> joins,
      String fieldPath) {

    Path<?> path = table;

    String[] fields = fieldPath.contains(".") ? fieldPath.split("\\.") : new String[] {fieldPath};

    String previous = null;

    for (String field : fields) {

      if (path instanceof PluralAttributePath || path instanceof SingularAttributePath) {
        if (!joins.containsKey(previous)) {
          if (path instanceof PluralAttributePath) {
            joins.put(previous, table.join(previous));
          } else {
            joins.put(previous, table.join(previous, JoinType.LEFT));
          }
        }
        path = joins.get(previous).get(field);
      }

      else {
        path = path.get(field);
      }

      if (previous == null) {
        previous = field;
      } else {
        previous += "." + field;
      }

    }

    return path;

  }

}
