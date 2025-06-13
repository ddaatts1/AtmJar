package com.mitec.business.service.categories;

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

import com.mitec.business.model.Region;
import com.mitec.business.repository.RegionRepository;
import com.mitec.business.specification.ObjectSpecification;

@Service
public class RegionService {

	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private ObjectSpecification objectSpecification;

	public List<Region> getRegions() {
		return regionRepository.findAll();
	}
	
	public Region saveRegion(String body) throws JSONException {
		Region region = new Region();
		JSONObject ob = new JSONObject(body);
		region.setName(ob.getString("name"));
		return regionRepository.save(region);
	}
	
	public List<Region> listRegion() {
		return regionRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	public Page<Region> listAllRegion(int pageNumber, int size) {

		Specification<Region> spec = objectSpecification.searchRegion();
		Page<Region> page = regionRepository.findAll(spec, PageRequest.of(pageNumber, size));
		List<Region> regions = page.getContent();
		List<Region> list = regions.stream().map(region -> {
			return region;
		}).collect(Collectors.toList());
		
		return new PageImpl<>(list, PageRequest.of(pageNumber, size), page.getTotalElements());
	}
	
	public void deleteRegion(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		regionRepository.deleteById(id);
	}
	
	public Region getRegion(String body) throws JSONException{
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		return regionRepository.getById(id);
	}
	
	public Region updateRegion(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		Region region = regionRepository.getById(id);
		region.setName(ob.getString("name"));
		return regionRepository.save(region);
	}
}
