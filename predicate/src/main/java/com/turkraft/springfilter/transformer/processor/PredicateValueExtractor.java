package com.turkraft.springfilter.transformer.processor;

import java.util.function.Predicate;

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

}
