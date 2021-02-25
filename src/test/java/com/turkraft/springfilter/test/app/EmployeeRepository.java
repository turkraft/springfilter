package com.turkraft.springfilter.test.app;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository
    extends CrudRepository<Employee, Integer>, JpaSpecificationExecutor<Employee> {

  @Query(value = "SELECT * FROM Employee ORDER BY rand() LIMIT 1;", nativeQuery = true)
  Employee random();

}
