package com.turkraft.springfilter.helper;

import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterExpressionTransformer;
import jakarta.persistence.criteria.Expression;

public interface ExistsExpressionHelper {

  boolean requiresExists(FilterExpressionTransformer transformer, FilterNode node);

  Expression<?> wrapWithExists(FilterExpressionTransformer transformer, FilterNode node);

}
