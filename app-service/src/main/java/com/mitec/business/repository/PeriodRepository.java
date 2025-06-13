package com.mitec.business.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.Period;

@Repository
public interface PeriodRepository extends JpaRepository<Period, Long>{
	@Query(value = "select p.* from period p where p.contract_id = :contractId and now() between p.start_time and p.end_time limit 1", nativeQuery = true)
	public Period getPeriodByContractId(Long contractId);
	
	@Query(value = "select p.* from period p where p.contract_id = :contractId "
			+ "and (((p.start_time >= :startTime and p.start_time < :endTime)"
			+ "or (p.end_time > :startTime and p.end_time <= :endTime))"
			+ "or (p.start_time >= :startTime and p.start_time < :endTime)"
			+ "or (p.start_time <= :startTime and end_time > :endTime))", nativeQuery = true)
	public List<Period> getByContractAndTime(Long contractId, LocalDateTime startTime, LocalDateTime endTime);
}
