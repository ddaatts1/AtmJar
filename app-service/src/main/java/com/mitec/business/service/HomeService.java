package com.mitec.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mitec.business.dto.HomeDataDto;
import com.mitec.business.model.Contract;
import com.mitec.business.model.CustomerEmail;
import com.mitec.business.model.EmailConfig;
import com.mitec.business.repository.ContractRepository;
import com.mitec.business.repository.CustomerEmailRepository;
import com.mitec.business.repository.EmailConfigRepository;
import com.mitec.business.repository.StatisticalRepository;
import com.mitec.business.utils.ContractTypeEnum;
import com.mitec.business.utils.EmailTypeEnum;

@Service
public class HomeService {
	
	@Autowired
	private StatisticalRepository statisticalRepository;
	@Autowired
	private ContractRepository contractRepository;
	@Autowired
	private EmailConfigRepository emailConfigRepository;
	@Autowired
	private CustomerEmailRepository customerEmailRepository;
	
	public List<Contract> getContracts() {
		return contractRepository.findContractIsActive(ContractTypeEnum.HOP_DONG_LE.getKey());
	}
	
	public Page<HomeDataDto> getHomeData(String address, Long contractId, boolean noMaintenanceYet, boolean notClosedYet, boolean repeatError, int pageNumber, int pageSize) {
		List<Object[]> objectDatas = statisticalRepository.getHomeData(address);
		List<HomeDataDto> list = new ArrayList<>();
		if (objectDatas != null && !objectDatas.isEmpty()) {
			list = objectDatas.stream().map(item -> {
				// item 5 null -> is_mantenance = false -> 0
				return new HomeDataDto(
						item[0] != null ? Long.valueOf(item[0].toString()) : null, 
						String.valueOf(item[1]), Integer.valueOf(item[2].toString()), 
						String.valueOf(item[3]), String.valueOf(item[4]), 
						item[5] != null ? Integer.valueOf(item[5].toString()) : 0, String.valueOf(item[6]), 
						item[7] != null ? Long.valueOf(item[7].toString()) : null,
						item[8] != null ? String.valueOf(item[8]) : null);
			}).collect(Collectors.toList());

			if (contractId != null) {
				if (contractId > 0) {
					list = list.stream().filter(item -> item.getContractId().equals(contractId)).collect(Collectors.toList());
				}else {
					list = list.stream().filter(item -> item.getType().equals(ContractTypeEnum.HOP_DONG_LE.getKey())).collect(Collectors.toList());
				}
			}
			if (noMaintenanceYet) {
				list = list.stream().filter(item -> !item.isTinhTrangBaoTri()).collect(Collectors.toList());
			}
			if (notClosedYet) {
				list = list.stream().filter(item -> StringUtils.isNotBlank(item.getTrangThai())).collect(Collectors.toList());
			}
			if (repeatError) {
				list = list.stream().filter(item -> StringUtils.isNotBlank(item.getTinhTrangService())).collect(Collectors.toList());
			}
		}
		
		if (!list.isEmpty()) {
			int firstIndex = pageNumber*pageSize;
			int lastIndex = (pageNumber + 1)*pageSize >= list.size() ? (list.size()) : (pageNumber + 1)*pageSize;
			List<HomeDataDto> subList = new ArrayList<>();
			if (pageNumber <= (list.size() / pageSize)) {
				subList = list.subList(firstIndex, lastIndex);
			}
			return new PageImpl<>(subList, PageRequest.of(pageNumber, pageSize), list.size());
		}else {
			return new PageImpl<>(new ArrayList<>(), PageRequest.of(pageNumber, pageSize), 0);
		}
	}
	
	public EmailConfig getEmailConfig() {
		return emailConfigRepository.findFirstOrderById();
	}
	
	public EmailConfig saveEmailConfig(String body) throws JSONException {
		EmailConfig email = new EmailConfig();
		JSONObject ob = new JSONObject(body);
		email.setHost(ob.getString("host"));
		email.setPort(Integer.parseInt(ob.getString("port")));
		email.setProtocol(ob.getString("protocol"));
		email.setUsername(ob.getString("username"));
		email.setPassword(ob.getString("password"));
		
		String internalEmail = ob.getString("internalEmail");
		CustomerEmail customerEmail = customerEmailRepository.getInternalEmail().orElse(new CustomerEmail());
		customerEmail.setEmail(internalEmail);
		customerEmail.setType(EmailTypeEnum.INTERNAL.getKey());
		
		customerEmailRepository.save(customerEmail);
		
		return emailConfigRepository.save(email);
	}
	
	public EmailConfig updateEmailConfig(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		EmailConfig email = emailConfigRepository.getById(Long.parseLong(ob.getString("id")));
		email.setHost(ob.getString("host"));
		email.setPort(Integer.parseInt(ob.getString("port")));
		email.setProtocol(ob.getString("protocol"));
		email.setUsername(ob.getString("username"));
		email.setPassword(ob.getString("password"));
		
		String internalEmail = ob.getString("internalEmail");
		CustomerEmail customerEmail = customerEmailRepository.getInternalEmail().orElse(new CustomerEmail());
		customerEmail.setEmail(internalEmail);
		customerEmail.setType(EmailTypeEnum.INTERNAL.getKey());
		
		customerEmailRepository.save(customerEmail);
		
		return emailConfigRepository.save(email);
	}
	
	public CustomerEmail getInternalEmail() {
		return customerEmailRepository.getInternalEmail().orElse(new CustomerEmail());
	}
}
