package com.mitec.business.service.categories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mitec.business.model.Bank;
import com.mitec.business.repository.BankRepository;
import com.mitec.business.specification.ObjectSpecification;

@Service
public class BankService {
	
	@Autowired
	private BankRepository bankRepository;
	@Autowired
	private ObjectSpecification objectSpecification;
	
	public Bank saveBank(String body) throws JSONException {
		Bank bank = new Bank();
		JSONObject ob = new JSONObject(body);
		bank.setName(ob.getString("name"));
		return bankRepository.save(bank);
	}
	
	public List<Bank> listAll() {
		return bankRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}
	
	public Page<Bank> listAllBank(int pageNumber, int size) {
		Specification<Bank> spec = objectSpecification.searchBank();
		return bankRepository.findAll(spec, PageRequest.of(pageNumber, size));
	}
	
	public void deleteBank(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		bankRepository.deleteById(id);
	}

	public Bank getBank(String body) throws JSONException{
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		return bankRepository.getById(id);
	}
	public Bank updateBank(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		Bank bank = bankRepository.getById(id);
		bank.setName(ob.getString("name"));
		return bankRepository.save(bank);
	}
}
