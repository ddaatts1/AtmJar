package com.mitec.business.service.api;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.mitec.business.dto.DeviceDto;
import com.mitec.business.dto.ErrorDeviceDto;
import com.mitec.business.dto.JobDto;
import com.mitec.business.dto.ResultApi;
import com.mitec.business.event.CheckOutEvent;
import com.mitec.business.event.CloneStatisticalEvent;
import com.mitec.business.event.UpdateInventoryCheckOutEvent;
import com.mitec.business.model.ATM;
import com.mitec.business.model.Accessory;
import com.mitec.business.model.Device;
import com.mitec.business.model.Error;
import com.mitec.business.model.ErrorDevice;
import com.mitec.business.model.Job;
import com.mitec.business.model.JobPerform;
import com.mitec.business.model.Kpsc;
import com.mitec.business.model.ReplacementAccessory;
import com.mitec.business.model.User;
import com.mitec.business.repository.ATMRepository;
import com.mitec.business.repository.AccessoryRepository;
import com.mitec.business.repository.DeviceRepository;
import com.mitec.business.repository.ErrorDeviceRepository;
import com.mitec.business.repository.ErrorRepository;
import com.mitec.business.repository.JobPerformRepository;
import com.mitec.business.repository.JobRepository;
import com.mitec.business.repository.KpscRepository;
import com.mitec.business.repository.ReplacementAccessoryRepository;
import com.mitec.business.repository.StatisticalRepository;
import com.mitec.business.repository.UserRepository;
import com.mitec.business.specification.ObjectSpecification;
import com.mitec.business.utils.AtmStatusEnum;
import com.mitec.business.utils.ClassMapper;
import com.mitec.business.utils.JobStatusEnum;
import com.mitec.business.utils.SpringUploadUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Component
public class ApiService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private ATMRepository atmRepository;
	@Autowired
	private ClassMapper classMapper;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private KpscRepository kpscRepository;
	@Autowired
	private AccessoryRepository accessoryRepository;
	@Autowired
	private ReplacementAccessoryRepository replacementAccessoryRepository;
	@Autowired
	private JobPerformRepository jobPerformRepository;
	@Autowired
	private StatisticalRepository statisticalRepository;
	@Autowired
	private ErrorRepository errorRepository;
	@Autowired
	private ObjectSpecification objectSpecification;
	@Autowired
	private ErrorDeviceRepository errorDeviceRepository;
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Async
	public CompletableFuture<ResultApi> checkIn(String input) throws JSONException {
		log.debug("Processing Checkin service()....");
		ResultApi result = new ResultApi();
		JSONObject object = new JSONObject(input);
		String username = object.getString("username");
		String serialNumber = object.getString("serialNumber");
		
		// nếu ATM không có task nào đang mở
		Map<String, Object> check = checkTaskInprocess(serialNumber);
		if ((boolean) check.get("check")) {
		
			ATM atm = atmRepository.findById(serialNumber).orElse(null);
			if(atm == null) {
				result.setSuccess(false);
				result.setMessage("Không tìm thấy thông tin cây ATM tương ứng");
				return CompletableFuture.completedFuture(result);
			}
			
			Job job = new Job();
			job.setAtm(atm);
			job.setUser(userRepository.findByUsername(username));
			job = jobRepository.save(job);
			JobDto jobDto = classMapper.convertToJobDtoWithoutUser(job);
			
			//Publisher event clone
			applicationEventPublisher.publishEvent(new CloneStatisticalEvent(this, job));
			
			Map<String, Object> data = new HashMap<>();
			data.put("job", jobDto);
			result.setData(data);
			result.setSuccess(true);
			result.setMessage("Check in thành công");
			log.debug("Check in thành công");
			
			return CompletableFuture.completedFuture(result);
		}
		
		result.setSuccess(false);
		result.setMessage("User " + (String) check.get("username") + " đang mở task công việc trên ATM này, vui lòng kiểm tra lại!");
		return CompletableFuture.completedFuture(result);
	}
	
	private Map<String, Object> checkTaskInprocess(String serialNumber) {
		Map<String, Object> result = new HashMap<>();
		List<Job> jobs = jobRepository.getJobProcessByATM(serialNumber, JobStatusEnum.IN_PROCESS.getKey());
		if (jobs == null || jobs.isEmpty()) {
			result.put("check", true);
		}else {
			result.put("check", false);
			result.put("username", jobs.get(0).getUser().getUsername());
		}
		return result;
	}
	
	public ResultApi personalHistory(String input) throws JSONException {
		log.debug("Processing personalHistory service()....");
		JSONObject object = new JSONObject(input);
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		List<Job> jobs = jobRepository.getByUsernameLimited5(object.getString("username"), JobStatusEnum.IN_PROCESS.getKey());
		List<JobDto> jobDtos = jobs.stream().map(job -> classMapper.convertToJobDtoWithoutUser(job)).collect(Collectors.toList());
		
		data.put("jobs", jobDtos);
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi atmHistory(String input) throws JSONException {
		log.debug("Processing atmHistory service()....");
		JSONObject object = new JSONObject(input);
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		List<Job> jobs = jobRepository.getBySerialNumberLimited5(object.getString("serialNumber"), JobStatusEnum.IN_PROCESS.getKey());
		List<JobDto> jobDtos = jobs.stream().map(job -> classMapper.convertToJobDtoWithUser(job)).collect(Collectors.toList());
		
		data.put("jobs", jobDtos);
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi cancelJob(String input, String path) throws JSONException, IOException {
		log.debug("Processing cancelJob service()....");
		JSONObject object = new JSONObject(input);
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		Job job = jobRepository.getById(object.getLong("jobId"));
		job.setStatus(JobStatusEnum.CANCEL.getKey());
		job.setCompleteTime(LocalDateTime.now());
		
		if (StringUtils.isNotBlank(job.getFilePath())) {
			SpringUploadUtil.deleteFile(path, job.getFilePath().substring(1));
			job.setFilePath(null);
		}
		
		job = jobRepository.save(job);
		
		statisticalRepository.deleteByJobId(job.getId());
		
		data.put("jobs", classMapper.convertToJobDtoWithoutUser(job));
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi; 
	}
	
	public ResultApi jobDetail(Long jobId) {
		log.debug("Processing jobDetail service()....");
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		Job job = jobRepository.getById(jobId);
		
		data.put("job", classMapper.convertToJobDto(job));
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi checkOut(String input, String username) throws JSONException {
		log.debug("Processing checkOut service()....");
		log.debug("input =======> " + input);
		JSONObject object = new JSONObject(input);
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		JSONArray listJsonAcc = new JSONArray();
		
		Job job = jobRepository.getById(object.getLong("id"));
//		job.setStatus(JobStatusEnum.COMPLETED.getKey());
		job.setStatus(object.getInt("status"));
		job.setCompleteTime(LocalDateTime.parse(object.getString("completeTime"), formatter));
		job.setJobReason(object.getString("jobReason"));
		job.setMaintenance(object.getBoolean("isMaintenance"));
		job.setKpsc(object.getBoolean("isKpsc"));
		job.setNote(object.getString("note"));
		job.setJobError(object.getString("jobError"));
		job.setCheckOutTime(LocalDateTime.now());
		job = jobRepository.save(job);
		
		JSONArray listKpsc = object.getJSONArray("kpscs");
		if(listKpsc != null && listKpsc.length() > 0) {
			for(int i = 0; i < listKpsc.length(); i++) {
				Kpsc kpsc = new Kpsc();
				JSONObject jsonKpsc = listKpsc.getJSONObject(i);
				if (!jsonKpsc.isNull("deviceId")) {
					kpsc.setDevice(deviceRepository.getById(jsonKpsc.getLong("deviceId")));
				}
				if (!jsonKpsc.isNull("jobPerformId")) {
					kpsc.setJobPerform(jobPerformRepository.getById(jsonKpsc.getLong("jobPerformId")));
				}
				if (!jsonKpsc.isNull("errorDesc")) {
					kpsc.setErrorDesc(jsonKpsc.getString("errorDesc"));
				}
				kpsc.setJob(job);
				kpsc = kpscRepository.save(kpsc);
				
				JSONArray listReplacementAccessory = jsonKpsc.getJSONArray("replacementAccessories");
				if(listReplacementAccessory != null && listReplacementAccessory.length() > 0) {
					for(int j = 0; j < listReplacementAccessory.length(); j++) {
						JSONObject jsonReplacementAccessory = listReplacementAccessory.getJSONObject(j);
						
						// add list linh kiện thay thế
						listJsonAcc.put(jsonReplacementAccessory);
						
						ReplacementAccessory replacementAccessory = new ReplacementAccessory();
						replacementAccessory.setErrorDevice(errorDeviceRepository.getById(jsonReplacementAccessory.getLong("errorDeviceId")));
						
						replacementAccessory.setAccessory(accessoryRepository.getById(jsonReplacementAccessory.getLong("accessoryId")));
						replacementAccessory.setQuantity(jsonReplacementAccessory.getInt("quantity"));
						replacementAccessory.setKpsc(kpsc);
						replacementAccessoryRepository.save(replacementAccessory);
					}
				}
			}
		}
		
		// tạo phiếu xuất kho
		applicationEventPublisher.publishEvent(new UpdateInventoryCheckOutEvent(this, listJsonAcc, username, job.getCheckOutTime()));
		
		applicationEventPublisher.publishEvent(new CloneStatisticalEvent(this, job));
		applicationEventPublisher.publishEvent(new CheckOutEvent(this, job));
		
		data.put("jobId", job.getId());
		resultApi.setData(data);
		resultApi.setMessage("Check out thành công");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi jobProcessing(String username) {
		log.debug("Processing jobProcessing service()....");
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		List<Job> jobs = jobRepository.getByUsernameAndStatus(username, JobStatusEnum.IN_PROCESS.getKey());
		List<JobDto> jobDtos = jobs.stream().map(job -> classMapper.convertToJobDtoWithoutUser(job)).collect(Collectors.toList());
		
		data.put("jobs", jobDtos);
		resultApi.setData(data);
		resultApi.setMessage("Success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	/*----------------- Api get reference data ----------------------*/
	public ResultApi getAllDevice() {
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		List<Device> devices = deviceRepository.findAll();
		List<DeviceDto> deviceDtos = devices.stream().map(device -> classMapper.convertToDeviceDto(device)).collect(Collectors.toList());
		
		data.put("devices", deviceDtos);
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi getAccessoryByErrorDeviceId(Long errorDeviceId) {
		log.debug("Processing getAccessoryByDeviceId service()....");
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		ErrorDevice errorDevice = errorDeviceRepository.getById(errorDeviceId);
		
		data.put("errorDevice", classMapper.convertToErrorDeviceDto(errorDevice));
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi getErrorByDeviceId(Long deviceId) {
		log.debug("Processing getErrorByDeviceId service()....");
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		Device device = deviceRepository.getById(deviceId);
		
		data.put("device", classMapper.convertToDeivceDtoWithError(device));
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi getAllJobPerform() {
		log.debug("Processing getDeviceById service()....");
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		List<JobPerform> jobPerforms = jobPerformRepository.findAll();
		
		data.put("jobPerforms", jobPerforms.stream().map(j -> classMapper.convertToJobPerformDto(j)).collect(Collectors.toList()));
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi getAllError() {
		log.debug("Processing getAllError service()....");
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		List<Error> errors = errorRepository.findAll();
		
		data.put("errors", errors.stream().map(err -> classMapper.convertToErrorDto(err)).collect(Collectors.toList()));
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi getAllAccessory() {
		log.debug("Processing getAllAccessory service()....");
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		List<Accessory> accessories = accessoryRepository.findAll();
		
		data.put("accessories", accessories.stream().map(acc -> classMapper.convertToAccessoryDtoWithSeries(acc)).collect(Collectors.toList()));
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi searchAtm(String input) {
		log.debug("Processing searchAtm service()....");
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		List<Map<String, Object>> atms = new ArrayList<>();
		
		Specification<ATM> spec = objectSpecification.searchATMBySerial(input);
		List<ATM> list = atmRepository.findAll(spec);
		if (list != null && !list.isEmpty()) {
			list.stream().forEach(a -> {
				Map<String, Object> atm = new HashMap<>();
				atm.put("serialNumber", a.getSerialNumber());
				atm.put("address", a.getAddress());
				atm.put("status", a.getStatus());
				atm.put("statusDesc", AtmStatusEnum.fromKey(a.getStatus()).getValue());
				atms.add(atm);
			});
		}
		
		data.put("atms", atms);
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi getAllAtm() {
		log.debug("Processing getAllAtm service()....");
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		
		List<ATM> atms = atmRepository.findAll();
		
		List<Map<String, String>> listData = atms.stream().map(atm -> {
			Map<String, String> item = new HashMap<>();
			item.put("serialNumber", atm.getSerialNumber());
			item.put("address", atm.getAddress());
			
			return item;
		}).collect(Collectors.toList());
		
		data.put("atms", listData);
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi getUserDetail(String username) {
		log.debug("get user info by username = " + username);
		ResultApi resultApi = new ResultApi();
		Map<String, Object> data = new HashMap<>();
		
		User user = userRepository.findByUsername(username);
		
		data.put("username", username);
		data.put("fullName", user.getFullName());

		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	/*----------------- Api get reference data ----------------------*/

	/*----------------------- Pagination ---------------------------*/
	
	public ResultApi atmHistoryPagination(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		ResultApi resultApi = new ResultApi();
		Map<String, Object> data = new HashMap<>();
		
		Specification<Job> spec = objectSpecification.getAtmHistory(ob.isNull("serialNumber") ? null : ob.getString("serialNumber"));
		Page<Job> page = jobRepository.findAll(spec, PageRequest.of(ob.getInt("pageNumber"), 5));
		
		List<JobDto> jobDtos = page.getContent().stream().map(item -> classMapper.convertToJobDtoWithUser(item)).collect(Collectors.toList());
		
//		data.put("jobs", new PageImpl<>(jobDtos, PageRequest.of(ob.getInt("pageNumber"), 5), page.getTotalElements()));
		data.put("jobs", jobDtos);
		data.put("currentPage", ob.getInt("pageNumber"));
		data.put("totalElements", page.getTotalElements());
		data.put("totalPages", page.getTotalPages());
		resultApi.setData(data);
		resultApi.setMessage("Success!");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi personalHistoryPage(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		ResultApi resultApi = new ResultApi();
		Map<String, Object> data = new HashMap<>();
		
		String username = ob.isNull("username") ? null : ob.getString("username");
		String search = ob.isNull("search") ? null : ob.getString("search");
		
		Specification<Job> spec = objectSpecification.getPersonalHistory(username, search);
		Page<Job> page = jobRepository.findAll(spec, PageRequest.of(ob.getInt("pageNumber"), 5));
		
		List<JobDto> jobDtos = page.getContent().stream().map(item -> classMapper.convertToJobDtoWithUser(item)).collect(Collectors.toList());
	
		data.put("jobs", jobDtos);
		data.put("currentPage", ob.getInt("pageNumber"));
		data.put("totalElements", page.getTotalElements());
		data.put("totalPages", page.getTotalPages());
		resultApi.setData(data);
		resultApi.setMessage("Success!");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi getAllAtm(String body) throws JSONException {
		log.debug("Processing getAllAtm service()....");
		Map<String, Object> data = new HashMap<>();
		ResultApi resultApi = new ResultApi();
		JSONObject ob = new JSONObject(body);
		
		Specification<ATM> spec = objectSpecification.searchATM(ob.isNull("search") ? null : ob.getString("search"));
		Page<ATM> page = atmRepository.findAll(spec, PageRequest.of(ob.getInt("pageNumber"), 5));
				
//		List<ATM> atms = atmRepository.findAll();
		
		List<Map<String, Object>> listData = page.getContent().stream().map(atm -> {
			Map<String, Object> item = new HashMap<>();
			item.put("serialNumber", atm.getSerialNumber());
			item.put("address", atm.getAddress());
			item.put("status", atm.getStatus());
			item.put("statusDesc", AtmStatusEnum.fromKey(atm.getStatus()).getValue());
			
			return item;
		}).collect(Collectors.toList());
		
		data.put("atms", listData);
		data.put("currentPage", ob.getInt("pageNumber"));
		data.put("totalElements", page.getTotalElements());
		data.put("totalPages", page.getTotalPages());
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	public ResultApi getAllErrorDevice() {
		ResultApi resultApi = new ResultApi();
		Map<String, Object> data = new HashMap<>();
		
		List<ErrorDevice> errorDevices = errorDeviceRepository.findAll();
		List<ErrorDeviceDto> errorDeviceDtos = errorDevices.stream().map(item -> classMapper.convertToErrorDeviceDtoWithSeries(item)).collect(Collectors.toList());
		
		data.put("errorDevices", errorDeviceDtos);
		resultApi.setData(data);
		resultApi.setMessage("success");
		resultApi.setSuccess(true);
		return resultApi;
	}
	
	/*----------------------- Pagination ---------------------------*/
}
