package com.turkraft.springfilter.transformer.processor;

import com.turkraft.springfilter.helper.DbRefFieldSupport;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.language.LikeOperator;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.parser.node.InputNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import java.lang.reflect.Field;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

@Component
public class LikeOperationJsonNodeProcessor implements
    FilterInfixOperationProcessor<FilterJsonNodeTransformer, JsonNode> {

  protected final FieldTypeResolver fieldTypeResolver;

  protected final DbRefFieldSupport dbRefFieldSupport;

  public LikeOperationJsonNodeProcessor(FieldTypeResolver fieldTypeResolver,
      DbRefFieldSupport dbRefFieldSupport) {
    this.fieldTypeResolver = fieldTypeResolver;
    this.dbRefFieldSupport = dbRefFieldSupport;
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

      if (field != null && field.isAnnotationPresent(Id.class) && field
          .getType()
          .equals(String.class)) {

        /*
            $function: {
              body: "function (id) {return new RegExp(regex, options).test(input)}",
              args: [ "$_id" ],
              lang: "js"
            }
         */

        ObjectNode functionBody = transformer
            .getObjectMapper()
            .createObjectNode();
        functionBody.set("lang", transformer
            .getObjectMapper()
            .getNodeFactory()
            .stringNode("js"));
        functionBody.set("args", transformer
            .getObjectMapper()
            .createArrayNode()
            .add(transformer
                .getObjectMapper()
                .getNodeFactory()
                .stringNode("$_id")));
        functionBody.set("body", transformer
            .getObjectMapper()
            .getNodeFactory()
            .stringNode("function(id) { return new RegExp('" + createRegex(
                String.valueOf(inputNode.getValue())).replace("'", "\\'") + "', '" + regexOptions
                + "').test(id) }"));

        return transformer
            .getObjectMapper()
            .createObjectNode()
            .set("$function", functionBody);

      }

    }

    JsonNode leftResult = transformer.transform(infixOperationNode.getLeft());

    JsonNode regexValue = infixOperationNode.getRight() instanceof InputNode ? transformer
        .getObjectMapper()
        .getNodeFactory()
        .stringNode(
            createRegex(String.valueOf(((InputNode) infixOperationNode.getRight()).getValue())))
        : transformer.transform(infixOperationNode.getRight());

    JsonNode optionsValue = transformer
        .getObjectMapper()
        .getNodeFactory()
        .stringNode(regexOptions);

    if (dbRefFieldSupport.isCollectionDbRefDollarField(transformer,
        infixOperationNode.getLeft())) {

      ObjectNode elementRegex = transformer
          .getObjectMapper()
          .createObjectNode();
      elementRegex.set("input", dbRefFieldSupport.regexInput(transformer, transformer
          .getObjectMapper()
          .getNodeFactory()
          .stringNode("$$this")));
      elementRegex.set("regex", regexValue);
      elementRegex.set("options", optionsValue);

      return dbRefFieldSupport.anyElement(transformer, leftResult, transformer
          .getObjectMapper()
          .createObjectNode()
          .set("$regexMatch", elementRegex), false);

    }

    ObjectNode regexOperation = transformer
        .getObjectMapper()
        .createObjectNode();
    regexOperation.set("input",
        dbRefFieldSupport.isReferenceDollarField(transformer, infixOperationNode.getLeft())
            ? dbRefFieldSupport.regexInput(transformer, leftResult)
            : leftResult);
    regexOperation.set("regex", regexValue);
    regexOperation.set("options", optionsValue);

    return transformer
        .getObjectMapper()
        .createObjectNode()
        .set("$regexMatch", regexOperation);

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
