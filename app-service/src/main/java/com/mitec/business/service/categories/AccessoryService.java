package com.mitec.business.service.categories;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mitec.business.dto.AccessoryDto;
import com.mitec.business.event.AddNewPartEvent;
import com.mitec.business.event.UpdatePartEvent;
import com.mitec.business.model.Accessory;
import com.mitec.business.model.ErrorDevice;
import com.mitec.business.model.Series;
import com.mitec.business.repository.AccessoryRepository;
import com.mitec.business.repository.ErrorDeviceRepository;
import com.mitec.business.repository.SeriesRepository;
import com.mitec.business.specification.ObjectSpecification;
import com.mitec.business.utils.ClassMapper;
import com.mitec.business.utils.PartInventoryTypeEnum;

@Service
public class AccessoryService {

	@Autowired
	private AccessoryRepository accessoryRepository;
	@Autowired
	private ObjectSpecification objectSpecification;
	@Autowired
	private ClassMapper classMapper;
	@Autowired
	private ErrorDeviceRepository errorDeviceRepository;
	@Autowired
	private SeriesRepository seriesRepository;
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	public Accessory saveAccessories(String body) throws JSONException {
		Accessory accessory = new Accessory();
		JSONObject ob = new JSONObject(body);
		accessory.setName(ob.getString("name"));
		Long errorDeviceId = Long.parseLong(ob.getString("device"));
		ErrorDevice errorDevice = errorDeviceRepository.getById(errorDeviceId);
		accessory.setErrorDevice(errorDevice);
		
		JSONArray jsonArray = new JSONArray(ob.getString("series"));
		List<Long> list = new ArrayList<>();
		for (int i=0; i<jsonArray.length(); i++) {
		    list.add( Long.parseLong(jsonArray.getString(i)) );
		}
		List<Series> listSeries = seriesRepository.getByListId(list);
		accessory.setSeries(listSeries);
		
		accessory = accessoryRepository.save(accessory);
		
		applicationEventPublisher.publishEvent(new AddNewPartEvent(this, accessory.getId(), accessory.getName(), PartInventoryTypeEnum.ACCESSORY.getKey()));
		
		return accessory;
	}
	
	public List<AccessoryDto> listAccessories() {
		List<Accessory> list = accessoryRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
		List<AccessoryDto> result = new ArrayList<>();
		if (!list.isEmpty()) {
			result = list.stream().map(a -> {
				AccessoryDto item = new AccessoryDto();
				BeanUtils.copyProperties(a, item);
				return item;
			}).collect(Collectors.toList());
		}
		return result;
	}

	public Page<Accessory> listAllAccessories(int pageNumber, int size) {
		Specification<Accessory> spec = objectSpecification.searchAccessory();
		return accessoryRepository.findAll(spec, PageRequest.of(pageNumber, size));
	}
	
	public void deleteAccessories(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		accessoryRepository.deleteById(id);
	}
	
	public AccessoryDto getAccessories(String body) throws JSONException{
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		Accessory acc = accessoryRepository.getById(id);
		return classMapper.convertToAccessoryDto(acc);
	}
	
	public Accessory updateAccessories(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		Accessory accessory = accessoryRepository.getById(id);
		accessory.setName(ob.getString("name"));
		
		Long errorDeviceId = Long.parseLong(ob.getString("device"));
		ErrorDevice errorDevice = errorDeviceRepository.getById(errorDeviceId);
		accessory.setErrorDevice(errorDevice);
		
		JSONArray jsonArray = new JSONArray(ob.getString("series"));
		List<Long> list = new ArrayList<>();
		for (int i=0; i<jsonArray.length(); i++) {
		    list.add( Long.parseLong(jsonArray.getString(i)) );
		}
		List<Series> listSeries = seriesRepository.getByListId(list);
		accessory.setSeries(listSeries);
		
		applicationEventPublisher.publishEvent(new UpdatePartEvent(this, accessory.getId(), accessory.getName(), PartInventoryTypeEnum.ACCESSORY.getKey()));
		
		return accessoryRepository.save(accessory);
	}
	
	public List<Accessory> getAll() {
		return accessoryRepository.findAll();
	}

	public byte[] exportData() throws IOException {
		InputStream inputStream = new ClassPathResource("static/reports/template-export-accessory.xlsx").getInputStream();
		Workbook workbook = new XSSFWorkbook(inputStream);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		List<Accessory> accessories = accessoryRepository.findAll();

		Sheet sheet = workbook.getSheetAt(0);
		int row = 1;
		int column = 0;
		
		if (!accessories.isEmpty()) {
			for (Accessory item : accessories) {
				Row rowItem = sheet.createRow(row);
				
				Cell cell = rowItem.createCell(column);
				cell.setCellValue(item.getId());
				
				cell = rowItem.createCell(column + 1);
				cell.setCellValue(item.getErrorDevice().getName());
				
				cell = rowItem.createCell(column + 2);
				cell.setCellValue(item.getName());
				
				cell = rowItem.createCell(column + 3);
				cell.setCellValue(item.getSeriesStr());
				
				row++;
			}
			workbook.write(bos);
			workbook.close();
			return bos.toByteArray();
		}

		workbook.close();
		return new byte[0];
	}
	
	public Map<String, Object> importData(MultipartFile excelFile) {
		Map<String, Object> result = new HashMap<>();
		boolean isSuccess = false;
		String messages = "";
		List<Series> listAll = seriesRepository.findAll();
		List<ErrorDevice> listDevice = errorDeviceRepository.findAll();
		
		// vị trí hiện tại của table
		int row = 1;
		int column = 0;
		
		try {
			Workbook workbook = new XSSFWorkbook(excelFile.getInputStream());
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			while (row <= sheet.getLastRowNum()) {
				XSSFRow dataRow = sheet.getRow(row);
				if (dataRow != null) {
					Long id = null;
					Cell dataCell = dataRow.getCell(column);
					Accessory acc = new Accessory();
					
					if (dataCell != null) {
						id = Math.round(dataCell.getNumericCellValue());
					}
					dataCell = dataRow.getCell(column + 1);
					if (dataCell != null) {
						String device = dataCell.getStringCellValue();
						acc.setErrorDevice(getErrorDevice(device, listDevice));
					}else {
						workbook.close();
						messages = "Lỗi định dạng ở dòng " + (row + 1) + ". Vui lòng kiểm tra lại!";
						result.put("isSuccess", isSuccess);
						result.put("messages", messages);
						return result;
					}
					
					dataCell = dataRow.getCell(column + 2);
					String name = dataCell.getStringCellValue();
	
					dataCell = dataRow.getCell(column + 3);
					String seriesStr = dataCell.getStringCellValue();
					
					acc.setId(id);
					acc.setName(name);
					acc.setSeries(convertStrToSeries(seriesStr, listAll));
					
					accessoryRepository.save(acc);
					row++;
				}

			}
			workbook.close();
			isSuccess = true;
			messages = "Import dữ liệu thành công!";
		}catch (Exception e) {
			e.printStackTrace();
			messages = "Lỗi định dạng ở dòng " + (row + 1) + ". Vui lòng kiểm tra lại!";
			result.put("isSuccess", isSuccess);
			result.put("messages", messages);
			return result;
		} 
		
		result.put("isSuccess", isSuccess);
		result.put("messages", messages);
		return result;
	}
	
	private List<Series> convertStrToSeries(String seriesStr, List<Series> listSeries) {
		List<Series> list = new ArrayList<>();
		String[] arr = seriesStr.split(",");
		for (String str : arr) {
			for (Series series : listSeries) {
				if (series.getName().equals(str.trim())) {
					list.add(series);
					break;
				}
			}
		}
		return list;
	}
	
	private ErrorDevice getErrorDevice(String device, List<ErrorDevice> list) {
		ErrorDevice err = null;
		
		for (ErrorDevice item : list) {
			if (device.equals(item.getName())) {
				err = item;
				break;
			}
		}
		return err;
	}
}
