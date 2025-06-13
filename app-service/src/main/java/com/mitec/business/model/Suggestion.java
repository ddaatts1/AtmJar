package com.mitec.business.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "suggestion")
public class Suggestion {

	@JsonIgnoreProperties
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonProperty
	@Column(name = "name")
	private String name;

	@JsonProperty
	@Column(name = "phone")
	private String phone;

	@JsonProperty
	@Column(name = "department")
	private String department;

	@JsonProperty
	@Column(name = "address")
	private String address;
}
