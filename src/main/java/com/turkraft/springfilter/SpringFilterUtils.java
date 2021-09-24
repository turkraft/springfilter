package com.turkraft.springfilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiFunction;
import org.springframework.data.jpa.domain.Specification;
import com.turkraft.springfilter.boot.SpecificationMerger;
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

  @SafeVarargs
  public static <T> Specification<T> mergeSpecifications(Specification<T>... specifications) {

    SpecificationMerger<T> merger = new SpecificationMerger<T>();

    for (Specification<T> specification : specifications) {

      if (specification == null) {
        continue;
      }

      if (specification instanceof SpecificationMerger) {
        merger.getSpecifications()
            .addAll(((SpecificationMerger<T>) specification).getSpecifications());
      } else {
        merger.getSpecifications().add(specification);
      }
    }

    return merger;

  }

}
