package com.turkraft.springfilter.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.turkraft.springfilter.TestEntity;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TransformerUtilsImplTest {

    @Autowired
    private ConversionService conversionService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FilterBuilder fb;
    @Autowired
    private FilterNodeProcessorFactories filterNodeProcessorFactories;
    @Autowired
    private FieldTypeResolver fieldTypeResolver;
    private FilterJsonNodeTransformer transformer;
    @Mock
    private JsonNode node;
    @Mock
    private InfixOperationNode source;
    private TransformerUtils transformerUtils;

    @BeforeEach
    void setUp() {
        transformerUtils = new TransformerUtilsImpl(fieldTypeResolver);
        transformer = new FilterJsonNodeTransformer(conversionService, objectMapper, filterNodeProcessorFactories, fieldTypeResolver,
                TestEntity.class);
    }

    @Test
    void simplifyShouldReturnNodeWhenNoAndOrOrPresent() {
        when(node.has("$and")).thenReturn(false);
        when(node.has("$or")).thenReturn(false);

        JsonNode result = transformerUtils.simplify(transformer, node);

        assertSame(node, result);
    }

    @Test
    void simplifyShouldMergeAndElementsWhenAndPresent() {
        // {$and: [ { $and: [ {}, {} ] }, {} , {}]}
        // =>
        // {$and: [ {}, {}, {}, {}]}
        //arrange
        ArrayNode arrayNode = transformer.getObjectMapper().createArrayNode();
        arrayNode.add(mock(ObjectNode.class));
        arrayNode.add(mock(ObjectNode.class));
        ArrayNode innerAndArray = transformer.getObjectMapper().createArrayNode();
        innerAndArray.add(mock(ObjectNode.class));
        innerAndArray.add(mock(ObjectNode.class));
        ObjectNode innerAnd = transformer.getObjectMapper().createObjectNode();
        innerAnd.set("$and", innerAndArray);
        arrayNode.add(innerAnd);

        ObjectNode input = transformer.getObjectMapper().createObjectNode();
        input.set("$and", arrayNode);

        //act
        JsonNode result = transformerUtils.simplify(transformer, input);

        //assert
        assertTrue(result.has("$and"));
        assertEquals(4, result.get("$and").size());
    }

    @Test
    void simplifyShouldMergeOrElementsWhenOrPresent() {
        // {$or: [ { $or: [ {}, {} ] }, {} , {}]}
        // =>
        // {$or: [ {}, {}, {}, {}]}
        //arrange
        ArrayNode arrayNode = transformer.getObjectMapper().createArrayNode();
        arrayNode.add(mock(ObjectNode.class));
        arrayNode.add(mock(ObjectNode.class));
        ArrayNode innerOrArray = transformer.getObjectMapper().createArrayNode();
        innerOrArray.add(mock(ObjectNode.class));
        innerOrArray.add(mock(ObjectNode.class));
        ObjectNode innerOr = transformer.getObjectMapper().createObjectNode();
        innerOr.set("$or", innerOrArray);
        arrayNode.add(innerOr);

        ObjectNode input = transformer.getObjectMapper().createObjectNode();
        input.set("$or", arrayNode);

        //act
        JsonNode result = transformerUtils.simplify(transformer, input);

        //assert
        assertTrue(result.has("$or"));
        assertEquals(4, result.get("$or").size());
    }

    @Test
    void simplifyShouldMergeAndAndOrElementsWhenAndAndOrPresent() {
        // {$and: [ { $and: [ { $or: [ {}, {} ] }, {} ] }, {} , {}]}
        // =>
        // {$and: [ { $or: [ {}, {} ] }, {} , {}, {}]}
        //arrange
        ArrayNode innerOrArray = transformer.getObjectMapper().createArrayNode();
        innerOrArray.add(mock(ObjectNode.class));
        innerOrArray.add(mock(ObjectNode.class));
        ObjectNode innerOr = transformer.getObjectMapper().createObjectNode();
        innerOr.set("$or", innerOrArray);

        ArrayNode innerAndArray = transformer.getObjectMapper().createArrayNode();
        innerAndArray.add(innerOr);
        innerAndArray.add(mock(ObjectNode.class));
        ObjectNode innerAnd = transformer.getObjectMapper().createObjectNode();
        innerAnd.set("$and", innerAndArray);
        ObjectNode input = transformer.getObjectMapper().createObjectNode();
        ArrayNode arrayNode = transformer.getObjectMapper().createArrayNode();

        arrayNode.add(mock(ObjectNode.class));
        arrayNode.add(mock(ObjectNode.class));
        arrayNode.add(innerAnd);
        input.set("$and", arrayNode);

        //act
        JsonNode result = transformerUtils.simplify(transformer, input);

        //assert
        assertTrue(result.has("$and"));
        assertEquals(4, result.get("$and").size());
        for (JsonNode node : result.get("$and")) {
            if (node.has("$or")) {
                assertEquals(2, node.get("$or").size());
            }
        }
    }

    @Test
    void simplifyShouldNotMergeAnyElemenTrueWithDifferentMapping() {
        // {$and: [
        // $anyElementTrue: { $map: { input: { $ifNull: [ "$mapping1", [] ] }, as: "this", in: filter } },
        // $anyElementTrue: { $map: { input: { $ifNull: [ "$mapping2", [] ] }, as: "this", in: filter } },
        // {}
        // ]}
        // => no change
        //arrange
        ObjectNode filter = transformer.getObjectMapper().createObjectNode();
        filter.put("$key", "value");
        ObjectNode filter2 = transformer.getObjectMapper().createObjectNode();
        filter2.put("$key", "value2");
        JsonNode anyElementsNode1 = getAnyElementsNode("$mapping1", filter);
        JsonNode anyElementsNode2 = getAnyElementsNode("$mapping2", filter2);
        ObjectNode input = transformer.getObjectMapper().createObjectNode();
        ArrayNode arrayNode = transformer.getObjectMapper().createArrayNode();
        arrayNode.add(mock(ObjectNode.class));
        arrayNode.add(anyElementsNode1);
        arrayNode.add(anyElementsNode2);
        input.set("$and", arrayNode);


        //act
        JsonNode result = transformerUtils.simplify(transformer, input);

        //assert
        assertTrue(result.has("$and"));
        assertEquals(3, result.get("$and").size());
        int count = 0;
        Set<String> values = new HashSet<>();
        for (JsonNode node : result.get("$and")) {
            if (node.has("$anyElementTrue")) {
                assertTrue(node.has("$anyElementTrue"));
                assertTrue(node.get("$anyElementTrue").get("$map").get("in").has("$key"));
                values.add(node.get("$anyElementTrue").get("$map").get("in").get("$key").asText());
                count++;
            }
        }
        assertEquals(2, count);
        assertEquals(2, values.size());
    }

    @Test
    void simplifyShouldMergeAnyElemenTrueWithSameMapping() {
        // {$and: [
        // {$anyElementTrue: { $map: { input: { $ifNull: [ "$mapping", [] ] }, as: "this", in: filter } }},
        // {$anyElementTrue: { $map: { input: { $ifNull: [ "$mapping", [] ] }, as: "this", in: filter } }},
        // {}
        // ]}
        // =>
        //{$and: [
        // {$anyElementTrue: { $map: { input: { $ifNull: [ "$mapping", [] ] }, as: "this", in: { $and: [ filter, filter ] } } },
        // {}
        //]}
        //arrange
        ObjectNode filter = transformer.getObjectMapper().createObjectNode();
        filter.put("$key", "value");
        ObjectNode filter2 = transformer.getObjectMapper().createObjectNode();
        filter2.put("$key", "value2");
        JsonNode anyElementsNode1 = getAnyElementsNode("$mapping", filter);
        JsonNode anyElementsNode2 = getAnyElementsNode("$mapping", filter2);
        ObjectNode input = transformer.getObjectMapper().createObjectNode();
        ArrayNode arrayNode = transformer.getObjectMapper().createArrayNode();
        arrayNode.add(mock(ObjectNode.class));
        arrayNode.add(anyElementsNode1);
        arrayNode.add(anyElementsNode2);
        input.set("$and", arrayNode);


        //act
        JsonNode result = transformerUtils.simplify(transformer, input);

        //assert
        assertTrue(result.has("$and"));
        assertEquals(2, result.get("$and").size());
        int count = 0;
        for (JsonNode node : result.get("$and")) {
            if (node.has("$anyElementTrue")) {
                assertTrue(node.has("$anyElementTrue"));
                assertEquals(2, node.get("$anyElementTrue").get("$map").get("in").get("$and").size());
                count++;
            }
        }
        assertEquals(1, count);
    }

    @Test
    void simplifyShouldMergeAnyElemenTrueWithSameMappingAndRemoveSingleElementAndOrOrs() {
        // {$and: [
        // $anyElementTrue: { $map: { input: { $ifNull: [ "$mapping", [] ] }, as: "this", in: filter1 } },
        // $anyElementTrue: { $map: { input: { $ifNull: [ "$mapping", [] ] }, as: "this", in: filter2 } },
        // ]}
        // =>
        // {$anyElementTrue: { $map: { input: { $ifNull: [ "$mapping", [] ] }, as: "this", in: { $and: [ filter1, filter2 ] } } }}
        //arrange
        ObjectNode filter = transformer.getObjectMapper().createObjectNode();
        filter.put("$key", "value");
        ObjectNode filter2 = transformer.getObjectMapper().createObjectNode();
        filter2.put("$key", "value2");
        JsonNode anyElementsNode1 = getAnyElementsNode("$mapping", filter);
        JsonNode anyElementsNode2 = getAnyElementsNode("$mapping", filter2);
        ObjectNode input = transformer.getObjectMapper().createObjectNode();
        ArrayNode arrayNode = transformer.getObjectMapper().createArrayNode();
        arrayNode.add(anyElementsNode1);
        arrayNode.add(anyElementsNode2);
        input.set("$and", arrayNode);


        //act
        JsonNode result = transformerUtils.simplify(transformer, input);

        //assert
        assertTrue(result.has("$anyElementTrue"));
        assertEquals(2, result.get("$anyElementTrue").get("$map").get("in").get("$and").size());
    }

    JsonNode getAnyElementsNode(String mapping, JsonNode filter) {
        //{
        //    "$anyElementTrue": {
        //    "$map": {
        //        "input": {
        //            "$ifNull": [
        //            "$mapping",
        //                        []
        //                    ]
        //        },
        //        "as": "this",
        //        "in": filter
        //    }
        //}
        ObjectNode result = objectMapper.createObjectNode();
        ObjectNode anyElementTrue = objectMapper.createObjectNode();
        ObjectNode map = objectMapper.createObjectNode();
        var input = objectMapper.createObjectNode();
        ArrayNode ifNull = objectMapper.createArrayNode();
        ifNull.add(mapping);
        ifNull.add(objectMapper.createArrayNode());
        input.set("$ifNull", ifNull);
        map.set("input", input);
        map.put("as", "this");
        map.set("in", filter);
        anyElementTrue.set("$map", map);
        return result.set("$anyElementTrue", anyElementTrue);
    }

    @Configuration
    @ComponentScan("com.turkraft.springfilter")
    static class Config {

    }
}
