package com.turkraft.springfilter.converter;

import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.CollectionNode;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.parser.node.InputNode;
import com.turkraft.springfilter.parser.node.PlaceholderNode;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.parser.node.PrefixOperationNode;
import com.turkraft.springfilter.parser.node.PriorityNode;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Service;

@Service
class FilterStringConverterImpl implements FilterStringConverter {

  private final FilterParser filterParser;

  private final ConversionService conversionService;

  public FilterStringConverterImpl(FilterParser filterParser,
      ConversionService conversionService) {
    this.filterParser = filterParser;
    this.conversionService = conversionService;
  }

  @Override
  public Class<String> getTargetType() {
    return String.class;
  }

  @Override
  public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    if (source instanceof String) {
      return convert((String) source);
    }
    if (source instanceof FilterNode) {
      return transform((FilterNode) source);
    }
    throw new UnsupportedOperationException(
        "Can't convert " + sourceType.getName() + " to " + targetType.getName());
  }

  @Override
  public FilterNode convert(String node) {
    return filterParser.parse(node);
  }

  @Override
  public String convert(FilterNode source) {
    return transform(source);
  }

  @Override
  public Set<ConvertiblePair> getConvertibleTypes() {
    return Set.of(new ConvertiblePair(String.class, FilterNode.class),
        new ConvertiblePair(FilterNode.class, String.class));
  }

  @Override
  public String transformField(FieldNode node) {
    return node.getName();
  }

  @Override
  public String transformInput(InputNode node) {
    return "'" + Objects.requireNonNull(
        conversionService.convert(node.getValue(), String.class),
        "Could not convert `" + node.getValue() + "` to string").replace("'", "\\'") + "'";
  }

  @Override
  public String transformPriority(PriorityNode node) {
    return "(" + transform(node.getNode()) + ")";
  }

  @Override
  public String transformPlaceholder(PlaceholderNode node) {
    return "`" + node.getPlaceholder().getName() + "`";
  }

  @Override
  public String transformFunction(FunctionNode node) {
    return node.getFunction().getName() + "("
        + node.getArguments().stream().map(this::transform).collect(Collectors.joining(", ")) + ")";
  }

  @Override
  public String transformCollection(CollectionNode node) {
    return "[" + node.getItems().stream().map(this::transform).collect(Collectors.joining(", "))
        + "]";
  }

  @Override
  public String transformPrefixOperation(PrefixOperationNode node) {
    return node.getOperator().getToken() + " " + transform(node.getRight());
  }

  @Override
  public String transformInfixOperation(InfixOperationNode node) {
    return transform(node.getLeft()) + " " + node.getOperator().getToken() + " " + transform(
        node.getRight());
  }

  @Override
  public String transformPostfixOperation(PostfixOperationNode node) {
    return transform(node.getLeft()) + " " + node.getOperator().getToken();
  }

}
