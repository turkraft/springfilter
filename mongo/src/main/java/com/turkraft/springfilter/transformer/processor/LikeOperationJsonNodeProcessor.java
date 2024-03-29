package com.turkraft.springfilter.transformer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.language.LikeOperator;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.parser.node.InputNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import java.lang.reflect.Field;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

@Component
public class LikeOperationJsonNodeProcessor implements
    FilterInfixOperationProcessor<FilterJsonNodeTransformer, JsonNode> {

  protected final FieldTypeResolver fieldTypeResolver;

  public LikeOperationJsonNodeProcessor(FieldTypeResolver fieldTypeResolver) {
    this.fieldTypeResolver = fieldTypeResolver;
  }

  @Override
  public Class<FilterJsonNodeTransformer> getTransformerType() {
    return FilterJsonNodeTransformer.class;
  }

  @Override
  public Class<LikeOperator> getDefinitionType() {
    return LikeOperator.class;
  }

  @Override
  public JsonNode process(FilterJsonNodeTransformer transformer,
      InfixOperationNode infixOperationNode) {

    return getRegexNode(transformer, infixOperationNode, "");

  }

  public ObjectNode getRegexNode(FilterJsonNodeTransformer transformer,
      InfixOperationNode infixOperationNode, String regexOptions) {

    transformer.registerTargetType(infixOperationNode, Boolean.class);

    transformer.registerTargetType(infixOperationNode.getLeft(), String.class);
    transformer.registerTargetType(infixOperationNode.getRight(), String.class);

    if (infixOperationNode.getLeft() instanceof FieldNode fieldNode
        && infixOperationNode.getRight() instanceof InputNode inputNode) {

      Field field = fieldTypeResolver.getField(transformer.getEntityType(), fieldNode.getName());

      if (field.isAnnotationPresent(Id.class) && field.getType().equals(String.class)) {

        /*
            $function: {
              body: "function (id) {return new RegExp(regex, options).test(input)}",
              args: [ "$_id" ],
              lang: "js"
            }
         */

        ObjectNode functionBody = transformer.getObjectMapper().createObjectNode();
        functionBody.set("lang", transformer.getObjectMapper().createObjectNode().textNode("js"));
        functionBody.set("args", transformer.getObjectMapper().createArrayNode()
            .add(transformer.getObjectMapper().createObjectNode().textNode("$_id")));
        functionBody.set("body", transformer.getObjectMapper().createObjectNode()
            .textNode("function(id) { return new RegExp('" + createRegex(
                String.valueOf(inputNode.getValue())).replace("'", "\\'") + "', '" + regexOptions
                + "').test(id) }"));

        return transformer.getObjectMapper().createObjectNode().set("$function", functionBody);

      }

    }

    ObjectNode regexOperation = transformer.getObjectMapper().createObjectNode();
    regexOperation.set("input", transformer.transform(infixOperationNode.getLeft()));
    regexOperation.set("regex",
        infixOperationNode.getRight() instanceof InputNode ? transformer.getObjectMapper()
            .createObjectNode().textNode(
                createRegex(String.valueOf(((InputNode) infixOperationNode.getRight()).getValue())))
            : transformer.transform(infixOperationNode.getRight()));
    regexOperation.set("options",
        transformer.getObjectMapper().createObjectNode().textNode(regexOptions));

    return transformer.getObjectMapper().createObjectNode().set("$regexMatch", regexOperation);

  }

  private String createRegex(String input) {
    if (!input.contains("*")) {
      return ".*" + sanitizeRegexInput(input) + ".*";
    }
    return sanitizeRegexInput(input.replace("*", "X_WILDCARD_X")).replace("X_WILDCARD_X", ".*");
  }

  private String sanitizeRegexInput(String input) {
    return input.replaceAll("[-.\\+*?\\[^\\]$(){}=!<>|:\\\\]", "\\\\$0");
  }

}
