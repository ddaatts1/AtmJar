package com.mitec.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
}
