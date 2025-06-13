package com.mitec.business.specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.mitec.business.model.ATM;
import com.mitec.business.model.Accessory;
import com.mitec.business.model.Bank;
import com.mitec.business.model.Contract;
import com.mitec.business.model.Department;
import com.mitec.business.model.Device;
import com.mitec.business.model.Error;
import com.mitec.business.model.ErrorDevice;
import com.mitec.business.model.Job;
import com.mitec.business.model.JobPerform;
import com.mitec.business.model.Region;
import com.mitec.business.model.Series;
import com.mitec.business.model.Statistical;
import com.mitec.business.model.User;
import com.mitec.business.utils.JobStatusEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ObjectSpecification {
	private static final String CONTRACT_ID = "contractId";
	private static final String ID = "id";
	private static final String USERNAME = "username";
	private static final String CHECK_IN_TIME = "checkInTime";
	private static final String SERIAL_NUMBER = "serialNumber";
	private static final String ADDRESS = "address";
	private static final String JOB_COMPLETE_TIME = "jobCompleteTime";
	
	public Specification<User> searchUser(final String username) {
		log.debug("Processing searchUser specification()...");
		return(root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotBlank(username)) {
				predicates.add(cb.like(root.get(USERNAME), "%" + username + "%"));
			}
			
			predicates.add(cb.notEqual(root.get(USERNAME), "admin"));
			
			cq.orderBy(cb.desc(root.get(ID)));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	public Specification<Bank> searchBank() {
		log.debug("Processing searchBank specification()...");
		return(root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			cq.orderBy(cb.desc(root.get(ID)));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	public Specification<Region> searchRegion() {
		log.debug("Processing searchRegion specification()...");
		return(root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			cq.orderBy(cb.desc(root.get(ID)));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}

	public Specification<Series> searchSeries() {
		log.debug("Processing searchSeries specification()...");
		return(root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			cq.orderBy(cb.desc(root.get(ID)));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}

	public Specification<Device> searchDevice() {
		log.debug("Processing searchDevice specification()...");
		return(root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			cq.orderBy(cb.desc(root.get(ID)));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	public Specification<Error> searchError() {
		log.debug("Processing searchError specification()...");
		return(root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			cq.orderBy(cb.desc(root.get(ID)));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	public Specification<ErrorDevice> searchErrorDevice() {
		log.debug("Processing searchErrorDevice specification()...");
		return(root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			cq.orderBy(cb.desc(root.get(ID)));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	public Specification<Accessory> searchAccessory() {
		log.debug("Processing searchAccessory specification()...");
		return(root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			cq.orderBy(cb.desc(root.get(ID)));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}

	public Specification<JobPerform> searchJobPerform() {
		log.debug("Processing searchJobPerform specification()...");
		return(root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			cq.orderBy(cb.desc(root.get(ID)));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public Specification<Job> searchJob(final String username, final LocalDateTime startTime, final LocalDateTime endTime, 
			final Integer status, final Long serviceType) {
		log.debug("Processing searchJob specification()...");
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotBlank(username)) {
				predicates.add(cb.like(root.get("user").get(USERNAME), "%" + username + "%"));
			}
			if (startTime != null && endTime == null) {
				predicates.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get(CHECK_IN_TIME), startTime));
			}else if (startTime != null && endTime != null) {
				predicates.add(cb.between(root.<LocalDateTime>get(CHECK_IN_TIME), startTime, endTime));
			}else if (startTime == null && endTime != null) {
				predicates.add(cb.lessThanOrEqualTo(root.<LocalDateTime>get(CHECK_IN_TIME), endTime));
			}
			if (status != null) {
				predicates.add(cb.equal(root.get("status"), status));
			}
			if (serviceType != null) {
				if (serviceType == 0) {
					predicates.add(cb.notEqual(root.join("kpscs").get("jobPerform").get("id"), 5));
				}
				else if (serviceType == 1) {
					predicates.add(cb.equal(root.join("kpscs").get("jobPerform").get("id"), 5));
				}
			}
			cq.orderBy(cb.desc(root.get(CHECK_IN_TIME)));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public Specification<Job> lookup(final String serial, final String address, final LocalDateTime startTime, final LocalDateTime endTime, 
			final Integer status, final Integer idAccessory, final Long serviceType) {
		log.debug("Processing lookup specification()...");
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotBlank(serial)) {
				predicates.add(cb.like(root.get("atm").get(SERIAL_NUMBER), "%" + serial + "%"));
			}
			if (StringUtils.isNotBlank(address)) {
				predicates.add(cb.like(root.get("atm").get(ADDRESS), "%" + address + "%"));
			}
			if (startTime != null && endTime == null) {
				predicates.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get(CHECK_IN_TIME), startTime));
			}else if (startTime != null && endTime != null) {
				predicates.add(cb.between(root.<LocalDateTime>get(CHECK_IN_TIME), startTime, endTime));
			}else if (startTime == null && endTime != null) {
				predicates.add(cb.lessThanOrEqualTo(root.<LocalDateTime>get(CHECK_IN_TIME), endTime));
			}
			if (status != null) {
				predicates.add(cb.equal(root.get("status"), status));
			}
			
			if (idAccessory != null ) {
				predicates.add(cb.equal(root.join("kpscs").join("replacementAccessories").get("accessory").get("id"), idAccessory));
			}
			if (serviceType != null) {
				if (serviceType == 0) {
					predicates.add(cb.notEqual(root.join("kpscs").get("jobPerform").get("id"), 5));
				}
				else if (serviceType == 1) {
					predicates.add(cb.equal(root.join("kpscs").get("jobPerform").get("id"), 5));
				}
			}
			cq.orderBy(cb.desc(root.get(CHECK_IN_TIME)));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public Specification<Statistical> totalAmount(final Long contractId, final Long regionId, 
			final Long departmentId, final LocalDateTime startTime, final LocalDateTime endTime) {
		log.debug("Processing totalAmount specification()...");
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.notEqual(root.get("status"), JobStatusEnum.IN_PROCESS.getKey()));
			
			if (contractId != null) {
				predicates.add(cb.equal(root.get(CONTRACT_ID), contractId));
			}
			if (regionId != null) {
				predicates.add(cb.equal(root.get("regionId"), regionId));
			}
			if (departmentId != null) {
				predicates.add(cb.equal(root.get("departmentId"), departmentId));
			}
			if (startTime != null) {
				predicates.add(cb.between(root.<LocalDateTime>get(JOB_COMPLETE_TIME), startTime, endTime));
			}
			
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public Specification<ATM> totalATM(final Long contractId, final Long regionId, final Long departmentId) {
		log.debug("Processing totalAmount specification()...");
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (contractId != null) {
				Root<Contract> contract = cq.from(Contract.class);
				Expression<Collection<ATM>> atms = contract.get("atms");
				predicates.add(cb.and(cb.equal(contract.get(ID), contractId)));
				predicates.add(cb.and(cb.isMember(root, atms)));
			}
			if (regionId != null) {
				predicates.add(cb.equal(root.get("region").get(ID), regionId));
			}
			if (departmentId != null) {
				predicates.add(cb.equal(root.get("department").get(ID), departmentId));
			}
			
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public Specification<Statistical> totalAmount(final String username,
			final LocalDateTime startTime, final LocalDateTime endTime) {
		
		log.debug("Processing totalAmountPerPerson specification()...");
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotBlank(username)) {
				predicates.add(cb.like(root.get(USERNAME), "%" + username + "%"));
			}
			if (startTime != null) {
				predicates.add(cb.between(root.<LocalDateTime>get(JOB_COMPLETE_TIME), startTime, endTime));
			}
			
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public Specification<Statistical> cyclicalStatistics(final Long contractId, 
			final LocalDateTime startTime, final LocalDateTime endTime) {
		log.debug("Processing cyclicalStatistics specification()...");
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (contractId != null) {
				predicates.add(cb.equal(root.get(CONTRACT_ID), contractId));
			}
			if (startTime != null) {
				predicates.add(cb.between(root.<LocalDateTime>get(JOB_COMPLETE_TIME), startTime, endTime));
			}
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public Specification<ATM> searchATM(final String serialNumber, final String address,
			final Long series, final Long region, final Long department) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotBlank(serialNumber)) {
				predicates.add(cb.like(root.get(SERIAL_NUMBER), "%" + serialNumber + "%"));
			}
			if (StringUtils.isNotBlank(address)) {
				predicates.add(cb.like(root.get(ADDRESS), "%" + address + "%"));
			}
			if (series != null) {
				predicates.add(cb.equal(root.get("series").get(ID), series));
			}
			if (department != null) {
				predicates.add(cb.equal(root.get("department").get(ID), department));
			}
			if (region != null) {
				predicates.add(cb.equal(root.get("region").get(ID), region));
			}
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	public Specification<ATM> searchATMBySerial(final String input) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotBlank(input)) {
				Predicate predicateSerial = cb.like(root.get(SERIAL_NUMBER), "%" + input +"%");
				Predicate predicateAddress = cb.like(root.get(ADDRESS), "%" + input +"%");
				predicates.add(cb.or(predicateSerial, predicateAddress));
			}
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public Specification<Contract> searchContract(final Integer type) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("type"), type));
			cq.orderBy(cb.asc(root.get("name")));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public Specification<Job> getAtmHistory(final String serialNumber) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("atm").get(SERIAL_NUMBER), serialNumber));
			predicates.add(cb.notEqual(root.get("status"), JobStatusEnum.IN_PROCESS.getKey()));
			cq.orderBy(cb.desc(root.get(CHECK_IN_TIME)));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public Specification<Job> getPersonalHistory(final String username, final String search) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("user").get(USERNAME), username));
			predicates.add(cb.notEqual(root.get("status"), JobStatusEnum.IN_PROCESS.getKey()));
			
			if (StringUtils.isNotBlank(search)) {
				predicates.add(cb.or(cb.like(root.get("atm").get(ADDRESS), "%" + search + "%"), cb.like(root.get("atm").get(SERIAL_NUMBER), "%" + search + "%")));
			}
			
			cq.orderBy(cb.desc(root.get(CHECK_IN_TIME)));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public Specification<ATM> searchATM(final String search) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			
			if (StringUtils.isNotBlank(search)) {
				predicates.add(cb.or(cb.like(root.get(ADDRESS), "%" + search + "%"), cb.like(root.get(SERIAL_NUMBER), "%" + search + "%")));
			}
			
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}

	public Specification<Accessory> searchAccessoryStatistical(final String contractID, final Long accessory_id) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
//			if (StringUtils.isNotBlank(contractID)) {
//				predicates.add(cb.equal(root.get("contract_atm").get("contract_id"), Long.parseLong(contractID)));
				
//			}
			
//			if (startTime != null && endTime == null) {
//				predicates.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get(CHECK_IN_TIME), startTime));
//			}else if (startTime != null && endTime != null) {
//				predicates.add(cb.between(root.<LocalDateTime>get(CHECK_IN_TIME), startTime, endTime));
//			}else if (startTime == null && endTime != null) {
//				predicates.add(cb.lessThanOrEqualTo(root.<LocalDateTime>get(CHECK_IN_TIME), endTime));
//			}
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public Specification<Department> getDepartmentForStatistical(final Long regionId, final Long departmentId) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			
			if (regionId != null) {
				predicates.add(cb.equal(root.get("region").get(ID), regionId));
			}
			if (departmentId != null) {
				predicates.add(cb.equal(root.get(ID), departmentId));
			}
			
			return cb.and(predicates.stream().toArray(Predicate[]::new)); 
		};
	}
	
	public Specification<Region> getDepartmentForExport(final Long regionId, final Department department) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			final Path<Department> departments = root.<Department> get("departments");
			
			if (regionId != null) {
				predicates.add(cb.equal(root.get(ID), regionId));
			}
			if (department != null) {
				predicates.add(departments.in(department));
			}
			
			return cb.and(predicates.stream().toArray(Predicate[]::new)); 
		};
	}
}
