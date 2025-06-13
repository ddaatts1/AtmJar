package com.mitec.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.ContractAtm;

@Repository
public interface ContractAtmRepository extends JpaRepository<ContractAtm, Long>{

}
