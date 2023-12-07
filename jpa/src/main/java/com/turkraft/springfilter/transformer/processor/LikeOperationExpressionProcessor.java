package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.LikeOperator;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.parser.node.InputNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Note: the behavior of this operation might differ between different SQL dialects.
 */
@Component
public class LikeOperationExpressionProcessor implements
    FilterInfixOperationProcessor<FilterExpressionTransformer, Expression<?>> {

  @Value("${springfilter.jpa.like_escape_character:#{null}}")
  private Character escapeCharacter;

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<LikeOperator> getDefinitionType() {
    return LikeOperator.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer, InfixOperationNode source) {
    
    transformer.registerTargetType(source, Boolean.class);
    transformer.registerTargetType(source.getLeft(), String.class);
    transformer.registerTargetType(source.getRight(), String.class);

    if (getEscapeCharacter() == null) {
      return transformer.getCriteriaBuilder()
          .like((Expression<String>) transformer.transform(source.getLeft()),
              getLikePatternExpression(transformer, source.getRight()));
    } else {
      return transformer.getCriteriaBuilder()
          .like((Expression<String>) transformer.transform(source.getLeft()),
              getLikePatternExpression(transformer, source.getRight()), escapeCharacter);
    }

  }

  @SuppressWarnings("unchecked")
  public Expression<String> getLikePatternExpression(FilterExpressionTransformer transformer,
      FilterNode node) {

    if (!(node instanceof InputNode)) {
      return (Expression<String>) transformer.transform(node);
    }

    String pattern = ((InputNode) node).getValue().toString();

    pattern = pattern.replace("\\\\%", "\\%");

    pattern = pattern.replace("\\\\*", "X_ESCAPED_STAR_X");

    pattern = pattern.replace("*", "%");

    pattern = pattern.replace("X_ESCAPED_STAR_X", "*");

    if (!pattern.contains("%")) {
      pattern = "%" + pattern + "%";
    }

    return transformer.getCriteriaBuilder().literal(pattern);

  }

  @Nullable
  public Character getEscapeCharacter() {
    return escapeCharacter;
  }

}
