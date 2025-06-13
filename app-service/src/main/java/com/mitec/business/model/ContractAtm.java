package com.mitec.business.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "contract_atm")
public class ContractAtm {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne()
	@JoinColumn(name = "serial_number")
	private ATM atm;
	
	@ManyToOne
	@JoinColumn(name = "contract_id")
	private Contract contract;
	
	@Column(name = "order_number")
	private Long orderNumber;
	
	@PrePersist
	public void prePersist() {
		orderNumber = 1L;
	}
}
