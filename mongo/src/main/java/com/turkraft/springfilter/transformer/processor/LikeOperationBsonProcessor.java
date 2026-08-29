package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.DbRefBsonSupport;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.language.LikeOperator;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.parser.node.InputNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import java.lang.reflect.Field;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

@Component
public class LikeOperationBsonProcessor implements
    FilterInfixOperationProcessor<FilterBsonTransformer, Object> {

  protected final FieldTypeResolver fieldTypeResolver;

  protected final DbRefBsonSupport dbRefBsonSupport;

  public LikeOperationBsonProcessor(FieldTypeResolver fieldTypeResolver,
      DbRefBsonSupport dbRefBsonSupport) {
    this.fieldTypeResolver = fieldTypeResolver;
    this.dbRefBsonSupport = dbRefBsonSupport;
  }

  @Override
  public Class<FilterBsonTransformer> getTransformerType() {
    return FilterBsonTransformer.class;
  }

  @Override
  public Class<LikeOperator> getDefinitionType() {
    return LikeOperator.class;
  }

  @Override
  public Object process(FilterBsonTransformer transformer,
      InfixOperationNode infixOperationNode) {
    return getRegexNode(transformer, infixOperationNode, "");
  }

  public Document getRegexNode(FilterBsonTransformer transformer,
      InfixOperationNode infixOperationNode, String regexOptions) {
    transformer.registerTargetType(infixOperationNode, Boolean.class);
    transformer.registerTargetType(infixOperationNode.getLeft(), String.class);
    transformer.registerTargetType(infixOperationNode.getRight(), String.class);
    Object leftResult = transformer.transform(infixOperationNode.getLeft());
    Object regexValue = infixOperationNode.getRight() instanceof InputNode inputNode
        ? createRegex(String.valueOf(inputNode.getValue()))
        : transformer.transform(infixOperationNode.getRight());
    if (dbRefBsonSupport.isCollectionDbRefDollarField(transformer,
        infixOperationNode.getLeft())) {
      Document elementRegex = new Document("input", dbRefBsonSupport.regexInput("$$this"))
          .append("regex", regexValue)
          .append("options", regexOptions);
      return dbRefBsonSupport.anyElement(leftResult, new Document("$regexMatch", elementRegex),
          false);
    }
    Object input = requiresStringConversion(transformer, infixOperationNode.getLeft())
        ? dbRefBsonSupport.regexInput(leftResult)
        : leftResult;
    return new Document("$regexMatch", new Document("input", input)
        .append("regex", regexValue)
        .append("options", regexOptions));
  }

  private boolean requiresStringConversion(FilterBsonTransformer transformer, FilterNode left) {
    if (dbRefBsonSupport.isReferenceDollarField(transformer, left)) {
      return true;
    }
    if (!(left instanceof FieldNode fieldNode)) {
      return false;
    }
    Field field = fieldTypeResolver.getField(transformer.getEntityType(), fieldNode.getName());
    return field != null && (field.getType().equals(ObjectId.class)
        || (field.isAnnotationPresent(Id.class) && field.getType().equals(String.class)));
  }

  private String createRegex(String input) {
    if (!input.contains("*")) {
      return ".*" + sanitizeRegexInput(input) + ".*";
    }
    return sanitizeRegexInput(input.replace("*", "X_WILDCARD_X")).replace("X_WILDCARD_X", ".*");
  }

  private String sanitizeRegexInput(String input) {
    return input.replaceAll("[-.\\+*?\\[^\\]$(){}=!<>|:\\\\]", "\\\\$0");
  }

}
