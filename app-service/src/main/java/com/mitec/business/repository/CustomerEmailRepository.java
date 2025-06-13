package com.mitec.business.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.CustomerEmail;

@Repository
public interface CustomerEmailRepository extends JpaRepository<CustomerEmail, Long>{
	
	@Query(value = "select ce from CustomerEmail ce where ce.contract.id = :contractId and ce.type = 2")
	public List<CustomerEmail> getByContractId(Long contractId);
	
	@Query(value = "select ce.* from customer_email ce where ce.type = 1 order by ce.id limit 1", nativeQuery = true)
	public Optional<CustomerEmail> getInternalEmail();
}
