package com.turkraft.springfilter.transformer.processor.factory;

import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterNodeTransformer;

public interface FilterNodeProcessorFactory<Source extends FilterNode> {

  <T> T process(FilterNodeTransformer<T> transformer, Source source);

}
