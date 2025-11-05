package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.SizeFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class SizeFunctionPredicateProcessor implements
    FilterFunctionProcessor<FilterPredicateTransformer, Predicate<Object>> {

  @Override
  public Class<FilterPredicateTransformer> getTransformerType() {
    return FilterPredicateTransformer.class;
  }

  @Override
  public Class<SizeFunction> getDefinitionType() {
    return SizeFunction.class;
  }

  @Override
  public Predicate<Object> process(FilterPredicateTransformer transformer,
      FunctionNode source) {

    Predicate<?> argument = transformer.transform(source.getArgument(0));

    if (argument instanceof ContainerPredicate<?> container) {
      return new ContainerPredicate<>(getSizeValue(container.getValue()));
    }

    if (argument instanceof FieldAccessPredicate fieldAccess) {
      return new SizeFieldPredicate(fieldAccess);
    }

    return new ContainerPredicate<>(0);

  }

  public static class SizeFieldPredicate implements Predicate<Object> {

    private final FieldAccessPredicate fieldAccess;

    public SizeFieldPredicate(FieldAccessPredicate fieldAccess) {
      this.fieldAccess = fieldAccess;
    }

    @Override
    public boolean test(Object entity) {
      throw new UnsupportedOperationException(
          "SizeFieldPredicate cannot be directly tested. It must be composed with an operation.");
    }

    public Object getValue(Object entity) {
      return getSizeValue(fieldAccess.getValue(entity));
    }

    public FieldAccessPredicate getFieldAccess() {
      return fieldAccess;
    }

  }

  public static Object getSizeValue(Object value) {
    if (value == null) {
      return 0;
    }

    if (value instanceof Collection) {
      return ((Collection<?>) value).size();
    }

    if (value instanceof Map) {
      return ((Map<?, ?>) value).size();
    }

    if (value instanceof String) {
      return ((String) value).length();
    }

    if (value.getClass().isArray()) {
      return ((Object[]) value).length;
    }

    return 0;
  }

}
