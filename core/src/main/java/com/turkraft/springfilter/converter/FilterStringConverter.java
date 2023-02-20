package com.turkraft.springfilter.converter;

import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterNodeTransformer;
import org.springframework.core.convert.converter.GenericConverter;

public interface FilterStringConverter extends FilterNodeTransformer<String>, GenericConverter {

  FilterNode convert(String source);

  String convert(FilterNode source);

}
