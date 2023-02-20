package com.turkraft.springfilter.transformer;

import com.turkraft.springfilter.parser.node.CollectionNode;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.parser.node.InputNode;
import com.turkraft.springfilter.parser.node.OperationNode;
import com.turkraft.springfilter.parser.node.PlaceholderNode;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.parser.node.PrefixOperationNode;
import com.turkraft.springfilter.parser.node.PriorityNode;

public interface FilterNodeTransformer<Target> {

  Class<Target> getTargetType();

  default Target transform(FilterNode node) {
    if (node instanceof FieldNode) {
      return transformField((FieldNode) node);
    }
    if (node instanceof InputNode) {
      return transformInput((InputNode) node);
    }
    if (node instanceof PriorityNode) {
      return transformPriority((PriorityNode) node);
    }
    if (node instanceof PlaceholderNode) {
      return transformPlaceholder((PlaceholderNode) node);
    }
    if (node instanceof FunctionNode) {
      return transformFunction((FunctionNode) node);
    }
    if (node instanceof CollectionNode) {
      return transformCollection((CollectionNode) node);
    }
    if (node instanceof OperationNode) {
      return transformOperation((OperationNode) node);
    }
    throw new UnsupportedOperationException("Unsupported node " + node);
  }

  Target transformField(FieldNode node);

  Target transformInput(InputNode node);

  Target transformPriority(PriorityNode node);

  Target transformPlaceholder(PlaceholderNode node);

  Target transformFunction(FunctionNode node);

  Target transformCollection(CollectionNode node);

  default Target transformOperation(OperationNode node) {
    if (node instanceof PrefixOperationNode) {
      return transformPrefixOperation((PrefixOperationNode) node);
    }
    if (node instanceof InfixOperationNode) {
      return transformInfixOperation((InfixOperationNode) node);
    }
    if (node instanceof PostfixOperationNode) {
      return transformPostfixOperation((PostfixOperationNode) node);
    }
    throw new UnsupportedOperationException("Unsupported operation node " + node);
  }

  Target transformPrefixOperation(PrefixOperationNode node);

  Target transformInfixOperation(InfixOperationNode node);

  Target transformPostfixOperation(PostfixOperationNode node);

}
