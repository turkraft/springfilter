package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.transformer.FilterNodeTransformer;
import com.turkraft.springfilter.definition.FilterOperator;
import com.turkraft.springfilter.parser.node.OperationNode;

public interface FilterOperationProcessor<Transformer extends FilterNodeTransformer<Target>, Definition extends FilterOperator, Source extends OperationNode, Target> extends
    FilterNodeProcessor<Transformer, Definition, Source, Target> {

}
