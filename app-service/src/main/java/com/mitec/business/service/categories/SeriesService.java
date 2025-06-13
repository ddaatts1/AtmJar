package com.mitec.business.service.categories;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mitec.business.dto.SeriesDto;
import com.mitec.business.model.Series;
import com.mitec.business.repository.SeriesRepository;
import com.mitec.business.specification.ObjectSpecification;

@Service
public class SeriesService {

	@Autowired
	private SeriesRepository seriesRepository;
	@Autowired
	private ObjectSpecification objectSpecification;
	
	public Series saveSeries(String body) throws JSONException {
		Series series = new Series();
		JSONObject ob = new JSONObject(body);
		series.setName(ob.getString("name"));
		return seriesRepository.save(series);
	}
	
	public List<SeriesDto> listSeries() {
		return seriesRepository.findAllDto();
	}
	
	public Page<Series> listAllSeries(int pageNumber, int size) {
		Specification<Series> spec = objectSpecification.searchSeries();
		Page<Series> page = seriesRepository.findAll(spec, PageRequest.of(pageNumber, size));
		List<Series> regions = page.getContent();
		List<Series> list = regions.stream().map(series -> {
			return series;
		}).collect(Collectors.toList());
		
		return new PageImpl<>(list, PageRequest.of(pageNumber, size), page.getTotalElements());
	}
	
	public void deleteSeries(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		seriesRepository.deleteById(id);
	}
	
	public Series getSeries(String body) throws JSONException{
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		return seriesRepository.getById(id);
	}
	
	public Series updateSeries(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		Series series = seriesRepository.getById(id);
		series.setName(ob.getString("name"));
		return seriesRepository.save(series);
	}
}
