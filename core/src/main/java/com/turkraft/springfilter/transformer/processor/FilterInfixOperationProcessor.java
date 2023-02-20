package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.transformer.FilterNodeTransformer;
import com.turkraft.springfilter.definition.FilterInfixOperator;
import com.turkraft.springfilter.parser.node.InfixOperationNode;

public interface FilterInfixOperationProcessor<Transformer extends FilterNodeTransformer<Target>, Target> extends
    FilterOperationProcessor<Transformer, FilterInfixOperator, InfixOperationNode, Target> {

}
