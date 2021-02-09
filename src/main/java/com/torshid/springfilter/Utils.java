package com.torshid.springfilter;

import java.util.Map;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.hibernate.query.criteria.internal.path.PluralAttributePath;

public class Utils {

  private Utils() {}

  public static Path<?> buildDatabasePath(Root<?> table, Map<String, Join<Object, Object>> joins, String fieldPath) {

    Path<?> path = table;

    String[] fields = fieldPath.contains(".") ? fieldPath.split("\\.") : new String[] {fieldPath};

    String previous = null;

    for (String field : fields) {

      if (path instanceof PluralAttributePath) { // = relation
        if (!joins.containsKey(previous)) {
          joins.put(previous, table.join(previous));
        }
        path = joins.get(previous).get(field);
      } else {
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
