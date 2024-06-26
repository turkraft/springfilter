package com.turkraft.springfilter.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkraft.springfilter.helper.FieldTypeResolver;
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
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

public class FilterJsonNodeTransformer implements FilterNodeTransformer<JsonNode> {

  protected final ConversionService conversionService;

  protected final ObjectMapper objectMapper;

  protected final FilterNodeProcessorFactories filterNodeProcessorFactories;

  protected final FieldTypeResolver fieldTypeResolver;

  private final Class<?> entityType;

  private final Map<FilterNode, Class<?>> targetTypes = new HashMap<>();

  public FilterJsonNodeTransformer(ConversionService conversionService,
      ObjectMapper objectMapper,
      FilterNodeProcessorFactories filterNodeProcessorFactories,
      FieldTypeResolver fieldTypeResolver,
      Class<?> entityType) {
    this.conversionService = conversionService;
    this.objectMapper = objectMapper;
    this.filterNodeProcessorFactories = filterNodeProcessorFactories;
    this.entityType = entityType;
    this.fieldTypeResolver = fieldTypeResolver;
  }

  @Override
  public Class<JsonNode> getTargetType() {
    return JsonNode.class;
  }

  @Override
  public JsonNode transformField(FieldNode node) {
    if(node.getName().contains("$this")) {
      return objectMapper.createObjectNode().textNode("$" +node.getName());
    }
    Field field = fieldTypeResolver.getField(getEntityType(), node.getName());
    if (field.isAnnotationPresent(Id.class)) {
      return objectMapper.createObjectNode().textNode("$_id");
    }
    return objectMapper.createObjectNode().textNode("$" + node.getName());
  }

  @Override
  public JsonNode transformInput(InputNode node) {
    if (targetTypes.containsKey(node)) {
      return objectMapper.createObjectNode()
          .pojoNode(castIfNeeded(node.getValue(), targetTypes.get(node)));
    }
    return objectMapper.createObjectNode().pojoNode(node.getValue());
  }

  @Override
  public JsonNode transformPriority(PriorityNode node) {
    return transform(node.getNode());
  }

  @Override
  public JsonNode transformPlaceholder(PlaceholderNode node) {
    return filterNodeProcessorFactories.getPlaceholderProcessorFactory().process(this, node);
  }

  @Override
  public JsonNode transformFunction(FunctionNode node) {
    return filterNodeProcessorFactories.getFunctionProcessorFactory().process(this, node);
  }

  @Override
  public JsonNode transformCollection(CollectionNode node) {
    if (targetTypes.containsKey(node)) {
      node.getItems().forEach(i -> registerTargetType(i, targetTypes.get(node)));
    }
    return objectMapper.createArrayNode()
        .addAll(node.getItems().stream().map(this::transform).collect(
            Collectors.toList()));
  }

  @Override
  public JsonNode transformPrefixOperation(PrefixOperationNode node) {
    return filterNodeProcessorFactories.getOperationProcessorFactory().process(this, node);
  }

  @Override
  public JsonNode transformInfixOperation(InfixOperationNode node) {
    return filterNodeProcessorFactories.getOperationProcessorFactory().process(this, node);
  }

  @Override
  public JsonNode transformPostfixOperation(PostfixOperationNode node) {
    return filterNodeProcessorFactories.getOperationProcessorFactory().process(this, node);
  }

  public FilterJsonNodeTransformer registerTargetType(FilterNode node,
      @Nullable Class<?> targetType) {
    targetTypes.put(node, targetType);
    return this;
  }

  @Nullable
  public Class<?> getRegisteredTargetType(FilterNode node) {
    return targetTypes.get(node);
  }

  @Nullable
  private Object castIfNeeded(@Nullable Object value, @Nullable Class<?> targetType) {
    if (value != null && targetType != null && !targetType.isAssignableFrom(value.getClass())) {
      return conversionService.convert(value, targetType);
    }
    return value;
  }

  public Class<?> getEntityType() {
    return entityType;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

}
