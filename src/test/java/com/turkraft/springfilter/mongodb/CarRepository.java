package com.turkraft.springfilter.mongodb;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;
import com.turkraft.springfilter.repository.DocumentExecutor;

@Repository
public class CarRepository implements DocumentExecutor<Car, Long> {

  private final MongoEntityInformation<Car, Long> metadata;
  private final MongoOperations mongoOperations;

  public CarRepository(MongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
    this.metadata = new MongoRepositoryFactory(mongoOperations).getEntityInformation(Car.class);
  }

  @Override
  public MongoEntityInformation<Car, Long> getMetadata() {
    return metadata;
  }

  @Override
  public MongoOperations getMongoOperations() {
    return mongoOperations;
  }

}
