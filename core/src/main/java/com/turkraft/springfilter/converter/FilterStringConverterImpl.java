package com.turkraft.springfilter.converter;

import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.ParseContext;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterStringTransformer;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class FilterStringConverterImpl implements FilterStringConverter {

  protected final FilterParser filterParser;

  protected final ConversionService conversionService;

  protected final FilterStringTransformer filterStringTransformer;

  public FilterStringConverterImpl(FilterParser filterParser,
      @Qualifier("sfConversionService") ConversionService conversionService) {
    this.filterParser = filterParser;
    this.conversionService = conversionService;
    this.filterStringTransformer = new FilterStringTransformer(this.conversionService);
  }

  @Override
  public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    if (source instanceof String) {
      return convert((String) source);
    }
    if (source instanceof FilterNode) {
      return convert((FilterNode) source);
    }
    throw new UnsupportedOperationException(
        "Can't convert " + sourceType.getName() + " to " + targetType.getName());
  }

  @Override
  public FilterNode convert(String node, @Nullable ParseContext ctx) {
    return filterParser.parse(node, ctx);
  }

  @Override
  public String convert(FilterNode source) {
    return filterStringTransformer.transform(source);
  }

  @Override
  public Set<ConvertiblePair> getConvertibleTypes() {
    return Set.of(new ConvertiblePair(String.class, FilterNode.class),
        new ConvertiblePair(FilterNode.class, String.class));
  }

}
