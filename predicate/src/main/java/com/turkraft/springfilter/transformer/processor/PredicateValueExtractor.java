package com.turkraft.springfilter.transformer.processor;

import java.util.function.Predicate;
import org.springframework.core.convert.ConversionService;

public final class PredicateValueExtractor {

  private PredicateValueExtractor() {
  }

  public static Object extractValue(Predicate<?> predicate, Object entity) {
    if (predicate instanceof ContainerPredicate<?> container) {
      return container.getValue();
    }
    if (predicate instanceof FieldAccessPredicate fieldAccess) {
      return fieldAccess.getValue(entity);
    }
    if (predicate instanceof SizeFunctionPredicateProcessor.SizeFieldPredicate sizeField) {
      return sizeField.getValue(entity);
    }
    throw new IllegalStateException("Unsupported predicate type: " + predicate.getClass());
  }

  public static Object[] coerce(ConversionService conversionService, Object left, Object right) {
    if (left == null || right == null || left.getClass().equals(right.getClass())) {
      return new Object[]{left, right};
    }
    if (conversionService.canConvert(right.getClass(), left.getClass())) {
      try {
        return new Object[]{left, conversionService.convert(right, left.getClass())};
      } catch (Exception ignored) {
      }
    }
    return new Object[]{left, right};
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static int compare(ConversionService conversionService, Object left, Object right) {
    Object[] coerced = coerce(conversionService, left, right);
    if (coerced[0] instanceof Comparable && coerced[1] instanceof Comparable) {
      try {
        return ((Comparable) coerced[0]).compareTo(coerced[1]);
      } catch (ClassCastException e) {
        throw new IllegalStateException(cannotCompare(coerced[0], coerced[1]), e);
      }
    }
    throw new IllegalStateException(cannotCompare(coerced[0], coerced[1]));
  }

  private static String cannotCompare(Object left, Object right) {
    return "Cannot compare a value of type " + typeName(left) + " with a value of type "
        + typeName(right);
  }

  private static String typeName(Object value) {
    if (value == null) {
      return "null";
    }
    return value
        .getClass()
        .getName();
  }

}
