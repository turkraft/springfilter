package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.InOperator;
import com.turkraft.springfilter.parser.node.CollectionNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.Expression;
import org.springframework.stereotype.Component;

@Component
class InOperationExpressionProcessor implements
    FilterInfixOperationProcessor<FilterExpressionTransformer, Expression<?>> {

  @Override
  public Class<FilterExpressionTransformer> getTransformerType() {
    return FilterExpressionTransformer.class;
  }

  @Override
  public Class<InOperator> getDefinitionType() {
    return InOperator.class;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Expression<?> process(FilterExpressionTransformer transformer,
      InfixOperationNode source) {

    transformer.registerTargetType(source, Boolean.class);

    Expression<?> left = transformer.transform(source.getLeft());

    if (source.getRight() instanceof CollectionNode collectionNode) {

      In<Object> in = transformer.getCriteriaBuilder().in(left);

      for (FilterNode item : collectionNode.getItems()) {
        transformer.registerTargetType(item, left.getJavaType());
        in.value(transformer.transform(item));
      }

      return in;

    }

    transformer.registerTargetType(source.getRight(), left.getJavaType());

    return transformer.getCriteriaBuilder().in(left)
        .value((Expression) transformer.transform(source.getRight()));

  }

}
