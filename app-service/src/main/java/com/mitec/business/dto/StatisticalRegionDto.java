
package com.mitec.business.dto;

import java.util.List;
import lombok.Data;

@Data
public class StatisticalRegionDto {
	private Long id;
	private String username;
	private Long contractId;
	private String region;
	private Integer countDepartment;

	private List<StatisticalDepartmeDto> statisticalDepartment;
}