package com.mitec.business.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "role")
public class Role {

	@Id
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "desc")
	private String desc;
	
	@ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
	private List<User> appUsers;
}
