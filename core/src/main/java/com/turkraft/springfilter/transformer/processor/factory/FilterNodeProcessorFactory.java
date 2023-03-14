package com.turkraft.springfilter.transformer.processor.factory;

import com.turkraft.springfilter.definition.FilterDefinition;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterNodeTransformer;
import com.turkraft.springfilter.transformer.processor.FilterNodeProcessor;

public interface FilterNodeProcessorFactory<Source extends FilterNode> {

  <Target> Target process(FilterNodeTransformer<Target> transformer, Source source);

  <Transformer extends FilterNodeTransformer<Target>, Definition extends FilterDefinition, Target> FilterNodeProcessor<Transformer, Definition, Source, Target> getProcessor(
      Class<Transformer> transformerType, Class<Definition> definitionType);

}
