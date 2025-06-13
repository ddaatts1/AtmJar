package com.mitec.business.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mitec.business.utils.AtmStatusEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table(name = "atm")
public class ATM {

	@Getter@Setter
	@Id
	@Column(name = "serial_number")
	private String serialNumber;
	
	@Getter@Setter
	@Column(name = "atm_id", nullable = true)
	private String atmId;
	
	@Getter@Setter
	@Column(name = "address")
	private String address;
	
	@Getter@Setter
	@ManyToOne()
	@JoinColumn(name = "series_id")
	private Series series;
	
	@Getter@Setter
	@ManyToMany()
	@JoinTable(name = "contract_atm",
			joinColumns = @JoinColumn(name = "serial_number"),
			inverseJoinColumns = @JoinColumn(name = "contract_id"))
	private List<Contract> contracts;
	
	@Getter@Setter
	@ManyToOne
	@JoinColumn(name = "region_id")
	private Region region;
	
	@Getter@Setter
	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;
	
	@Getter@Setter
	@Column(name = "status")
	private Integer status;
	
	@Setter
	@Transient
	private String statusDesc;
	
	@JsonBackReference(value = "atm-job")
	@Getter@Setter
	@OneToMany(mappedBy = "atm", cascade = CascadeType.REMOVE)
	private List<Job> jobs;
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			try {
				ATM atm = (ATM) obj;
				return serialNumber.equals(atm.getSerialNumber());
			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return serialNumber.hashCode();
	}

	@Override
	public String toString() {
		return "ATM [serialNumber=" + serialNumber + ", atmId=" + atmId + ", address=" + address + ", status=" + status
				+ "]";
	}

	public String getStatusDesc() {
		if (status != null)
			return AtmStatusEnum.fromKey(status).getValue();
		return null;
	}
}
