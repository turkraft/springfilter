package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.language.InsensitiveLikeOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterPredicateTransformer;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class InsensitiveLikeOperationPredicateProcessor implements
    FilterInfixOperationProcessor<FilterPredicateTransformer, Predicate<Object>> {

  @Override
  public Class<FilterPredicateTransformer> getTransformerType() {
    return FilterPredicateTransformer.class;
  }

  @Override
  public Class<InsensitiveLikeOperator> getDefinitionType() {
    return InsensitiveLikeOperator.class;
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

      String leftStr = leftValue.toString().toLowerCase();
      String pattern = rightValue.toString().toLowerCase();

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
