package com.turkraft.springfilter.transformer.processor.factory;

import org.springframework.stereotype.Service;

@Service
public class FilterNodeProcessorFactoriesImpl implements FilterNodeProcessorFactories {

  protected final FilterFunctionProcessorFactory functionProcessorFactory;

  protected final FilterPlaceholderProcessorFactory placeholderProcessorFactory;

  protected final FilterOperationProcessorFactory operationProcessorFactory;

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
