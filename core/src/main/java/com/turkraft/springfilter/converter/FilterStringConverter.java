package com.turkraft.springfilter.converter;

import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.core.convert.converter.GenericConverter;

public interface FilterStringConverter extends GenericConverter {

  FilterNode convert(String source);

  String convert(FilterNode source);

}
