package com.mitec.business.dto;
import java.util.List;

import lombok.Data;
@Data
public class StatisticalContracDto {
	private Long id;
	private Integer countRegion;
	private Integer countDepartment;
	private String contract;
	private List<StatisticalRegionDto> statisticalRegion;
}
