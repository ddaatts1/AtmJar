package com.mitec.business.service.categories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitec.business.repository.ContractRepository;
import com.mitec.business.repository.HistoryRepository;

@Service
public class HistoryService {
	
	@Autowired
	private ContractRepository contractRepository;
	
	@Autowired
	private HistoryRepository historyRepository;
}
