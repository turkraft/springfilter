package com.turkraft.springfilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiFunction;
import com.turkraft.springfilter.compiler.Parser;
import com.turkraft.springfilter.compiler.node.IExpression;

public class SpringFilterUtils {

  private SpringFilterUtils() {}

  private static boolean isClassPresent(String className) {
    try {
      Class.forName(className);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  public static boolean isSpringDataMongoDbDependencyPresent() {
    return isClassPresent("com.mongodb.MongoClientSettings");
  }

  public static boolean isSpecificationDependencyPresent() {
    return isClassPresent("org.springframework.data.jpa.domain.Specification");
  }

  public static boolean isHibernateCoreDependencyPresent() {
    return isClassPresent("org.hibernate.query.criteria.internal.path.PluralAttributePath");
  }

  public static IExpression getFilterFromInputs(String[] inputs) {

    if (inputs != null && inputs.length > 0) {

      Collection<IExpression> filters = new ArrayList<IExpression>();

      for (String input : inputs) {
        if (input.trim().length() > 0) {
          filters.add(Parser.parse(input.trim()));
        }
      }

      return FilterBuilder.and(filters);

    }

    return null;

  }

  public static <T> T merge(BiFunction<T, T, T> func, Collection<T> inputs) {
    if (inputs == null || inputs.size() == 0) {
      return null;
    }
    Iterator<T> i = inputs.iterator();
    if (inputs.size() == 1) {
      return i.next();
    }
    T result = i.next();
    while (i.hasNext()) {
      result = func.apply(result, i.next());
    }
    return result;
  }

}
