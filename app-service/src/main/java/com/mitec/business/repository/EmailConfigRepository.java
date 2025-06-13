package com.mitec.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.EmailConfig;

@Repository
public interface EmailConfigRepository extends JpaRepository<EmailConfig, Long>{
	
	@Query(value = "select e.* from email_config e order by e.id limit 1", nativeQuery = true)
	EmailConfig findFirstOrderById();
}
