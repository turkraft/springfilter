package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.SizeFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class SizeFunctionJsonNodeProcessor implements
    FilterFunctionProcessor<FilterJsonNodeTransformer, JsonNode> {

  protected final MapFieldSupport mapFieldSupport;

  public SizeFunctionJsonNodeProcessor(MapFieldSupport mapFieldSupport) {
    this.mapFieldSupport = mapFieldSupport;
  }

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
    return transformer
        .getObjectMapper()
        .createObjectNode()
        .set("$size",
            mapFieldSupport.wrapMapField(transformer, functionNode.getArgument(0),
                transformer.transform(functionNode.getArgument(0))));
  }

}
