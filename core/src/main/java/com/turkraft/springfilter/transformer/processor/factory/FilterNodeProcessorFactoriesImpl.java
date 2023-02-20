package com.turkraft.springfilter.transformer.processor.factory;

import org.springframework.stereotype.Service;

@Service
public class FilterNodeProcessorFactoriesImpl implements FilterNodeProcessorFactories {

  private final FilterFunctionProcessorFactory functionProcessorFactory;

  private final FilterPlaceholderProcessorFactory placeholderProcessorFactory;

  private final FilterOperationProcessorFactory operationProcessorFactory;

  public FilterNodeProcessorFactoriesImpl(
      FilterFunctionProcessorFactory functionProcessorFactory,
      FilterPlaceholderProcessorFactory placeholderProcessorFactory,
      FilterOperationProcessorFactory operationProcessorFactory) {
    this.functionProcessorFactory = functionProcessorFactory;
    this.placeholderProcessorFactory = placeholderProcessorFactory;
    this.operationProcessorFactory = operationProcessorFactory;
  }
  
  @Override
  public FilterFunctionProcessorFactory getFunctionProcessorFactory() {
    return functionProcessorFactory;
  }

  @Override
  public FilterPlaceholderProcessorFactory getPlaceholderProcessorFactory() {
    return placeholderProcessorFactory;
  }

  @Override
  public FilterOperationProcessorFactory getOperationProcessorFactory() {
    return operationProcessorFactory;
  }

}
