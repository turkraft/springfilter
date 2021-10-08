package com.turkraft.springfilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiFunction;
import com.turkraft.springfilter.parser.Filter;
import com.turkraft.springfilter.parser.FilterParser.FilterContext;

public class FilterUtils {

  private FilterUtils() {}

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

  public static FilterContext getFilterFromInputs(String[] inputs) {

    if (inputs != null && inputs.length > 0) {

      Collection<Filter> filters = new ArrayList<Filter>();

      for (String input : inputs) {
        if (input != null && input.trim().length() > 0) {
          filters.add(Filter.from(input.trim()).predicate());
        }
      }

      return FilterBuilder.filter(FilterBuilder.and(filters));

    }

    return null;

  }

  // not sure if something like that already exists
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

  // requireNonNullElse comes with Java 9
  public static <T> T requireNonNullElse(T obj, T defaultObj) {
    return obj != null ? obj : defaultObj;
  }

}
