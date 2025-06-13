package com.mitec.business.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.mitec.business.model.Kpsc;

@Repository
public interface KpscRepository extends JpaRepository<Kpsc, Long>{
	
	@Query(value = "select k.* from Kpsc k join job j on k.job_id = j.id  where j.serial_number = :serial_number and j.status != 1 and j.is_kpsc = 1 and j.check_in_time between :startDate and :endDate", nativeQuery = true)
	public List<Kpsc> getErrorBy(String serial_number, LocalDateTime startDate, LocalDateTime endDate);
	
	@Query(value = "select k from Kpsc k where k.job.id = :jobId and k.errorDesc is not null")
	public List<Kpsc> getByJobId(Long jobId);
}
