package com.turkraft.springfilter.transformer.processor.factory;

import com.turkraft.springfilter.parser.node.OperationNode;
import com.turkraft.springfilter.transformer.FilterNodeTransformer;
import com.turkraft.springfilter.transformer.processor.FilterOperationProcessor;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FilterOperationProcessorFactoryImpl extends
    AbstractFilterNodeProcessorFactory<OperationNode, FilterOperationProcessor<?, ?, ? extends OperationNode, ?>> implements
    FilterOperationProcessorFactory {

  public FilterOperationProcessorFactoryImpl(
      List<FilterOperationProcessor<?, ?, ? extends OperationNode, ?>> processors) {
    super(processors);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T process(FilterNodeTransformer<T> transformer, OperationNode source) {

    if (!getProcessorMap().containsKey(transformer.getClass()) || !getProcessorMap().get(
            transformer.getClass())
        .containsKey(source.getOperator().getClass())) {
      throw new UnsupportedOperationException(
          "No transformer from operator " + source.getOperator().getClass()
              + " to " + transformer.getTargetType()
              + " found");
    }

    return ((FilterOperationProcessor<FilterNodeTransformer<T>, ?, OperationNode, T>) getProcessorMap().get(
            transformer.getClass())
        .get(source.getOperator().getClass())).process(transformer,
        source);

  }

}
