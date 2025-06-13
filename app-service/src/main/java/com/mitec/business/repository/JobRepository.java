package com.mitec.business.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.dto.JobTimeDto;
import com.mitec.business.model.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>{
	
	@Query(value = "select j.* from Job j join User u on j.user_id = u.id where u.username = :username and j.status <> :status order by j.check_in_time desc limit 5", nativeQuery = true)
	public List<Job> getByUsernameLimited5(String username, Integer status);
	
	@Query(value = "select j.* from Job j where j.serial_number = :serialNumber and j.status <> :status order by j.check_in_time desc limit 5", nativeQuery = true)
	public List<Job> getBySerialNumberLimited5(String serialNumber, Integer status);
	
	@Query(value = "select j from Job j where j.user.username = :username and j.status = :status")
	public List<Job> getByUsernameAndStatus(String username, Integer status);
	
	public Page<Job> findAll(Specification<Job> spec, Pageable pageable);
	
	@Query(value = "select j from Job j where j.atm.serialNumber = :serialNumber and j.status = :status")
	public List<Job> getJobProcessByATM(String serialNumber, Integer status);
	
	@Query(value = "select j.* from Job j where j.serial_number = :serialNumber and j.status = 0 and j.check_in_time between :startDate and :endDate", nativeQuery = true)
	public List<Job> getListBySerialNumber(String serialNumber, LocalDateTime startDate, LocalDateTime endDate);
	
	@Query(value = "select j.* from Job j where j.serial_number = :serialNumber and j.status != 1 order by j.id desc", nativeQuery = true)
	public List<Job> getListDetailBySerialNumber(String serialNumber);

	@Query(value = "select j.* from Job j where j.serial_number = :serialNumber and j.status != 1 and j.is_kpsc = true and j.check_in_time between :startDate and :endDate", nativeQuery = true)
	public List<Job> getListBySerialNumber1(String serialNumber, LocalDateTime startDate, LocalDateTime endDate);

	@Query(value = "select j.* from Job j where j.serial_number = :serialNumber and j.status != 1 and j.is_maintenance = true and j.check_in_time between :startDate and :endDate", nativeQuery = true)
	public List<Job> getListBySerialNumberMainten(String serialNumber, LocalDateTime startDate, LocalDateTime endDate);
	
	public List<Job> findAll(Specification<Job> spec);
	
	@Transactional
	public void deleteById(Long id);

	@Transactional
	public void deleteAll();
	
	@Query(value = "select new com.mitec.business.dto.JobTimeDto(j.id, j.atm.serialNumber, j.atm.address, j.checkInTime, "
			+ "j.checkOutTime, j.user.fullName, j.user.phoneNumber, j.isMaintenance, j.isKpsc) "
			+ "from Job j where j.id = :id")
	public JobTimeDto getJobTime(Long id);
}
