package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.TodayFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class TodayFunctionPredicateProcessor implements
    FilterFunctionProcessor<FilterPredicateTransformer, Predicate<Object>> {

  @Override
  public Class<FilterPredicateTransformer> getTransformerType() {
    return FilterPredicateTransformer.class;
  }

  @Override
  public Class<TodayFunction> getDefinitionType() {
    return TodayFunction.class;
  }

  @Override
  public Predicate<Object> process(FilterPredicateTransformer transformer,
      FunctionNode source) {
    return new ContainerPredicate<>(new SimpleDateFormat("EEEE").format(new Date()));
  }

}
