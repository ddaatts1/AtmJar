package com.mitec.business.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
@Entity
@Table(name = "contract")
public class Contract {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "start_time")
	private LocalDateTime startTime; 

	@Column(name = "end_time")
	private LocalDateTime endTime;
	
	@ManyToOne
	@JoinColumn(name = "bank_id")
	private Bank bank;
	
	@Column(name = "status")
	private Integer status;
	
	@Column(name = "maintenance_cycle")
	private Integer maintenanceCycle;
	
	@OneToMany(mappedBy = "contract", cascade = CascadeType.REMOVE)
	private List<Period> periods;
	
	@ManyToMany()
	@JoinTable(name = "contract_atm",
			joinColumns = @JoinColumn(name = "contract_id"),
			inverseJoinColumns = @JoinColumn(name = "serial_number"))
	private List<ATM> atms;
	
	@OneToMany(mappedBy = "contract", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<CustomerEmail> customerEmails;
	
	@Column(name = "type")
	private Integer type;
	
	@OneToMany(mappedBy = "contract")
	private List<ContractAtm> contractAtms;

	@OneToMany(mappedBy = "contract", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReportPDF> reports;

	@Override
	public String toString() {
		return "Contract [id=" + id + ", name=" + name + ", startTime=" + startTime + ", endTime=" + endTime + ", bank="
				+ bank + ", status=" + status + ", maintenanceCycle=" + maintenanceCycle + ", type=" + type + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		Contract contract = (Contract) obj;
		
		return name.equals(contract.getName());
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@PrePersist
	public void prePersist() {
		status = 1;
	}
}
