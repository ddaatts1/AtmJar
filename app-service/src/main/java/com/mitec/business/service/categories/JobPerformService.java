package com.mitec.business.service.categories;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mitec.business.dto.JobPerformDto;
import com.mitec.business.model.JobPerform;
import com.mitec.business.repository.JobPerformRepository;
import com.mitec.business.specification.ObjectSpecification;
import com.mitec.business.utils.ClassMapper;

@Service
public class JobPerformService {

	@Autowired
	private JobPerformRepository jobPerformRepository;
	@Autowired
	private ObjectSpecification objectSpecification;
	@Autowired
	private ClassMapper classMapper;
	
	public List<JobPerformDto> getJobPerforms() {
		List<JobPerform> jobPerforms = jobPerformRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
		if (!jobPerforms.isEmpty()) {
			return jobPerforms.stream().map(item -> classMapper.convertToJobPerformDto(item)).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
	
	public Page<JobPerform> listAllJobPerform(int pageNumber, int size) {
		Specification<JobPerform> spec = objectSpecification.searchJobPerform();
		Page<JobPerform> page = jobPerformRepository.findAll(spec, PageRequest.of(pageNumber, size));
		List<JobPerform> regions = page.getContent();
		List<JobPerform> list = regions.stream().map(job -> {
			return job;
		}).collect(Collectors.toList());
		
		return new PageImpl<>(list, PageRequest.of(pageNumber, size), page.getTotalElements());
	}
	
	public JobPerform saveJobPerform(String body) throws JSONException {
		JobPerform jobPerform = new JobPerform();
		JSONObject ob = new JSONObject(body);
		jobPerform.setName(ob.getString("name"));
		
		return jobPerformRepository.save(jobPerform);
	}
	
	public void deleteJobPerform(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		jobPerformRepository.deleteById(id);
	}
	
	public JobPerformDto getJobPerform(String body) throws JSONException{
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		JobPerform acc = jobPerformRepository.getById(id);
		return classMapper.convertToJobPerformDto(acc);
	}
	
	public JobPerform updateJobPerform(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		JobPerform jobPerform = jobPerformRepository.getById(id);
		jobPerform.setName(ob.getString("name"));
		
		return jobPerformRepository.save(jobPerform);
	}
}
