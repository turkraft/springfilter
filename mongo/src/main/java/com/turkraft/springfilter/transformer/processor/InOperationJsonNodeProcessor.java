package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.DbRefFieldSupport;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.language.InOperator;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class InOperationJsonNodeProcessor implements
    FilterInfixOperationProcessor<FilterJsonNodeTransformer, JsonNode> {

  protected final FieldTypeResolver fieldTypeResolver;

  protected final DbRefFieldSupport dbRefFieldSupport;

  public InOperationJsonNodeProcessor(FieldTypeResolver fieldTypeResolver,
      DbRefFieldSupport dbRefFieldSupport) {
    this.fieldTypeResolver = fieldTypeResolver;
    this.dbRefFieldSupport = dbRefFieldSupport;
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

    JsonNode leftResult = transformer.transform(source.getLeft());
    JsonNode rightResult = transformer.transform(source.getRight());

    if (dbRefFieldSupport.isCollectionDbRefDollarField(transformer, source.getLeft())) {
      return dbRefFieldSupport
          .anyElementIn(transformer, leftResult, rightResult, false);
    }

    if (dbRefFieldSupport.isCollectionDbRefDollarField(transformer, source.getRight())) {
      return dbRefFieldSupport
          .anyElementIn(transformer, rightResult, leftResult, false);
    }

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
                    .set("$isArray", transformer
                        .getObjectMapper()
                        .createArrayNode()
                        .add(rightResult)))
                .add(transformer
                    .getObjectMapper()
                    .createObjectNode()
                    .set("$in",
                        transformer
                            .getObjectMapper()
                            .createArrayNode()
                            .add(leftResult)
                            .add(rightResult))));

  }

}
