package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.transformer.FilterNodeTransformer;
import com.turkraft.springfilter.definition.FilterPlaceholder;
import com.turkraft.springfilter.parser.node.PlaceholderNode;

public interface FilterPlaceholderProcessor<Transformer extends FilterNodeTransformer<Target>, Target> extends
    FilterNodeProcessor<Transformer, FilterPlaceholder, PlaceholderNode, Target> {

}
