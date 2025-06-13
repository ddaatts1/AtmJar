package com.mitec.business.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "device")
public class Device {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@OneToMany(mappedBy = "device")
	private List<Error> errors;
	
	@Override
	public boolean equals(Object obj) {
		Device device = (Device) obj;
		return name.equals(device.getName());
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
