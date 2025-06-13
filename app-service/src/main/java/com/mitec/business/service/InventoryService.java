package com.mitec.business.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitec.business.dto.InventoryDto;
import com.mitec.business.dto.PartInventoryDto;
import com.mitec.business.dto.TrackingDetailDto;
import com.mitec.business.dto.UserDto;
import com.mitec.business.event.CreateSuggestionEvent;
import com.mitec.business.model.Accessory;
import com.mitec.business.model.ErrorDevice;
import com.mitec.business.model.Inventory;
import com.mitec.business.model.PartInventory;
import com.mitec.business.model.Region;
import com.mitec.business.model.Suggestion;
import com.mitec.business.model.Tracking;
import com.mitec.business.model.TrackingDetail;
import com.mitec.business.model.User;
import com.mitec.business.repository.AccessoryRepository;
import com.mitec.business.repository.ErrorDeviceRepository;
import com.mitec.business.repository.InventoryRepository;
import com.mitec.business.repository.PartInventoryRepository;
import com.mitec.business.repository.RegionRepository;
import com.mitec.business.repository.SuggestionRepository;
import com.mitec.business.repository.TrackingDetailRepository;
import com.mitec.business.repository.TrackingRepository;
import com.mitec.business.repository.UserRepository;
import com.mitec.business.specification.InventorySpecs;
import com.mitec.business.utils.PartInventoryTypeEnum;
import com.mitec.business.utils.TrackingStatusEnum;
import com.mitec.business.utils.TrackingTypeEnum;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Slf4j
@Service
public class InventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private PartInventoryRepository partInventoryRepository;
	@Autowired
	private ErrorDeviceRepository errorDeviceRepository;
	@Autowired
	private AccessoryRepository accessoryRepository;
	@Autowired
	private TrackingRepository trackingRepository;
	@Autowired
	private TrackingDetailRepository trackingDetailRepository;
	@Autowired
	private SuggestionRepository suggestionRepository;
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	static final String ID = "id";
	static final String ADDRESS = "address";
	static final String REGION_ID = "regionId";
	static final String NAME = "name";
	static final String USER_IDS = "userIds";
	static final String RECEIVED_INVENTORY = "receivedInventory";
	static final String PARTS = "parts";
	static final String RECEIVER = "receiver";
	static final String PHONE_NUMBER = "phoneNumber";
	static final String RECEIVED_DEPARTMENT = "receivedDepartment";
	static final String RECEIVED_ADDRESS = "receivedAddress";
	
	public boolean checkInventory(String username, Long inventoryId) {
		if (userRepository.isAdmin(username)) {
			return true;
		}
		return inventoryRepository.isExistInventory(username, inventoryId);
	}
	
	public List<User> getUsers() {
//		return userRepository.getAllNonAdminUser();
		return userRepository.findAll();
	}
	
	public List<String> getUserSuggestions(String username) {
//		return suggestionRepository.findByUsername(username);
		List<Suggestion> list = suggestionRepository.findAll();
		List<String> result = new ArrayList<>();
		
		if (!list.isEmpty()) {
			result = list.stream().map(item -> {
				StringBuilder sb = new StringBuilder();
				sb.append(item.getName());
				sb.append("/");
				sb.append(item.getPhone());
				
				return sb.toString();
			}).collect(Collectors.toList());
		}
		return result;
	}
	
	public Suggestion getSelectedSuggestion(String key) {
		int index = key.indexOf("/");
		String name = key.substring(0, index);
		String phone = key.substring(index + 1, key.length());
		
		return suggestionRepository.findByNameAndPhone(name, phone).orElse(null);
	}
	
	// Admin
	public Page<Inventory> getInventories(String name, int pageNumber, int pageSize) {
		Specification<Inventory> spec = InventorySpecs.searchInventories(name);
		return inventoryRepository.findAll(spec, PageRequest.of(pageNumber, pageSize));
	}
	
	// Lấy list inventories cho cán bộ không phải là Admin
	public List<Inventory> getInventories(String username) {
		return inventoryRepository.findByUsername(username);
	}
	
	public List<Inventory> getAllButNotId(Long id) {
		log.debug("size " + inventoryRepository.findByIdNot(id).size());
		return inventoryRepository.findByIdNot(id);
	}
	
	public boolean isAdmin(String username) {
		return userRepository.existsRoleInUser(username, "ROLE_ADMIN");
	}
	
	public Page<PartInventory> getPartInventory(Long id, String type, String name, int pageNumber, int pageSize) {
		Specification<PartInventory> spec = InventorySpecs.searchPart(id, type, name);
		
		return partInventoryRepository.findAll(spec, PageRequest.of(pageNumber, pageSize));
	}
	
	public Inventory createInventory(String body) throws JSONException {
		log.debug("Đang tạo mới kho()....");
		JSONObject ob = new JSONObject(body);
		Inventory inventory = new Inventory();
		inventory.setName(ob.getString(NAME));
		if (!ob.isNull(ADDRESS) && StringUtils.isNotBlank(ob.getString(ADDRESS))) {
			inventory.setAddress(ob.getString(ADDRESS)); 
		}
		if (!ob.isNull(REGION_ID) && StringUtils.isNotBlank(ob.getString(REGION_ID))) {
			Region region = regionRepository.getById(ob.getLong(REGION_ID));
			inventory.setRegionId(region.getId());
			inventory.setRegionName(region.getName());
		}
		JSONArray userIds = new JSONArray(ob.getString(USER_IDS));
		List<User> users = new ArrayList<>();
		if (userIds.length() > 0) {
			for (int i = 0; i < userIds.length(); i++) {
				users.add(userRepository.getById(userIds.getLong(i)));
			}
		}
		inventory.setUsers(users);
		
		inventory = inventoryRepository.save(inventory);
		insertPartInventory(inventory);
		return inventory;
	}
	
	// Thêm part-inventory sau khi tạo mới kho
	private void insertPartInventory(Inventory inventory) {
		List<ErrorDevice> devices = errorDeviceRepository.findAll();
		List<Accessory> accessories = accessoryRepository.findAll();
		
		if (!devices.isEmpty()) {
			devices.stream().forEach(item -> {
				PartInventory part = new PartInventory();
				part.setInventory(inventory);
				part.setPartId(item.getId());
				part.setName(item.getName());
				part.setType(PartInventoryTypeEnum.DIVICE.getKey());
				
				partInventoryRepository.save(part);
			});
		}
		if (!accessories.isEmpty()) {
			accessories.stream().forEach(item -> {
				PartInventory part = new PartInventory();
				part.setInventory(inventory);
				part.setPartId(item.getId());
				part.setName(item.getName());
				part.setType(PartInventoryTypeEnum.ACCESSORY.getKey());
				
				partInventoryRepository.save(part);
			});
		}
	}
	
	public Inventory updateInventory(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Inventory inventory = inventoryRepository.getById(ob.getLong(ID));
		inventory.setName(ob.getString(NAME));
		inventory.setAddress(ob.getString(ADDRESS));
		if (ob.isNull(REGION_ID) && StringUtils.isNotBlank(ob.getString(REGION_ID))) {
			Region region = regionRepository.getById(ob.getLong(REGION_ID));
			inventory.setRegionId(region.getId());
			inventory.setRegionName(region.getName());
		}
		JSONArray userIds = new JSONArray(ob.getString(USER_IDS));
		List<User> users = new ArrayList<>();
		if (userIds.length() > 0) {
			for (int i = 0; i < userIds.length(); i++) {
				users.add(userRepository.getById(userIds.getLong(i)));
			}
		}
		inventory.setUsers(users);
		return inventoryRepository.save(inventory);
	}
	
//	public List<PartInventory> getParts(String body) throws JSONException {
//		JSONObject ob = new JSONObject(body);
//		return partInventoryRepository.findByInventoryId(ob.getLong("inventoryId"));
//	}
	
	@Transactional
	public void deleteInventory(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		if (!ob.isNull("id")) {
			inventoryRepository.deleteById(ob.getLong(ID));
		}
	}
	
	public InventoryDto get(Long id) throws IllegalAccessException, InvocationTargetException {
		Inventory inventory = inventoryRepository.getById(id);
		InventoryDto inventoryDto = new InventoryDto();
		
		BeanUtils.copyProperties(inventoryDto, inventory);
		
		List<Long> uIds = inventory.getUsers().stream().map(item -> item.getId()).collect(Collectors.toList());
		inventoryDto.setUserIds(uIds.stream().toArray(Long[]::new));
		
		return inventoryDto;
	}
	
	public String getName(Long inventoryId) {
		return inventoryRepository.getNameById(inventoryId);
	}
	
	public List<PartInventoryDto> getPartByType(Integer partType, Long inventoryId) {
		return partInventoryRepository.findByTypeAndInventoryId(partType, inventoryId);
	}
	
	public List<PartInventoryDto> findByTypeAndQuantityGtZero(Integer partType, Long inventoryId) {
		return partInventoryRepository.findByTypeAndQuantityGtZero(partType, inventoryId);
	}
	
	public Long getQuantity(Long partId) {
		return partInventoryRepository.getQuantityByPartId(partId);
	}
	
	public List<UserDto> getUserByInventory(Long inventoryId) {
		Inventory inventory = inventoryRepository.getById(inventoryId);
		List<UserDto> userDtos = new ArrayList<>();
		if (inventory.getUsers() != null && !inventory.getUsers().isEmpty()) {
			userDtos = inventory.getUsers().stream().map(item -> {
				UserDto user = new UserDto();
				user.setId(item.getId());
				user.setUsername(item.getUsername());
				return user;
			}).collect(Collectors.toList());
		}
		return userDtos;
	}
	
	// Xuất phiếu gửi hàng
	public byte[] genGoodsIssue(Long id)
			throws JSONException, JRException, IOException {
		log.debug("Proccessing genGoodsIssue()....");

		Tracking tracking = trackingRepository.getById(id);
		
		/* Compile report template */
		InputStream inputStream = new ClassPathResource("static/reports/Phieu-gui-hang-thong-tin-nguoi-nhan.jrxml").getInputStream();
		JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

		/* Fill data from json to template */
		Map<String, Object> parameters = new HashMap<>();
		String receiver = "";
		String department = "";
		String address = "";
		String phoneNumber = "";
		StringBuilder quantityStr = new StringBuilder();
		if (tracking.getType().equals(TrackingTypeEnum.SUA_CHUA.getKey())) {
			Inventory inventory = inventoryRepository.getById(tracking.getReceivedInventory());
			Long userId = inventory.getUsers().get(0).getId();
			User user = userRepository.getById(userId);
			department = "MITEC Jsc.";
			address = inventory.getAddress();
			if (StringUtils.isNotBlank(user.getFullName())) {
				receiver = user.getFullName();
			}else {
				receiver = user.getUsername();
			}
			phoneNumber = user.getPhoneNumber();
		}else {
			receiver = tracking.getReceiver();
			department = tracking.getReceivedDepartment();
			address = tracking.getReceivedAddress();
			phoneNumber = tracking.getReceivedPhone();
		}
		
		Long quantity = 0L;
		List<TrackingDetailDto> list = new ArrayList<>();
		for (TrackingDetail item : tracking.getTrackingDetails()) {
			quantity += item.getQuantity();
			TrackingDetailDto pdfTrackingDto = new TrackingDetailDto(item.getPartName(), item.getQuantity());
			list.add(pdfTrackingDto);
		}
		
		quantityStr.append(new DecimalFormat("##").format(quantity));
		quantityStr.append("/");
		quantityStr.append(new DecimalFormat("##").format(quantity));
		
		JRBeanCollectionDataSource parts = new JRBeanCollectionDataSource(list);
		parameters.put(PARTS, parts);
		
		parameters.put("receiver", receiver);
		parameters.put("department", department);
		parameters.put("address", address);
		parameters.put("phoneNumber", phoneNumber);
		parameters.put("quantity", quantityStr.toString());

		return JasperExportManager
				.exportReportToPdf(JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource()));
	}
	
	public byte[] genGoodsIssueDetail(Long id)
			throws JSONException, JRException, IOException {
		log.debug("Proccessing genGoodsIssueDetail()....");

		Tracking tracking = trackingRepository.getById(id);
		
		/* Compile report template */
		InputStream inputStream = new ClassPathResource("static/reports/Danh-sach-linh-kien-gui-di.jrxml").getInputStream();
		JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

		/* Fill data from json to template */
		Map<String, Object> parameters = new HashMap<>();
		List<TrackingDetailDto> list = new ArrayList<>();
		
		for (TrackingDetail item : tracking.getTrackingDetails()) {
			TrackingDetailDto pdfTrackingDto = new TrackingDetailDto(item.getPartName(), item.getQuantity());
			list.add(pdfTrackingDto);
		}
		
		JRBeanCollectionDataSource parts = new JRBeanCollectionDataSource(list);
		parameters.put(PARTS, parts);

		return JasperExportManager
				.exportReportToPdf(JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource()));
	}
	
	public Tracking getById(Long id) {
		return trackingRepository.getById(id);
	}
	
	public byte[] getTemplateGoodsReceipt() throws IOException {
		File file = new ClassPathResource("static/reports/mau-nhap-kho.xlsx").getFile();
		return Files.readAllBytes(file.toPath());
	}
	
	public UserDto getUserData(String username) throws IllegalAccessException, InvocationTargetException {
		User user = userRepository.findByUsername(username);
		UserDto userDto = new UserDto();
		
		BeanUtils.copyProperties(userDto, user); 
		if (user.getDepartment() != null) {
			userDto.setDepartmentId(user.getDepartment().getId());
			userDto.setDepartmentName(user.getDepartment().getName());
		}
		return userDto;
	}
	
	// insert data from excel to dataTable
	public List<TrackingDetailDto> importReceiptData(MultipartFile file, String parts, Long inventoryId) throws JsonProcessingException {
		log.debug("Processing import atm data service()....");
		List<TrackingDetailDto> list = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		TrackingDetailDto[] arr = mapper.readValue(parts, TrackingDetailDto[].class);
		if (arr != null) {
			list.addAll(Arrays.asList(arr));
		}
		
		try {
			Workbook workbook = new XSSFWorkbook(file.getInputStream());
			XSSFSheet worksheet = (XSSFSheet) workbook.getSheetAt(0);
			
			int i = 1;
			while (i <= worksheet.getLastRowNum()) {
				XSSFRow dataRow = worksheet.getRow(i++);
				if (dataRow != null) {
					String typeStr = null;
					String partName = null;
					Integer type = null;
					Long quantity = null;
					// Load data từ excel
					try {
						typeStr = dataRow.getCell(0).getStringCellValue();
						partName = dataRow.getCell(1).getStringCellValue();
						Double quantityDb = dataRow.getCell(2).getNumericCellValue();
						quantity = quantityDb.longValue();
						type = PartInventoryTypeEnum.fromValue(typeStr.trim()).getKey();
					}catch (Exception e) {
						e.printStackTrace();
					}
					
					if (typeStr != null && partName != null && quantity != null) {
						PartInventory part = partInventoryRepository.getPartInventory(inventoryId, partName.trim(), type);
						
						if (part != null) {
							list.add(new TrackingDetailDto(part.getId(), partName, type, typeStr, quantity));
						}
					}
				}
			}
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Errors: " + e.toString());
		}
		
		
		Map<Long, Long> mapValue = list.stream().collect(Collectors.groupingBy(TrackingDetailDto::getPartId, Collectors.summingLong(TrackingDetailDto::getQuantity)));
		return mapValue.keySet().stream().map(item -> TrackingDetailDto.forQuantity(partInventoryRepository.getById(item), mapValue.get(item))).collect(Collectors.toList());
	}
	
	@Transactional
	public Tracking saveGoodsReceipt(String body, String username) throws JsonProcessingException, JSONException {
		JSONObject ob = new JSONObject(body);
		
//		Create new tracking
		Tracking tracking = new Tracking();
		tracking.setReceivedInventory(ob.getLong("inventoryId"));
		tracking.setReceivedDate(LocalDateTime.now());
		tracking.setType(TrackingTypeEnum.NHAP_KHO.getKey());
		tracking.setStatus(TrackingStatusEnum.DA_NHAN.getKey());
		tracking.setTradingCode(genTradingCode());
		
		User user = userRepository.findByUsername(username);
		tracking.setReceiver(username);
		tracking.setReceivedPhone(user.getPhoneNumber());
		if (user.getDepartment() != null) {
			tracking.setReceivedDepartment(user.getDepartment().getName());
		}
		
		tracking = trackingRepository.save(tracking);
		
		saveTrackingDetails(ob.getString(PARTS), tracking);
		
		return tracking;
	}
	
	@Transactional
	public Tracking saveGoodsIssue(String body, String username) throws JsonProcessingException, JSONException {
		JSONObject ob = new JSONObject(body);
		
//		Create new tracking
		Tracking tracking = new Tracking();
		tracking.setSendDate(LocalDateTime.now());
		if (!ob.isNull(RECEIVED_INVENTORY) && StringUtils.isNotBlank(ob.getString(RECEIVED_INVENTORY))) {
			tracking.setReceivedInventory(ob.getLong(RECEIVED_INVENTORY));
		}
		if (!ob.isNull(RECEIVER) && StringUtils.isNotBlank(ob.getString(RECEIVER))) {
			tracking.setReceiver(ob.getString(RECEIVER));
		}
		if (!ob.isNull(PHONE_NUMBER) && StringUtils.isNotBlank(ob.getString(PHONE_NUMBER))) {
			tracking.setReceivedPhone(ob.getString(PHONE_NUMBER));
		}
		if (!ob.isNull(RECEIVED_DEPARTMENT) && StringUtils.isNotBlank(ob.getString(RECEIVED_DEPARTMENT))) {
			tracking.setReceivedDepartment(ob.getString(RECEIVED_DEPARTMENT));
		}
		if (!ob.isNull(RECEIVED_ADDRESS) && StringUtils.isNotBlank(ob.getString(RECEIVED_ADDRESS))) {
			tracking.setReceivedAddress(ob.getString(RECEIVED_ADDRESS));
		}
		if (!ob.isNull("note")) {
			tracking.setNote(ob.getString("note"));
		}
		
		tracking.setStatus(TrackingStatusEnum.DANG_CHO.getKey());
		tracking.setType(TrackingTypeEnum.XUAT_KHO.getKey());
		tracking.setSender(username);
		tracking.setSendDate(LocalDateTime.now());
		tracking.setSendedInventory(ob.getLong("inventoryId"));
		tracking.setTradingCode(genTradingCode());
		
		tracking = trackingRepository.save(tracking);

		saveTrackingDetails(ob.getString(PARTS), tracking);
		
		applicationEventPublisher.publishEvent(new CreateSuggestionEvent(this, tracking.getReceiver(), 
				tracking.getReceivedPhone(), tracking.getReceivedDepartment(), tracking.getReceivedAddress()));
		
		return tracking;
	}
	
	private void saveTrackingDetails(String parts, Tracking tracking) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		TrackingDetailDto[] arr = mapper.readValue(parts, TrackingDetailDto[].class);
		List<TrackingDetailDto> list = Arrays.asList(arr);
		
		// Save list Tracking detail
		for (TrackingDetailDto item : list) {
			PartInventory partInventory = partInventoryRepository.getById(item.getPartId());
			
			TrackingDetail trackingDetail = new TrackingDetail();
			trackingDetail.setPartId(partInventory.getPartId());
			trackingDetail.setPartName(partInventory.getName());
			trackingDetail.setPartInventoryId(partInventory.getId());
			trackingDetail.setTracking(tracking);
			trackingDetail.setType(item.getPartType());
			trackingDetail.setQuantity(item.getQuantity());
			
			trackingDetailRepository.save(trackingDetail);
		}
	}
	
	// gen code
	public static String genTradingCode() { 
		StringBuilder code = new StringBuilder();
		LocalDateTime time = LocalDateTime.now();
		String dateStr = time.format(DateTimeFormatter.ofPattern("ddMMyy"));
		code.append(dateStr);
		
		int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 5;
	    Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
		
	    code.append(generatedString);
	    
		String timeStr = time.format(DateTimeFormatter.ofPattern("HHmm"));
		code.append(timeStr);
		
		return code.toString().toUpperCase();
	}
	
	@Transactional
	public Tracking saveRepairPart(String body, String username) throws JsonProcessingException, JSONException {
		JSONObject ob = new JSONObject(body);
		
//		Create new tracking
		Tracking tracking = new Tracking();
		tracking.setSendDate(LocalDateTime.now());
		if (!ob.isNull(RECEIVER) && StringUtils.isNotBlank(ob.getString(RECEIVER))) {
			tracking.setReceiver(ob.getString(RECEIVER));
		}
		if (!ob.isNull(PHONE_NUMBER) && StringUtils.isNotBlank(ob.getString(PHONE_NUMBER))) {
			tracking.setReceivedPhone(ob.getString(PHONE_NUMBER));
		}
		if (!ob.isNull(RECEIVED_ADDRESS) && StringUtils.isNotBlank(ob.getString(RECEIVED_ADDRESS))) {
			tracking.setReceivedAddress(ob.getString(RECEIVED_ADDRESS));
		}
		if (!ob.isNull("note")) {
			tracking.setNote(ob.getString("note"));
		}
		
		tracking.setStatus(TrackingStatusEnum.DANG_CHO.getKey());
		tracking.setType(TrackingTypeEnum.SUA_CHUA.getKey());
		tracking.setSender(username);
		tracking.setSendDate(LocalDateTime.now());
		tracking.setSendedInventory(ob.getLong("inventoryId"));
		tracking.setTradingCode(genTradingCode());
		
		tracking = trackingRepository.save(tracking);

		saveTrackingDetails(ob.getString(PARTS), tracking);
		
		return tracking;
	}
	
	public Page<Tracking> getHistory(Long inventoryId, String name, String status, String type, int pageNumber, int pageSize) {
		Specification<Tracking> spec = InventorySpecs.searchHistory(inventoryId, name, status, type);
		return trackingRepository.findAll(spec, PageRequest.of(pageNumber, pageSize));
	}
	
	public Tracking getTracking(Long id) {
		return trackingRepository.findById(id).orElse(null);
	}
	
	@Transactional
	public void updateTracking(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long trackingId = null;
		
		if (!ob.isNull("trackingId") && StringUtils.isNotBlank(ob.getString("trackingId"))) {
			trackingId = ob.getLong("trackingId");
		}
		
		if (trackingId != null) {
			Tracking tracking = trackingRepository.getById(trackingId);
			tracking.setReceivedDate(LocalDateTime.now());
			tracking.setStatus(TrackingStatusEnum.DA_NHAN.getKey());
			
			trackingRepository.save(tracking);
			
			List<TrackingDetail> list = tracking.getTrackingDetails();
			list.stream().forEach(item -> partInventoryRepository.updateQuantity(tracking.getReceivedInventory(), item.getPartId(), item.getType(), item.getQuantity()));
		}
	}
}
