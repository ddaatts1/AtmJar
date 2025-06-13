package com.mitec.business.model;

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
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "username", unique = true, nullable = false)
	private String username;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	@Column(name = "device_id", unique = true, nullable = true)
	private String deviceId;
	
	@Column(name = "is_actived", nullable = false)
	private boolean isActived;
	
	@Column(name = "full_name", nullable = true)
	private String fullName;
	
	@Column(name = "email")
	private String email;
	
	@ManyToOne()
	@JoinColumn(name = "department_id")
	private Department department;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "user_role",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Job> jobs;
	
	@Transient
	private String newPwd;
	
	@Transient
	private List<Long> roleIds; 

	@Override
	public String toString() {
		return "AppUser [id=" + id + ", username=" + username + ", password=" + password + ", deviceId=" + deviceId
				+ ", isActive=" + isActived + "]";
	}
	
	@PrePersist
	public void prePersist() {
		isActived = true;
		deviceId = null;
	}
}
