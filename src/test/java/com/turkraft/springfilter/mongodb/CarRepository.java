package com.turkraft.springfilter.mongodb;

import org.springframework.stereotype.Repository;
import com.turkraft.springfilter.repository.DocumentExecutor;

@Repository
public interface CarRepository extends DocumentExecutor<Car, Long> {
}
