package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.LikeOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class LikeOperationPredicateProcessor implements
    FilterInfixOperationProcessor<FilterPredicateTransformer, Predicate<Object>> {

  @Override
  public Class<FilterPredicateTransformer> getTransformerType() {
    return FilterPredicateTransformer.class;
  }

  @Override
  public Class<LikeOperator> getDefinitionType() {
    return LikeOperator.class;
  }

  @Override
  public Predicate<Object> process(FilterPredicateTransformer transformer,
      InfixOperationNode source) {

    Predicate<?> left = transformer.transform(source.getLeft());
    Predicate<?> right = transformer.transform(source.getRight());

    return entity -> {
      Object leftValue = PredicateValueExtractor.extractValue(left, entity);
      Object rightValue = PredicateValueExtractor.extractValue(right, entity);

      if (leftValue == null || rightValue == null) {
        return false;
      }

      String leftStr = leftValue.toString();
      String pattern = rightValue.toString();

      String regex = pattern
          .replace("\\", "\\\\")
          .replace(".", "\\.")
          .replace("*", "\\*")
          .replace("+", "\\+")
          .replace("?", "\\?")
          .replace("(", "\\(")
          .replace(")", "\\)")
          .replace("[", "\\[")
          .replace("]", "\\]")
          .replace("{", "\\{")
          .replace("}", "\\}")
          .replace("^", "\\^")
          .replace("$", "\\$")
          .replace("|", "\\|")
          .replace("%", ".*")
          .replace("_", ".");

      return leftStr.matches(regex);
    };

  }

}
