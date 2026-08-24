package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.IsEmptyOperator;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class IsEmptyOperationJsonNodeProcessor implements
    FilterPostfixOperationProcessor<FilterJsonNodeTransformer, JsonNode> {

  protected final MapFieldSupport mapFieldSupport;

  public IsEmptyOperationJsonNodeProcessor(MapFieldSupport mapFieldSupport) {
    this.mapFieldSupport = mapFieldSupport;
  }

  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<IsEmptyOperator> getDefinitionType() {
    return IsEmptyOperator.class;
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer,
      PostfixOperationNode postfixOperationNode) {
    transformer.registerTargetType(postfixOperationNode, Boolean.class);
    JsonNode leftResult = mapFieldSupport.wrapMapField(transformer,
        postfixOperationNode.getLeft(), transformer.transform(postfixOperationNode.getLeft()));
    return transformer
        .getObjectMapper()
        .createObjectNode()
        .set("$and",
            transformer
                .getObjectMapper()
                .createArrayNode()
                .add(transformer
                    .getObjectMapper()
                    .createObjectNode()
                    .set("$isArray", leftResult))
                .add(transformer
                    .getObjectMapper()
                    .createObjectNode()
                    .set("$eq",
                        transformer
                            .getObjectMapper()
                            .createArrayNode()
                            .add(transformer
                                .getObjectMapper()
                                .createObjectNode()
                                .set("$size", leftResult))
                            .add(transformer
                                .getObjectMapper()
                                .getNodeFactory()
                                .numberNode(0)))));
  }

}
