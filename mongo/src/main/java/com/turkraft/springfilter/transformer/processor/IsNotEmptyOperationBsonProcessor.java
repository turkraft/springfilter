package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.IsNotEmptyOperator;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import java.util.Arrays;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class IsNotEmptyOperationBsonProcessor implements
    FilterPostfixOperationProcessor<FilterBsonTransformer, Object> {

  protected final MapFieldBsonSupport mapFieldSupport;

  public IsNotEmptyOperationBsonProcessor(MapFieldBsonSupport mapFieldSupport) {
    this.mapFieldSupport = mapFieldSupport;
  }

  @Override
  public Class<FilterBsonTransformer> getTransformerType() {
    return FilterBsonTransformer.class;
  }

  @Override
  public Class<IsNotEmptyOperator> getDefinitionType() {
    return IsNotEmptyOperator.class;
  }

  @Override
  public Object process(FilterBsonTransformer transformer,
      PostfixOperationNode postfixOperationNode) {
    transformer.registerTargetType(postfixOperationNode, Boolean.class);
    Object leftResult = mapFieldSupport.wrapMapField(transformer,
        postfixOperationNode.getLeft(), transformer.transform(postfixOperationNode.getLeft()));
    return new Document("$and", Arrays.asList(
        new Document("$isArray", leftResult),
        new Document("$gt", Arrays.asList(new Document("$size", leftResult), 0))));
  }

}
