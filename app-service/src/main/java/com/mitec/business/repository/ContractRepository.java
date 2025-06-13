package com.mitec.business.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>{
	
	@Query(value = "select c.* from contract c join contract_atm ca on c.id = ca.contract_id "
			+ "where ca.serial_number = :serialNumber and c.start_time < NOW() order by c.start_time desc limit 1",
			nativeQuery = true)
	public Optional<Contract> findCurrentContract(String serialNumber);
	
	public List<Contract> findAll(Specification<Contract> spec);
	
	@Query(value = "select c.* from contract c where NOW() between c.start_time and c.end_time "
			+ "or DATE_FORMAT(start_time, '%Y-%m-%d') = current_date() "
			+ "or DATE_FORMAT(end_time, '%Y-%m-%d') = current_date()", nativeQuery = true)
	public List<Contract> getCurrentContract();
	
	@Query(value = "select c.* from contract c where status = 1 and NOW() between start_time and end_time",
			nativeQuery = true)
	public List<Contract> findContractIsActive();
	
	@Query(value = "select c.* from contract c where status = 1 and NOW() between start_time and end_time "
			+ "and c.type <> :type",
			nativeQuery = true)
	public List<Contract> findContractIsActive(Integer type);
	
	@Query(value = "select c.* from contract c "
			+ "join contract_atm ca on c.id = ca.contract_id "
			+ "join atm a on a.serial_number = ca.serial_number "
			+ "where NOW() between c.start_time and c.end_time "
			+ "and a.serial_number = :serialNumber limit 1", nativeQuery = true)
	public Contract getCurrentContractByAtm(String serialNumber);
	
	@Query(value = "select c.* from contract c where c.name = :name limit 1", nativeQuery = true)
	public Contract getContractByNameLimit1(String name);
	
	
	public Page<Contract> findAll(Specification<Contract> spec, Pageable pageable);
	
	@Query(value = "select c.* from contract c join contract_atm ca on c.id = ca.contract_id where ca.serial_number = :serialNumber", nativeQuery = true)
	public List<Contract> getBySerialNumber(String serialNumber);
	
	public List<Contract> findByType(Integer type);
}
