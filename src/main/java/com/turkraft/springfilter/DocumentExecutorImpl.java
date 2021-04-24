package com.turkraft.springfilter;

import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.lang.Nullable;

public interface DocumentExecutorImpl<T, I> extends DocumentExecutor<T> {

  @Override
  default Optional<T> findOne(@Nullable Document doc) {
    return Optional.ofNullable(getMongoOperations().findOne(FilterUtils.getQueryFromDocument(doc),
        getMetadata().getJavaType()));
  }

  @Override
  default List<T> findAll(@Nullable Document doc) {
    return getMongoOperations().find(FilterUtils.getQueryFromDocument(doc),
        getMetadata().getJavaType());
  }

  @Override
  default Page<T> findAll(@Nullable Document doc, Pageable pageable) {
    return new PageImpl<>(getMongoOperations()
        .find(FilterUtils.getQueryFromDocument(doc).with(pageable), getMetadata().getJavaType()));
  }

  @Override
  default List<T> findAll(@Nullable Document doc, Sort sort) {
    return getMongoOperations().find(FilterUtils.getQueryFromDocument(doc).with(sort),
        getMetadata().getJavaType());
  }

  @Override
  default long count(@Nullable Document doc) {
    return getMongoOperations().count(FilterUtils.getQueryFromDocument(doc),
        getMetadata().getJavaType());
  }

  MongoEntityInformation<T, I> getMetadata();

  MongoOperations getMongoOperations();

}
