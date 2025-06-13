package com.mitec.business.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.mitec.business.dto.ATMDto;
import com.mitec.business.dto.AccessoryDto;
import com.mitec.business.dto.BankDto;
import com.mitec.business.dto.ContractAtmDto;
import com.mitec.business.dto.ContractDto;
import com.mitec.business.dto.CustomerEmailDto;
import com.mitec.business.dto.DepartmentDto;
import com.mitec.business.dto.DeviceDto;
import com.mitec.business.dto.ErrorDeviceDto;
import com.mitec.business.dto.ErrorDto;
import com.mitec.business.dto.JobDto;
import com.mitec.business.dto.JobPerformDto;
import com.mitec.business.dto.KpscDto;
import com.mitec.business.dto.RegionDto;
import com.mitec.business.dto.ReplacementAccessoryDto;
import com.mitec.business.dto.RoleDto;
import com.mitec.business.dto.SeriesDto;
import com.mitec.business.dto.UserDto;
import com.mitec.business.model.ATM;
import com.mitec.business.model.Accessory;
import com.mitec.business.model.Bank;
import com.mitec.business.model.Contract;
import com.mitec.business.model.ContractAtm;
import com.mitec.business.model.CustomerEmail;
import com.mitec.business.model.Department;
import com.mitec.business.model.Device;
import com.mitec.business.model.Error;
import com.mitec.business.model.ErrorDevice;
import com.mitec.business.model.Job;
import com.mitec.business.model.JobPerform;
import com.mitec.business.model.Kpsc;
import com.mitec.business.model.Region;
import com.mitec.business.model.ReplacementAccessory;
import com.mitec.business.model.Role;
import com.mitec.business.model.Series;
import com.mitec.business.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClassMapper {
	
	private String convertTime(LocalDateTime localDateTime) {
		DateTimeFormatter fommater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return fommater.format(localDateTime);
	}
	
	public UserDto convertToUserDto(User user) {
		log.debug("Processing convertToAppUserDto()....");
		
		UserDto userDto = new UserDto();
		
		//copy Properties
		BeanUtils.copyProperties(user, userDto);
		return userDto;
	}
	
	public RoleDto convertToRoleDto(Role role) {
		log.debug("Processing convertToRoleDto()....");
		
		RoleDto roleDto = new RoleDto();
		//copy Properties
		BeanUtils.copyProperties(role, roleDto);
		return roleDto;
	}
	
	public SeriesDto convertToSeriesDto(Series series) {
		log.debug("Processing convertToSeriesDto()....");
		SeriesDto seriesDto = new SeriesDto();

		//copy Properties
		BeanUtils.copyProperties(series, seriesDto);
		return seriesDto;
	}
	
	public DepartmentDto convertToDepartmentDto(Department department) {
		log.debug("Processing convertToDepartmentDto()....");
		DepartmentDto departmentDto = new DepartmentDto();
	
		//copy Properties
		BeanUtils.copyProperties(department, departmentDto);
		if (department.getRegion() != null) {
			departmentDto.setRegionId(department.getRegion().getId());
		}else {
			departmentDto.setRegionId(null);
		}
		return departmentDto;
	}
	
	public RegionDto convertToRegionDto(Region region) {
		log.debug("Processing convertToDepartmentDto()....");
		RegionDto regionDto = new RegionDto();
	
		//copy Properties
		BeanUtils.copyProperties(region, regionDto);
		return regionDto;
	}
	
	//Convert ATMDto
	public ATMDto convertToATMDto(ATM atm) {
		log.debug("Processing convertToATMDto()....");
		ATMDto atmDto = new ATMDto();
		RegionDto region = atm.getRegion() != null ? convertToRegionDto(atm.getRegion()) : null;
		SeriesDto series = atm.getSeries() != null ? convertToSeriesDto(atm.getSeries()) :null;
		DepartmentDto department = atm.getDepartment() != null ? convertToDepartmentDto(atm.getDepartment()) :null;
		
		//copy Properties
		BeanUtils.copyProperties(atm, atmDto);
		atmDto.setSeries(series);
		atmDto.setRegion(region);
		atmDto.setDepartment(department);
		if (atm.getStatus() != null) {
			atmDto.setStatusDesc(AtmStatusEnum.fromKey(atm.getStatus()).getValue());
		}
		return atmDto;
	}
	
	//Convert jobDto all field
	public JobDto convertToJobDto(Job job) {
		log.debug("Processing convertToJobDto()....");
		JobDto jobDto = new JobDto();
		ATMDto atm = job.getAtm() != null ? convertToATMDto(job.getAtm()) : null;
		UserDto user = job.getUser() != null ? convertToUserDto(job.getUser()) : null ;
		List<KpscDto> kpscDtos = new ArrayList<>();
		if(job.getKpscs() != null) {
			kpscDtos = job.getKpscs().stream().map(kpsc -> convertToKpscDto(kpsc)).collect(Collectors.toList());
		}
		
		//copy Properties
		BeanUtils.copyProperties(job, jobDto);
		jobDto.setAtm(atm);
		jobDto.setUser(user);
		jobDto.setKpscs(kpscDtos);
		if (job.getCheckInTime() != null) {
			jobDto.setCheckInTime(convertTime(job.getCheckInTime()));
		}
		if (job.getCheckOutTime() != null) {
			jobDto.setCheckOutTime(convertTime(job.getCheckOutTime()));
		}
		if (job.getCompleteTime() != null) {
			jobDto.setCompleteTime(convertTime(job.getCompleteTime()));
		}
		jobDto.setNote(job.getNote() != null ? job.getNote() : "");
		return jobDto;
	}
	
	//Convert jobDto 
	public JobDto convertToJobDtoWithUser(Job job) {
		log.debug("Processing convertToJobDto()....");
		JobDto jobDto = new JobDto();
		ATMDto atm = job.getAtm() != null ? convertToATMDto(job.getAtm()) : null;
		UserDto user = job.getUser() != null ? convertToUserDto(job.getUser()) : null ;
		
		//copy Properties
		BeanUtils.copyProperties(job, jobDto);
		jobDto.setAtm(atm);
		jobDto.setUser(user);
		if (job.getCheckInTime() != null) {
			jobDto.setCheckInTime(convertTime(job.getCheckInTime()));
		}
		if (job.getCheckOutTime() != null) {
			jobDto.setCheckOutTime(convertTime(job.getCheckOutTime()));
		}
		if (job.getCompleteTime() != null) {
			jobDto.setCompleteTime(convertTime(job.getCompleteTime()));
		}
		jobDto.setNote(job.getNote() != null ? job.getNote() : "");
		return jobDto;
	}
	
	//Convert jobDto summary
	public JobDto convertToJobDtoWithoutUser(Job job) {
		log.debug("Processing summary convertToJobDto()....");
		JobDto jobDto = new JobDto();
		ATMDto atm = job.getAtm() != null ? convertToATMDto(job.getAtm()) : null;
		
		//copy Properties
		BeanUtils.copyProperties(job, jobDto);
		jobDto.setAtm(atm);
		if (job.getCheckInTime() != null) {
			jobDto.setCheckInTime(convertTime(job.getCheckInTime()));
		}
		if (job.getCheckOutTime() != null) {
			jobDto.setCheckOutTime(convertTime(job.getCheckOutTime()));
		}
		if (job.getCompleteTime() != null) {
			jobDto.setCompleteTime(convertTime(job.getCompleteTime()));
		}
		jobDto.setNote(job.getNote() != null ? job.getNote() : "");
		return jobDto;
	}
	
	public DeviceDto convertToDeviceDto(Device device) {
		log.debug("Processing convertToDeviceDto()....");
		DeviceDto deviceDto = new DeviceDto();
		
		//copy Properties
		BeanUtils.copyProperties(device, deviceDto);
		return deviceDto;
	}
	
	public DeviceDto convertToDeivceDtoWithError(Device device) {
		log.debug("Processing convertToDeivceDtoWithError()....");
		DeviceDto deviceDto = new DeviceDto();
		List<ErrorDto> errors = new ArrayList<>();
		
		BeanUtils.copyProperties(device, deviceDto);
		if (device.getErrors() != null) {
			errors = device.getErrors().stream().map(err -> convertToErrorDtoWithoutList(err)).collect(Collectors.toList());
		}
		deviceDto.setErrors(errors);
		return deviceDto;
	}
	
	//Convert ErrorDevice
	public ErrorDeviceDto convertToErrorDeviceDto(ErrorDevice errorDevice) {
		log.debug("Processing convertToErrorDeviceDto()....");
		ErrorDeviceDto errorDeviceDto = new ErrorDeviceDto();
		
		BeanUtils.copyProperties(errorDevice, errorDeviceDto);
		List<Accessory> accessories = errorDevice.getAccessories();
		List<AccessoryDto> accessoryDtos = new ArrayList<>();
		if (accessories != null && !accessories.isEmpty()) {
			accessoryDtos = accessories.stream().map(item -> convertToAccessoryDtoWithoutList(item)).collect(Collectors.toList());
		}
		errorDeviceDto.setAccessories(accessoryDtos);
		return errorDeviceDto;
	}
	
	public ErrorDeviceDto convertToErrorDeviceDtoWithSeries(ErrorDevice errorDevice) {
		log.debug("Processing convertToErrorDeviceDto()....");
		ErrorDeviceDto errorDeviceDto = new ErrorDeviceDto();
		List<SeriesDto> seriesDtos = new ArrayList<>();
		List<Accessory> accessories = errorDevice.getAccessories();
		
		BeanUtils.copyProperties(errorDevice, errorDeviceDto);
		Set<Series> series = new HashSet<>();
		accessories.stream().forEach(item -> {
			series.addAll(item.getSeries());
		});
		
		if (!series.isEmpty()) {
			seriesDtos = series.stream().map(item -> convertToSeriesDto(item)).collect(Collectors.toList());
		}
		
		errorDeviceDto.setSeries(seriesDtos);
		return errorDeviceDto;
	}
	
	public ErrorDeviceDto convertToErrorDeviceDtoWithoutList(ErrorDevice errorDevice) {
		log.debug("Processing convertToErrorDeviceDto()....");
		ErrorDeviceDto errorDeviceDto = new ErrorDeviceDto();
		
		BeanUtils.copyProperties(errorDevice, errorDeviceDto);
		return errorDeviceDto;
	}
	
	//Convert jobPerform
	public JobPerformDto convertToJobPerformDto(JobPerform jobPerform) {
		log.debug("Processing convertToDeivceDto()....");
		JobPerformDto jobPerformDto = new JobPerformDto();
		
		BeanUtils.copyProperties(jobPerform, jobPerformDto);
		return jobPerformDto;
	}
	
	//TO-DO
	public AccessoryDto convertToAccessoryDto(Accessory accessory) {
		log.debug("Processing convertToAccessoryDto()....");
		AccessoryDto accessoryDto = new AccessoryDto();

		BeanUtils.copyProperties(accessory, accessoryDto);
		ErrorDevice errorDevice = accessory.getErrorDevice();
		if (errorDevice != null) { 
			accessoryDto.setErrorDevice(convertToErrorDeviceDtoWithoutList(errorDevice)); 
		}
		
		List<Series> series = accessory.getSeries();
		List<SeriesDto> seriesDtos = new ArrayList<>();
		if (series != null && !series.isEmpty()) {
			seriesDtos = series.stream().map(item -> convertToSeriesDto(item)).collect(Collectors.toList());
		}
		accessoryDto.setSeries(seriesDtos);
		
		return accessoryDto;
	}
	
	public AccessoryDto convertToAccessoryDtoWithSeries(Accessory accessory) {
		log.debug("Processing convertToAccessoryDto()....");
		AccessoryDto accessoryDto = new AccessoryDto();

		BeanUtils.copyProperties(accessory, accessoryDto);
		List<Series> series = accessory.getSeries();
		List<SeriesDto> seriesDto = new ArrayList<>();
		
		accessoryDto.setErrorDeviceId(accessory.getErrorDevice().getId());
		if (series != null && !series.isEmpty()) {
			seriesDto = series.stream().map(item -> convertToSeriesDto(item)).collect(Collectors.toList());
		}
		accessoryDto.setSeries(seriesDto);;
		return accessoryDto;
	}
	
	public AccessoryDto convertToAccessoryDtoWithoutList(Accessory accessory) {
		log.debug("Processing convertToAccessoryDto()....");
		AccessoryDto accessoryDto = new AccessoryDto();

		accessoryDto.setErrorDeviceId(accessory.getErrorDevice().getId());
		BeanUtils.copyProperties(accessory, accessoryDto);
		return accessoryDto;
	}
	
	//Convert ReplacementAccessoryDto
	public ReplacementAccessoryDto convertToReplacementAccessoryDto(ReplacementAccessory replacementAccessory) {
		log.debug("Processing convertToReplacementAccessoryDto()....");
		ReplacementAccessoryDto replacementAccessoryDto = new ReplacementAccessoryDto();
		
		BeanUtils.copyProperties(replacementAccessory, replacementAccessoryDto);
		if(replacementAccessory.getErrorDevice() != null) {
			replacementAccessoryDto.setErrorDevice(convertToErrorDeviceDtoWithoutList(replacementAccessory.getErrorDevice()));
		}
		if(replacementAccessory.getAccessory() != null) {
			replacementAccessoryDto.setAccessory(convertToAccessoryDtoWithSeries(replacementAccessory.getAccessory()));
		}
		return replacementAccessoryDto;
	}
	
	//Convert kpsc
	public KpscDto convertToKpscDto(Kpsc kpsc) {
		log.debug("Processing convertToKpscDto()....");
		KpscDto kpscDto = new KpscDto();
		List<ReplacementAccessoryDto> replacementAccessoryDtos = new ArrayList<>();
		if(kpsc.getReplacementAccessories() != null) {
			replacementAccessoryDtos = kpsc.getReplacementAccessories().stream().map(ra -> {
				return convertToReplacementAccessoryDto(ra);
			}).collect(Collectors.toList());
		}
		kpscDto.setReplacementAccessories(replacementAccessoryDtos);
		
		BeanUtils.copyProperties(kpsc, kpscDto);
		if(kpsc.getDevice() != null) {
			kpscDto.setDevice(convertToDeviceDto(kpsc.getDevice()));
		}
		if(kpsc.getJobPerform() != null) {
			kpscDto.setJobPerform(convertToJobPerformDto(kpsc.getJobPerform()));
			kpscDto.setJobPerformId(kpsc.getJobPerform().getId());
		}
		return kpscDto;
	}
	
	//Convert error
	public ErrorDto convertToErrorDto(Error error) {
		log.debug("Processing convertToErrorDto()....");
		ErrorDto errorDto = new ErrorDto();
		Device device = error.getDevice();
		BeanUtils.copyProperties(error, errorDto);
		errorDto.setDeviceId(error.getDevice().getId());
		if (device != null) {
			errorDto.setDevice(convertToDeviceDto(device));
		}
		return errorDto;
	}
	
	public ErrorDto convertToErrorDtoWithoutList(Error error) {
		log.debug("Processing convertToErrorDtoWithoutList()....");
		ErrorDto errorDto = new ErrorDto();
		errorDto.setDeviceId(error.getDevice().getId());
		BeanUtils.copyProperties(error, errorDto);
		return errorDto;
	}
	
	//Convert bank
	public BankDto convertToBankDto(Bank bank) {
		log.debug("Processing convertToBankDto()....");
		BankDto bankDto = new BankDto();
		
		BeanUtils.copyProperties(bank, bankDto);
		return bankDto;
	}
	
	//Convert contract
	public ContractDto convertToContractDto(Contract contract) {
		log.debug("Processing convertToContractDto()....");
		ContractDto contractDto = new ContractDto();
		List<ATM> atms = contract.getAtms();
		List<CustomerEmail> customerEmails = contract.getCustomerEmails();
		List<ATMDto> atmDtos = new ArrayList<>();
		
		if (atms != null && !atms.isEmpty()) {
			atmDtos = atms.stream().map(atm -> convertToATMDto(atm)).collect(Collectors.toList());
		}
		List<CustomerEmailDto> customerEmailDtos = new ArrayList<>();
		if (customerEmails != null && !customerEmails.isEmpty()) {
			customerEmailDtos = customerEmails.stream().map(email -> convertToCustomerEmailDto(email)).collect(Collectors.toList());
		}
		Bank bank = contract.getBank();
		BeanUtils.copyProperties(contract, contractDto);
		contractDto.setAtms(atmDtos);
		contractDto.setCustomerEmails(customerEmailDtos);
		contractDto.setBankName(bank.getName());
		contractDto.setBank(bank);
		
		// convert LocalDateTime for input
		contractDto.setStartTimeStr(CustomFormatDate.formatLocalDate(contract.getStartTime(), "dd/MM/yyyy"));
		contractDto.setEndTimeStr(CustomFormatDate.formatLocalDate(contract.getEndTime(), "dd/MM/yyyy"));
		return contractDto;
	}
	
	/* Chỉ dùng để sửa orderNumber của ATM trong hợp đồng */
	public ContractDto convertContractDtoForEditATMs(Contract contract) {
		log.debug("Processing convertContractDtoForEditATMs()....");
		ContractDto contractDto = new ContractDto();
		contractDto.setId(contract.getId());
		List<ContractAtmDto> contractAtmDtos = new ArrayList<>();
		contractAtmDtos = contract.getContractAtms().stream().map(item -> {
			return convertContractAtmDto(item);
		}).collect(Collectors.toList());;
		
		contractAtmDtos.sort((ca1, ca2) -> {
			if (ca1.getOrderNumber() == null || ca2.getOrderNumber() == null) {
				return 0;
			}
			return ca1.getOrderNumber().compareTo(ca2.getOrderNumber());
		});
		
		contractDto.setContractAtmDtos(contractAtmDtos);
		return contractDto;
	}
	
	public ContractAtmDto convertContractAtmDto(ContractAtm contractAtm) {
		ContractAtmDto contractAtmDto = new ContractAtmDto();
		ATMDto atmDto = new ATMDto();
		atmDto.setSerialNumber(contractAtm.getAtm().getSerialNumber());
		atmDto.setAddress(contractAtm.getAtm().getAddress());
		
		BeanUtils.copyProperties(contractAtm, contractAtmDto);
		contractAtmDto.setAtmDto(atmDto);
		contractAtmDto.setContractId(contractAtm.getContract().getId());
		return contractAtmDto;
	}
	/* Chỉ dùng để sửa orderNumber của ATM trong hợp đồng */
	
	//Convert customer email
	public CustomerEmailDto convertToCustomerEmailDto(CustomerEmail customerEmail) {
		log.debug("Processing convertToCustomerEmailDto()....");
		CustomerEmailDto contractDto = new CustomerEmailDto();
		
		
		BeanUtils.copyProperties(customerEmail, contractDto);
		return contractDto;
	}
}
