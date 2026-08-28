package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.SizeFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class SizeFunctionBsonProcessor implements
    FilterFunctionProcessor<FilterBsonTransformer, Object> {

  protected final MapFieldBsonSupport mapFieldSupport;

  public SizeFunctionBsonProcessor(MapFieldBsonSupport mapFieldSupport) {
    this.mapFieldSupport = mapFieldSupport;
  }

  @Override
  public Class<FilterBsonTransformer> getTransformerType() {
    return FilterBsonTransformer.class;
  }

  @Override
  public Class<SizeFunction> getDefinitionType() {
    return SizeFunction.class;
  }

  @Override
  public Object process(FilterBsonTransformer transformer, FunctionNode functionNode) {
    transformer.registerTargetType(functionNode, Number.class);
    return new Document("$size", mapFieldSupport.wrapMapField(transformer,
        functionNode.getArgument(0), transformer.transform(functionNode.getArgument(0))));
  }

}
