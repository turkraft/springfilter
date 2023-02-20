package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.parser.node.FilterNode;

public interface StepWithResult extends Step {

  FilterNode get();

}
