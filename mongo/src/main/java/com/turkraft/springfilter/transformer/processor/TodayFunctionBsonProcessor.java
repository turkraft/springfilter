package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.TodayFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class TodayFunctionBsonProcessor implements
    FilterFunctionProcessor<FilterBsonTransformer, Object> {

  @Override
  public Class<FilterBsonTransformer> getTransformerType() {
    return FilterBsonTransformer.class;
  }

  @Override
  public Class<TodayFunction> getDefinitionType() {
    return TodayFunction.class;
  }

  @Override
  public Object process(FilterBsonTransformer transformer, FunctionNode source) {
    transformer.registerTargetType(source, Date.class);
    return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

}
