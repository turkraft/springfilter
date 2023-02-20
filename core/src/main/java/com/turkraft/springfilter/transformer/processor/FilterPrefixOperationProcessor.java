package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.transformer.FilterNodeTransformer;
import com.turkraft.springfilter.definition.FilterPrefixOperator;
import com.turkraft.springfilter.parser.node.PrefixOperationNode;

public interface FilterPrefixOperationProcessor<Transformer extends FilterNodeTransformer<Target>, Target> extends
    FilterOperationProcessor<Transformer, FilterPrefixOperator, PrefixOperationNode, Target> {

}
