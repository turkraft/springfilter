package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import java.util.Map;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class MapFieldSupport {

  protected final FieldTypeResolver fieldTypeResolver;

  public MapFieldSupport(FieldTypeResolver fieldTypeResolver) {
    this.fieldTypeResolver = fieldTypeResolver;
  }

  public boolean isMapField(FilterJsonNodeTransformer transformer, FilterNode node) {
    if (!(node instanceof FieldNode fieldNode)) {
      return false;
    }
    Class<?> fieldType = fieldTypeResolver.resolve(transformer.getEntityType(),
        fieldNode.getName());
    return Map.class.isAssignableFrom(fieldType);
  }

  public JsonNode wrapMapField(FilterJsonNodeTransformer transformer, FilterNode node,
      JsonNode transformed) {
    if (!isMapField(transformer, node)) {
      return transformed;
    }
    return transformer
        .getObjectMapper()
        .createObjectNode()
        .set("$objectToArray", transformed);
  }

}
