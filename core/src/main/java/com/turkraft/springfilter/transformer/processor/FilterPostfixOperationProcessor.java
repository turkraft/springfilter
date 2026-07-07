package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.definition.FilterPostfixOperator;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.transformer.FilterNodeTransformer;

public interface FilterPostfixOperationProcessor<Transformer extends FilterNodeTransformer<Target>, Target> extends
    FilterOperationProcessor<Transformer, FilterPostfixOperator, PostfixOperationNode, Target> {

}
