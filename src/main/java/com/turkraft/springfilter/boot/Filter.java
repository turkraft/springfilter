package com.turkraft.springfilter.boot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bson.conversions.Bson;

/**
 * This annotation can be used in Spring controllers to quickly build a filter as a
 * {@link org.springframework.data.jpa.domain.Specification Specification&lt;T&gt;},
 * {@link org.bson.conversions.Bson Bson}, or {@link org.bson.Document Document}. The filter's input
 * will be taken from the request's query parameters.
 *
 * <br>
 * <br>
 * Example:
 *
 * <pre>
 * &#64;GetMapping(value = "/search")
 * public List&lt;T&gt; search(@Filter Specification&lt;T&gt; doc, Pageable page) {
 *   return repo.findAll(doc, page);
 * }
 *
 * &#64;GetMapping(value = "/search")
 * public List&lt;T&gt; search(@Filter(entityClass = T.class) Document doc, Pageable page) {
 *   return repo.findAll(doc, page);
 * }
 * </pre>
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Filter {

  /**
   * The query parameter used as the filter input. <br>
   * Example: {@code /cars?filter=id>50}
   */
  String parameterName() default "filter";

  /**
   * The class of the entity on which filtering is applied. It should be provided when using
   * MongoDB's {@link Bson} and {@link Document}, but not when using JPA's
   * {@link org.springframework.data.jpa.domain.Specification Specification&lt;T&gt;}.
   */
  Class<?> entityClass() default Void.class;

}
