package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.DbRefBsonSupport;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.language.InOperator;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import java.util.Arrays;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class InOperationBsonProcessor implements
    FilterInfixOperationProcessor<FilterBsonTransformer, Object> {

  protected final FieldTypeResolver fieldTypeResolver;

  protected final DbRefBsonSupport dbRefBsonSupport;

  public InOperationBsonProcessor(FieldTypeResolver fieldTypeResolver,
      DbRefBsonSupport dbRefBsonSupport) {
    this.fieldTypeResolver = fieldTypeResolver;
    this.dbRefBsonSupport = dbRefBsonSupport;
  }

  @Override
  public Class<FilterBsonTransformer> getTransformerType() {
    return FilterBsonTransformer.class;
  }

  @Override
  public Class<InOperator> getDefinitionType() {
    return InOperator.class;
  }

  @Override
  public Object process(FilterBsonTransformer transformer, InfixOperationNode source) {
    transformer.registerTargetType(source, Boolean.class);
    if (source.getLeft() instanceof FieldNode fieldNode) {
      transformer.registerTargetType(source.getRight(),
          fieldTypeResolver.resolve(transformer.getEntityType(), fieldNode.getName()));
    } else if (source.getRight() instanceof FieldNode fieldNode) {
      transformer.registerTargetType(source.getLeft(),
          fieldTypeResolver.resolve(transformer.getEntityType(), fieldNode.getName()));
    }
    Object leftResult = transformer.transform(source.getLeft());
    Object rightResult = transformer.transform(source.getRight());
    if (dbRefBsonSupport.isCollectionDbRefDollarField(transformer, source.getLeft())) {
      return dbRefBsonSupport.anyElementIn(leftResult, rightResult, false);
    }
    if (dbRefBsonSupport.isCollectionDbRefDollarField(transformer, source.getRight())) {
      return dbRefBsonSupport.anyElementIn(rightResult, leftResult, false);
    }
    return new Document("$and", Arrays.asList(
        new Document("$isArray", Arrays.asList(rightResult)),
        new Document("$in", Arrays.asList(leftResult, rightResult))));
  }

}
