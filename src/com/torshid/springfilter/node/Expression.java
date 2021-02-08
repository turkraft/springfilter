package com.torshid.springfilter.node;

import com.torshid.compiler.node.Node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Expression extends Node {

}
