package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.definition.FilterFunction;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.transformer.FilterNodeTransformer;

public interface FilterFunctionProcessor<Transformer extends FilterNodeTransformer<Target>, Target> extends
    FilterNodeProcessor<Transformer, FilterFunction, FunctionNode, Target> {

}
