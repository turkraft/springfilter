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

}
