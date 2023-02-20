package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.definition.FilterDefinition;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterNodeTransformer;

public interface FilterNodeProcessor<Transformer extends FilterNodeTransformer<Target>, Definition extends FilterDefinition, Source extends FilterNode, Target> {

  Class<Transformer> getTransformerType();

  Class<? extends Definition> getDefinitionType();

  Target process(Transformer transformer, Source source);

}
