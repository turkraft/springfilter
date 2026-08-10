package com.turkraft.springfilter.transformer;

import com.turkraft.springfilter.language.AndOperator;
import com.turkraft.springfilter.language.GreaterThanOrEqualOperator;
import com.turkraft.springfilter.language.LessThanOrEqualOperator;
import com.turkraft.springfilter.parser.node.CollectionLikeNode;
import com.turkraft.springfilter.parser.node.CollectionNode;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.parser.node.InputNode;
import com.turkraft.springfilter.parser.node.PlaceholderNode;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.parser.node.PrefixOperationNode;
import com.turkraft.springfilter.parser.node.PriorityNode;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.core.convert.ConversionService;

public class FilterStringTransformer implements FilterNodeTransformer<String> {

  protected final ConversionService conversionService;

  public FilterStringTransformer(ConversionService conversionService) {
    this.conversionService = conversionService;
  }

  @Override
  public Class<String> getTargetType() {
    return String.class;
  }

  @Override
  public String transformField(FieldNode node) {
    return node.getName();
  }

  @Override
  public String transformInput(InputNode node) {
    return "'" + Objects
        .requireNonNull(
            conversionService.convert(node.getValue(), String.class),
            "Could not convert `" + node.getValue() + "` to string")
        .replace("'", "\\'") + "'";
  }

  @Override
  public String transformPriority(PriorityNode node) {
    return "(" + transform(node.getNode()) + ")";
  }

  @Override
  public String transformPlaceholder(PlaceholderNode node) {
    return "`" + node
        .getPlaceholder()
        .getName() + "`";
  }

  @Override
  public String transformFunction(FunctionNode node) {
    return node
        .getFunction()
        .getName() + "("
        + node
        .getArguments()
        .stream()
        .map(this::transform)
        .collect(Collectors.joining(", ")) + ")";
  }

  @Override
  public String transformCollection(CollectionNode node) {
    return "[" + node
        .getItems()
        .stream()
        .map(this::transform)
        .collect(Collectors.joining(", "))
        + "]";
  }

  @Override
  public String transformCollectionLike(CollectionLikeNode node) {
    return transform(node.getLeft()) + " " + node.getOperator().getToken()
        + " [" + node.getPatterns().stream()
        .map(this::transform)
        .collect(Collectors.joining(", ")) + "]";
  }

  @Override
  public String transformPrefixOperation(PrefixOperationNode node) {
    return node
        .getOperator()
        .getToken() + " " + transform(node.getRight());
  }

  @Override
  public String transformInfixOperation(InfixOperationNode node) {
    if (isBetweenPattern(node)) {
      InfixOperationNode gteNode = (InfixOperationNode) node.getLeft();
      InfixOperationNode lteNode = (InfixOperationNode) node.getRight();
      return transform(gteNode.getLeft()) + " between "
          + transform(gteNode.getRight()) + " and "
          + transform(lteNode.getRight());
    }
    return transform(node.getLeft()) + " " + node
        .getOperator()
        .getToken() + " " + transform(
        node.getRight());
  }

  private boolean isBetweenPattern(InfixOperationNode node) {
    if (!(node.getOperator() instanceof AndOperator)) {
      return false;
    }
    if (!(node.getLeft() instanceof InfixOperationNode)) {
      return false;
    }
    if (!(node.getRight() instanceof InfixOperationNode)) {
      return false;
    }
    InfixOperationNode leftOp = (InfixOperationNode) node.getLeft();
    InfixOperationNode rightOp = (InfixOperationNode) node.getRight();
    if (!(leftOp.getOperator() instanceof GreaterThanOrEqualOperator)
        || !(rightOp.getOperator() instanceof LessThanOrEqualOperator)) {
      return false;
    }
    String leftField = transform(leftOp.getLeft());
    String rightField = transform(rightOp.getLeft());
    return leftField.equals(rightField);
  }

  @Override
  public String transformPostfixOperation(PostfixOperationNode node) {
    return transform(node.getLeft()) + " " + node
        .getOperator()
        .getToken();
  }

}
