package com.turkraft.springfilter.repository;

import com.turkraft.springfilter.parser.generator.bson.BsonGeneratorUtils;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

public class SimpleDocumentExecutor<T, ID> implements DocumentExecutor<T, ID> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;

    /**
     * Creates a new {@link SimpleDocumentExecutor} for the given {@link MongoEntityInformation} and {@link MongoTemplate}.
     *
     * @param metadata must not be {@literal null}.
     * @param mongoOperations must not be {@literal null}.
     */
    public SimpleDocumentExecutor(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {

        Assert.notNull(metadata, "MongoEntityInformation must not be null!");
        Assert.notNull(mongoOperations, "MongoOperations must not be null!");

        this.entityInformation = metadata;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Optional<T> findOne(Document doc) {
        return Optional.ofNullable(mongoOperations
                .findOne(BsonGeneratorUtils.getQueryFromDocument(doc), entityInformation.getJavaType(), entityInformation.getCollectionName()));
    }

    @Override
    public List<T> findAll(Document doc) {
        return mongoOperations.find(BsonGeneratorUtils.getQueryFromDocument(doc),
                entityInformation.getJavaType(), entityInformation.getCollectionName());
    }

    @Override
    public Page<T> findAll(Document doc, Pageable pageable) {
        Query query = BsonGeneratorUtils.getQueryFromDocument(doc).with(pageable);
        long count = mongoOperations.count(query, entityInformation.getJavaType(), entityInformation.getCollectionName());
        List<T> content = mongoOperations.find(query, entityInformation.getJavaType(), entityInformation.getCollectionName());
        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public List<T> findAll(Document doc, Sort sort) {
        return mongoOperations.find(BsonGeneratorUtils.getQueryFromDocument(doc).with(sort),
                entityInformation.getJavaType(), entityInformation.getCollectionName());
    }

    @Override
    public long count(Document doc) {
        return mongoOperations.count(BsonGeneratorUtils.getQueryFromDocument(doc),
                entityInformation.getJavaType(), entityInformation.getCollectionName());
    }
}
