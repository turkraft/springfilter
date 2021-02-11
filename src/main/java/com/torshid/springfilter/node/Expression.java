package com.torshid.springfilter.node;

import com.torshid.compiler.node.INode;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Expression implements INode, IPredicate {

}
