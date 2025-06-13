package com.mitec.business.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.ReplacementAccessory;

@Repository
public interface ReplacementAccessoryRepository extends JpaRepository<ReplacementAccessory, Long>{
	
	@Query(value = "select ed.name as device, a.name as accessory, sum(ra.quantity) from replacement_accessory as ra "
			+ "join kpsc as k on ra.kpsc_id = k.id "
			+ "join accessory as a on ra.accessory_id = a.id "
			+ "join error_device as ed on ra.error_device_id = ed.id "
			+ "where k.job_id = :jobId group by ra.accessory_id, ed.name, a.name", nativeQuery = true)
	public List<Object[]> statisticalByJobId(Long jobId);
	
	@Query(value = "select ra from ReplacementAccessory ra where ra.kpsc.job.id = :jobId")
	public List<ReplacementAccessory> getByJobId(Long jobId);
	
	@Query(value = "select sum(ra.quantity) from replacement_accessory ra "
			+ "join kpsc k on ra.kpsc_id = k.id "
			+ "join job j on k.job_id = j.id "
			+ "where j.id = :jobId", nativeQuery = true)
	public Integer getSumQuantityByJobId(Long jobId);
	
	public Page<ReplacementAccessory> findAll(Specification<ReplacementAccessory> spec, Pageable pageable);
}
