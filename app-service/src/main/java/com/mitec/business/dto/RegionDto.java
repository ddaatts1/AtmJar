package com.mitec.business.dto;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionDto {
	private Long id;
	private String name;
	private Long totalSubItem;
	private Long countAtm;
	
	public RegionDto() {
		super();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegionDto other = (RegionDto) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "RegionDto [id=" + id + ", name=" + name + ", totalSubItem=" + totalSubItem + ", countAtm=" + countAtm
				+ "]";
	}
	
}
