package com.mitec.business.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mitec.business.dto.JobExportDto;
import com.mitec.business.dto.JobTimeDto;
import com.mitec.business.dto.UserDto;
import com.mitec.business.event.DeleteJobEvent;
import com.mitec.business.model.Job;
import com.mitec.business.model.Kpsc;
import com.mitec.business.model.ReplacementAccessory;
import com.mitec.business.repository.JobRepository;
import com.mitec.business.repository.ReplacementAccessoryRepository;
import com.mitec.business.repository.UserRepository;
import com.mitec.business.specification.ObjectSpecification;
import com.mitec.business.utils.CustomFormatDate;
import com.mitec.business.utils.JobStatusEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JobService {

	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private ReplacementAccessoryRepository replacementAccessoryRepository;
	@Autowired
	private ObjectSpecification objectSpecification;
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	@Autowired
	private UserRepository userRepository;
	
	public Page<Job> gets(String username, String startTimeStr, String endTimeStr, int pageNumber, int size, Integer status, Long serviceType) {
		log.debug("Processing get job page service()...");
		LocalDateTime startTime = null;
		LocalDateTime endTime = null;
		startTime = CustomFormatDate.formatStartTimeForInput(startTimeStr);
		endTime = CustomFormatDate.formartEndTimeForInput(endTimeStr);
		
		Specification<Job> spec = objectSpecification.searchJob(username, startTime, endTime, status, serviceType);
		Page<Job> page = jobRepository.findAll(spec, PageRequest.of(pageNumber, size));
		
		log.debug("page total element == " + page.getTotalElements());
		
		List<Job> list = page.getContent().stream().map(item -> {
			item.setStatusDesc(JobStatusEnum.fromKey(item.getStatus()).getValue());
			return item;
		}).collect(Collectors.toList());
		
		return new PageImpl<>(list, PageRequest.of(pageNumber, size), page.getTotalElements());
	}
	
	public Page<Job> lookup(String serial, String address, String startTimeStr, String endTimeStr, int pageNumber, 
			int size, Integer status, Integer idAccessory, Long serviceType) {
		log.debug("Processing lookup service()...");
		LocalDateTime startTime = CustomFormatDate.formatStartTimeForInput(startTimeStr);
		LocalDateTime endTime = CustomFormatDate.formartEndTimeForInput(endTimeStr);
		
		Specification<Job> spec = objectSpecification.lookup(serial, address, startTime, endTime, status, idAccessory, serviceType);
		Page<Job> page = jobRepository.findAll(spec, PageRequest.of(pageNumber, size));
		
		List<Job> list = page.getContent().stream().map(item -> {
			item.setStatusDesc(JobStatusEnum.fromKey(item.getStatus()).getValue());
			return item;
		}).collect(Collectors.toList());
		
		return new PageImpl<>(list, PageRequest.of(pageNumber, size), page.getTotalElements());
	}
	
	public Map<String, Object> jobDetail(Long id) {
		Map<String, Object> data = new HashMap<>();
		Job job = jobRepository.getById(id);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd-MM-yyyy");
		
		data.put("username", job.getUser().getFullName());
		data.put("phoneNumber", job.getUser().getPhoneNumber());
		data.put("serialNumber", job.getAtm().getSerialNumber().toUpperCase());
		data.put("address", job.getAtm().getAddress());
		data.put("checkInTime", formatter.format(job.getCheckInTime()));
		if (job.getCheckOutTime() != null) {
			data.put("checkOutTime", formatter.format(job.getCheckOutTime()));
		}else {
			data.put("checkOutTime", null);
		}


		if (job.getCompleteTime() != null) {
			data.put("completeTime",formatter.format(job.getCompleteTime()));

		}else {
			data.put("completeTime", null);
		}




		
		List<Map<String, Object>> kpscs = new ArrayList<>();
		List<Map<String, Object>> accessories = new ArrayList<>();
		
		job.getKpscs().stream().forEach(item -> {
			if (item.getDevice() != null) {
				Map<String, Object> kpsc = new HashMap<>();
				kpsc.put("device", item.getDevice().getName());
				kpsc.put("errorDesc", item.getErrorDesc());
				kpsc.put("jobPerform", item.getJobPerform().getName());
				
				kpscs.add(kpsc);
			}
		});
		
		List<Object[]> list = replacementAccessoryRepository.statisticalByJobId(id);
		if (list != null && !list.isEmpty()) {
			accessories = list.stream().map(item -> {
				Map<String, Object> accessory = new HashMap<>();
				accessory.put("device", item[0]);
				accessory.put("desc", "Thay " + item[2] + " " + item[1]);
				
				return accessory;
			}).collect(Collectors.toList());
		}
		
		data.put("kpscs", kpscs);
		data.put("accessories", accessories);
		
		data.put("workDetail", workDetail(job.isMaintenance(), job.isKpsc()));
		data.put("note", job.getNote());
		
		return data;
	}
	
	private String workDetail(boolean isMaintenance, boolean isKpsc) {
    	StringBuilder sb = new StringBuilder();
    	if (isMaintenance) {
    		sb.append("Bảo trì định kỳ ");
    	}
    	if (isMaintenance && isKpsc) {
    		sb.append("và ");
    	}
    	if (isKpsc) {
    		sb.append("Khắc phục sự cố");
    	}
    	return sb.toString();
    }
	
	public boolean deleteJob(Long id) {
		log.debug("Processing delete job by id()....");
		try {
			jobRepository.deleteById(id);
			
			// xóa bản ghi ở bảng statiscal
			applicationEventPublisher.publishEvent(new DeleteJobEvent(this, id, null));
			return true;
		}catch(Exception e) {
			log.debug("Err: " + e.getMessage());
			return false;
		}
	}
	
	public boolean deleteAllJob(String username, String startTimeStr, String endTimeStr, Integer status, Long serviceType) {
		log.debug("Processing delete all job()....");
		boolean isDelete = false;
		try {
			LocalDateTime startTime = null;
			LocalDateTime endTime = null;
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			if(StringUtils.isNotBlank(startTimeStr)) {
				startTime = LocalDate.parse(startTimeStr, formatter).atTime(LocalTime.MIN);
			}
			if (StringUtils.isNotBlank(endTimeStr)) {
				endTime = LocalDate.parse(endTimeStr, formatter).atTime(LocalTime.MAX);
			}
			Specification<Job> spec = objectSpecification.searchJob(username, startTime, endTime, status, serviceType);
			List<Job> jobs = jobRepository.findAll(spec);
			
			if (jobs != null && !jobs.isEmpty()) {
				jobs.stream().forEach(item -> jobRepository.delete(item));
				isDelete = true;
				// xóa bản ghi ở bảng statiscal
				applicationEventPublisher.publishEvent(new DeleteJobEvent(this, null, jobs));
			}
		}catch (Exception e) {
			log.debug("Err: " + e.getMessage());
		}
		return isDelete;
	}
	
	public boolean deleteAllJob(String serialNumber, String address, String startTimeStr, String endTimeStr, Integer status, Integer idAccessory, Long serviceType) {
		log.debug("Processing delete all job()....");
		try {
			LocalDateTime startTime = null;
			LocalDateTime endTime = null;
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			if(StringUtils.isNotBlank(startTimeStr)) {
				startTime = LocalDate.parse(startTimeStr, formatter).atTime(LocalTime.MIN);
			}
			if (StringUtils.isBlank(endTimeStr)) {
				endTime = LocalDateTime.now();
			}else {
				endTime = LocalDate.parse(endTimeStr, formatter).atTime(LocalTime.MAX);
			}
			Specification<Job> spec = objectSpecification.lookup(serialNumber, address, startTime, endTime, status, idAccessory, serviceType);
			List<Job> jobs = jobRepository.findAll(spec);

			if (jobs != null && !jobs.isEmpty()) {
				jobs.stream().forEach(item -> jobRepository.delete(item));
				// xóa bản ghi ở bảng statiscal
				applicationEventPublisher.publishEvent(new DeleteJobEvent(this, null, jobs));
			}
			
			return true;
		}catch (Exception e) {
			log.debug("Err: " + e.getMessage());
			return false;
		}
	}
	
	public void takeReport(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = ob.getLong("id");
		Job job = jobRepository.getById(id);
		job.setTakeReport(true);
		jobRepository.save(job);
 	}
	
	public byte[] exportExcel(String body) throws JSONException, IOException {
		JSONObject ob = new JSONObject(body);
		String serial = ob.getString("serial");
		String address = ob.getString("address");
		String statusStr = ob.getString("status");
		String startTimeStr = ob.getString("startTime");
		String endTimeStr = ob.getString("endTime");
		String accessoryStr = ob.getString("accessory");
		String serviceType = ob.getString("serviceType");
		
		Specification<Job> spec = objectSpecification.lookup(serial, address, CustomFormatDate.formatStartTimeForInput(startTimeStr), 
				CustomFormatDate.formartEndTimeForInput(endTimeStr), StringUtils.isBlank(statusStr) ? null : Integer.valueOf(statusStr), 
				StringUtils.isBlank(accessoryStr) ? null : Integer.valueOf(accessoryStr), StringUtils.isBlank(serviceType) ? null : Long.valueOf(serviceType));
	
		List<Job> jobs = jobRepository.findAll(spec);
		
		if (jobs != null && !jobs.isEmpty()) {
			List<JobExportDto> data = tranferData(jobs);	
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			InputStream inputStream = new ClassPathResource("static/reports/template-tra-cuu.xlsx").getInputStream();
			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0);
			
			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle.setAlignment(HorizontalAlignment.LEFT);
			cellStyle.setWrapText(true);
			
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			
			int firstRow = 4;
			
			for (int i = 0; i < data.size(); i++) {
				Row row = sheet.createRow(firstRow + i);
				Cell cell = row.createCell(0);
				cell.setCellValue(i + 1);
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(1);
				cell.setCellValue(data.get(i).getCheckIn());
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(2);
				cell.setCellValue(data.get(i).getCheckOut());
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(3);
				cell.setCellValue(data.get(i).getTime());
				cell.setCellStyle(cellStyle);

				cell = row.createCell(4);
				cell.setCellValue(data.get(i).getUsername());
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(5);
				cell.setCellValue(data.get(i).getSerial());
				cell.setCellStyle(cellStyle);

				cell = row.createCell(6);
				cell.setCellValue(data.get(i).getAddress());
				cell.setCellStyle(cellStyle);

				cell = row.createCell(7);
				cell.setCellValue(data.get(i).getNote());
				cell.setCellStyle(cellStyle);

				cell = row.createCell(8);
				cell.setCellValue(data.get(i).getMaintenance());
				cell.setCellStyle(cellStyle);

				cell = row.createCell(9);
				cell.setCellValue(data.get(i).getStatus());
				cell.setCellStyle(cellStyle);

				cell = row.createCell(10);
				cell.setCellValue(data.get(i).getAccessory());
				cell.setCellStyle(cellStyle);

				cell = row.createCell(11);
				cell.setCellValue(data.get(i).getTotal());
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(12);
				cell.setCellValue(data.get(i).getKpsc() == null ? "" : data.get(i).getKpsc());
				cell.setCellStyle(cellStyle);

				cell = row.createCell(13);
				cell.setCellValue(data.get(i).getJobPerform() == null ? "" : data.get(i).getJobPerform());
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(14);
				cell.setCellValue(data.get(i).getServiceType());
				cell.setCellStyle(cellStyle);
			}
			
			workbook.write(bos);
			workbook.close();
			
			return bos.toByteArray();
		}
		return new byte[0];
 	}
	
	private List<JobExportDto> tranferData(List<Job> jobs) {
		List<UserDto> users = userRepository.getAll();
		
		return jobs.stream().map(job -> {
			JobExportDto item = new JobExportDto();
			item.setCheckIn(CustomFormatDate.getTimeInLocalDateTime(job.getCheckInTime()));
			item.setCheckOut(CustomFormatDate.getTimeInLocalDateTime(job.getCheckOutTime()));
			
			item.setTime(CustomFormatDate.formatLocalDate(job.getCheckInTime(), "dd/MM/yyyy"));
			item.setSerial(job.getAtm().getSerialNumber());
			item.setAddress(job.getAtm().getAddress());
			item.setNote(job.getNote());
			item.setMaintenance(job.isMaintenance() ? "Bảo trì" : "");
			item.setStatus(JobStatusEnum.fromKey(job.getStatus()).getValue());
			
			for (UserDto u : users) {
				if (job.getUser().getId().equals(u.getId())) {
					item.setUsername(u.getFullName());
					break;
				}
			}
			
			StringBuilder sb = new StringBuilder();
			StringBuilder kpscStr = new StringBuilder();
			StringBuilder jobPerformStr = new StringBuilder();
			Set<Long> setJP = new HashSet<>();
			
			boolean isSupportOnline = false;
			String serviceType = "";
			
			Long total = 0L;
			if (job.isKpsc() && !job.getKpscs().isEmpty()) {
				for (Kpsc kpsc : job.getKpscs()) {
					if (!kpsc.getReplacementAccessories().isEmpty()) {
						for (ReplacementAccessory ra : kpsc.getReplacementAccessories()) {
							sb.append(ra.getQuantity());
							sb.append(" - ");
							sb.append(ra.getAccessory().getName());
							sb.append("\n");
							total += ra.getQuantity();
						}
					}

					if (kpsc.getErrorDesc() != null) {
						kpscStr.append(kpsc.getErrorDesc());
						kpscStr.append(",");
					}
					
					if (kpsc.getJobPerform() != null) {
						if (kpsc.getJobPerform().getId().equals(5L)) {
							isSupportOnline = true;
							serviceType = kpsc.getJobPerform().getName();
						}
						
						if (setJP == null || !setJP.contains(kpsc.getJobPerform().getId())) {
							jobPerformStr.append(kpsc.getJobPerform().getName());
							jobPerformStr.append(",");
						}

						setJP.add(kpsc.getJobPerform().getId());
					}
				}
			}
			if (sb.length() > 0) {
				sb.replace(sb.lastIndexOf("\n"), sb.length() - 1, "");
			}
			item.setAccessory(sb.toString());
			item.setTotal(total);
			
			if (kpscStr.length() > 0) {
				item.setKpsc(kpscStr.substring(0, kpscStr.length()));
			}
			
			if (kpscStr.length() > 0) {
				item.setJobPerform(jobPerformStr.substring(0, jobPerformStr.length()));
			}

			if (isSupportOnline) {
				item.setServiceType(serviceType);
			}
			
			return item;
		}).collect(Collectors.toList());
	}
	
	public JobTimeDto getJobTime(Long jobId) {
		if (jobId == null) return null;
		
		return jobRepository.getJobTime(jobId);
	}
	
	@Transactional
	public String editJobTime(Long jobId, String checkInStr, String checkOutStr) {
		Job job = jobRepository.findById(jobId).orElse(null);
		
		String pattern = "dd-MM-yyyy HH:mm:ss";
		
		if (job == null) return null;
		if (StringUtils.isBlank(checkInStr) || StringUtils.isBlank(checkOutStr))return null;
 		
		job.setCheckInTime(CustomFormatDate.parseInputDateTimeLocal(checkInStr));
		job.setCheckOutTime(CustomFormatDate.parseInputDateTimeLocal(checkOutStr));
		
		try {
			jobRepository.save(job);
		}catch (Exception e) {
			log.debug("Err when edit checkIn, checkOut: " + e.getMessage());
			return null;
		}
		
		return "Success";
	}
}
