package com.turkraft.springfilter.typesafe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an entity class for compile-time generation of a type-safe filter builder.
 *
 * <p>The generated class provides fluent, type-checked field accessors that
 * produce valid springfilter expressions without string concatenation or
 * typos in field names. The generated class extends {@code FilterChain} and
 * supports AND/OR chaining with correct operator precedence.
 *
 * <pre>{@code
 * @Filterable
 * @Entity
 * public class Car {
 *     private int year;
 *     private String model;
 * }
 *
 * // Generated: CarFilter.where(filterBuilder).year().greaterThan(2020).build()
 * }</pre>
 *
 * <p>Fields annotated with {@code @Transient} or {@code @JsonIgnore} are
 * excluded. Fields from superclasses are included.
 *
 * <p>The generated class extends {@code FilterChain} from the
 * {@code typesafe} module and supports AND/OR chaining with correct
 * operator precedence.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Filterable {

  /**
   * Custom name for the generated filter class.
   * If empty, the entity class name suffixed with "Filter" is used.
   */
  String className() default "";

}
