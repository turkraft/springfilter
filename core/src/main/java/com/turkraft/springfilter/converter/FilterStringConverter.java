package com.turkraft.springfilter.converter;

import com.turkraft.springfilter.parser.ParseContext;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;

public interface FilterStringConverter extends GenericConverter {

  FilterNode convert(String source, @Nullable ParseContext ctx);

  default FilterNode convert(String source) {
    return convert(source, null);
  }

  String convert(FilterNode source);

}
