package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.language.InOperator;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;

@Component
public class InOperationJsonNodeProcessor implements
    FilterInfixOperationProcessor<FilterJsonNodeTransformer, JsonNode> {

  private final FieldTypeResolver fieldTypeResolver;

  InOperationJsonNodeProcessor(FieldTypeResolver fieldTypeResolver) {
    this.fieldTypeResolver = fieldTypeResolver;
  }

  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<InOperator> getDefinitionType() {
    return InOperator.class;
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer,
      InfixOperationNode source) {

    transformer.registerTargetType(source, Boolean.class);

    if (source.getLeft() instanceof FieldNode fieldNode) {
      transformer.registerTargetType(source.getRight(),
          fieldTypeResolver.resolve(transformer.getEntityType(), fieldNode.getName()));
    } else if (source.getRight() instanceof FieldNode fieldNode) {
      transformer.registerTargetType(source.getLeft(),
          fieldTypeResolver.resolve(transformer.getEntityType(), fieldNode.getName()));
    }

    return transformer.getObjectMapper().createObjectNode().set("$and",
        transformer.getObjectMapper().createArrayNode()
            .add(transformer.getObjectMapper().createObjectNode()
                .set("$isArray", transformer.transform(source.getRight())))
            .add(transformer.getObjectMapper().createObjectNode().set("$in",
                transformer.getObjectMapper().createArrayNode()
                    .add(transformer.transform(source.getLeft()))
                    .add(transformer.transform(source.getRight())))));

  }

}
