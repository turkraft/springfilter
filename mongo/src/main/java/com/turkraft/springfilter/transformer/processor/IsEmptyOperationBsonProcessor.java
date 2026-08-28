package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.IsEmptyOperator;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import java.util.Arrays;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class IsEmptyOperationBsonProcessor implements
    FilterPostfixOperationProcessor<FilterBsonTransformer, Object> {

  protected final MapFieldBsonSupport mapFieldSupport;

  public IsEmptyOperationBsonProcessor(MapFieldBsonSupport mapFieldSupport) {
    this.mapFieldSupport = mapFieldSupport;
  }

  @Override
  public Class<FilterBsonTransformer> getTransformerType() {
    return FilterBsonTransformer.class;
  }

  @Override
  public Class<IsEmptyOperator> getDefinitionType() {
    return IsEmptyOperator.class;
  }

  @Override
  public Object process(FilterBsonTransformer transformer,
      PostfixOperationNode postfixOperationNode) {
    transformer.registerTargetType(postfixOperationNode, Boolean.class);
    Object leftResult = mapFieldSupport.wrapMapField(transformer,
        postfixOperationNode.getLeft(), transformer.transform(postfixOperationNode.getLeft()));
    return new Document("$and", Arrays.asList(
        new Document("$isArray", leftResult),
        new Document("$eq", Arrays.asList(new Document("$size", leftResult), 0))));
  }

}
