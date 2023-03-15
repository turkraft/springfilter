package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.language.SizeFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;

@Component
public class SizeFunctionJsonNodeProcessor implements
    FilterFunctionProcessor<FilterJsonNodeTransformer, JsonNode> {


  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<SizeFunction> getDefinitionType() {
    return SizeFunction.class;
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer,
      FunctionNode functionNode) {
    transformer.registerTargetType(functionNode, Number.class);
    return transformer.getObjectMapper().createObjectNode()
        .set("$size", transformer.transform(functionNode.getArgument(0)));
  }

}
