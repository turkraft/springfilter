package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.language.TodayFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
class TodayFunctionJsonNodeProcessor implements
    FilterFunctionProcessor<FilterJsonNodeTransformer, JsonNode> {

  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<TodayFunction> getDefinitionType() {
    return TodayFunction.class;
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer, FunctionNode source) {
    transformer.registerTargetType(source, String.class);
    return transformer.getObjectMapper().createObjectNode()
        .textNode(new SimpleDateFormat("EEEE").format(new Date()));
  }

}
