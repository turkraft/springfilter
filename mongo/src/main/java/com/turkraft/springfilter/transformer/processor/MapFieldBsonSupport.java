package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import java.util.Map;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class MapFieldBsonSupport {

  protected final FieldTypeResolver fieldTypeResolver;

  public MapFieldBsonSupport(FieldTypeResolver fieldTypeResolver) {
    this.fieldTypeResolver = fieldTypeResolver;
  }

  public boolean isMapField(FilterBsonTransformer transformer, FilterNode node) {
    if (!(node instanceof FieldNode fieldNode)) {
      return false;
    }
    Class<?> fieldType = fieldTypeResolver.resolve(transformer.getEntityType(),
        fieldNode.getName());
    return Map.class.isAssignableFrom(fieldType);
  }

  public Object wrapMapField(FilterBsonTransformer transformer, FilterNode node,
      Object transformed) {
    if (!isMapField(transformer, node)) {
      return transformed;
    }
    return new Document("$objectToArray", transformed);
  }

}
