package com.mitec.business.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "customer_email")
public class CustomerEmail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "email")
	private String email;
	
	@Column(name = "type")
	private Integer type;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "contract_id")
	private Contract contract;
}
