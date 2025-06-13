package com.mitec.business.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeDataDto {
	private Long contractId;
	private String name;
	private Integer type;
	private String serialNumber;
	private String address;
	private boolean tinhTrangBaoTri;
	private String trangThai;
	private Long soNgayKetThucCK;
	private String tinhTrangService;
	
	public HomeDataDto(Long contractId, String name, Integer type, String serialNumber, String address, Integer baoTri,
			String trangThai, Long soNgayKetThucCK, String tinhTrangService) {
		super();
		this.contractId = contractId;
		this.name = name;
		this.type = type;
		this.serialNumber = serialNumber;
		this.address = address;
		if (baoTri.equals(1)) {
			this.tinhTrangBaoTri = true;
		}else {
			this.tinhTrangBaoTri = false;
		}
		
		this.trangThai = trangThai;
		this.soNgayKetThucCK = soNgayKetThucCK;
		this.tinhTrangService = tinhTrangService;
	}

	public HomeDataDto() {
		super();
	}

	@Override
	public String toString() {
		return "HomeDataDto [contractId=" + contractId + ", name=" + name + ", serialNumber=" + serialNumber
				+ ", address=" + address + ", tinhTrangBaoTri=" + tinhTrangBaoTri + ", trangThai=" + trangThai
				+ ", soNgayKetThucCK=" + soNgayKetThucCK + ", tinhTrangService=" + tinhTrangService + "]";
	}
}
