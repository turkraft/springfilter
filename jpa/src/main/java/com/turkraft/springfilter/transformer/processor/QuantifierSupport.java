package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.metamodel.PluralAttribute;

final class QuantifierSupport {

  private QuantifierSupport() {
  }

  static void requireBasicCollection(FilterExpressionTransformer transformer, FunctionNode source) {
    String functionName = source
        .getFunction()
        .getName();
    FilterNode argument = source.getArgument(0);
    if (!(argument instanceof FieldNode field)) {
      throw new IllegalArgumentException(
          "The " + functionName + " function requires a collection field argument");
    }
    jakarta.persistence.metamodel.Attribute<?, ?> attribute =
        transformer
            .getRoot()
            .getModel()
            .getAttribute(field.getName());
    if (!(attribute instanceof PluralAttribute<?, ?, ?> plural)
        || plural
        .getElementType()
        .getPersistenceType()
        != jakarta.persistence.metamodel.Type.PersistenceType.BASIC) {
      throw new IllegalArgumentException(
          "The " + functionName + " function requires a collection of basic values");
    }
  }
}
