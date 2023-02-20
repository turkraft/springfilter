package com.turkraft.springfilter.transformer.processor.factory;

public interface FilterNodeProcessorFactories {

  FilterFunctionProcessorFactory getFunctionProcessorFactory();

  FilterPlaceholderProcessorFactory getPlaceholderProcessorFactory();

  FilterOperationProcessorFactory getOperationProcessorFactory();

}
