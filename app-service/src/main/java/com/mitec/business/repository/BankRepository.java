package com.mitec.business.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long>{

	@Query(value = "select b.* from bank b where b.name = :name limit 1", nativeQuery = true)
	public Bank getByNameLimit1(String name);
	
	public Page<Bank> findAll(Specification<Bank> spec, Pageable pageable);
}
