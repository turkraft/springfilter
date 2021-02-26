package com.turkraft.springfilter.app;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayslipRepository
    extends CrudRepository<Payslip, Integer>, JpaSpecificationExecutor<Payslip> {

}
