package com.mitec.business.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.dto.UserDto;
import com.mitec.business.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	public User findByUsernameAndDeviceId(String username, String deviceId);
	public List<User> findByUsernameOrDeviceId(String username, String deviceId);
	public User findByUsername(String username);
	public Page<User> findAll(Specification<User> spec, Pageable pageable);
	
	@Transactional
	public void deleteByUsername(String username);
	
	@Query(value = "select u.* from user as u "
			+ "join user_role as ur on u.id = ur.user_id "
			+ "join role as r on ur.role_id = r.id "
			+ "where r.name = 'ROLE_DEPLOYMENT'", nativeQuery = true)
	public List<User> getUserWithRoleDeployment();
	
	@Query(value = "select u from User u where u.username != 'admin'")
	public List<User> getAllNonAdminUser();
	
//	@Query(value = "select new com.mitec.business.dto.UserDto(distinct ui.user_id, u.username) from user_inventory ui join user u \r\n"
	@Query(value = "select distinct ui.user_id, u.username from user_inventory ui join user u \r\n"
			+ "on ui.user_id = u.id\r\n"
			+ "group by ui.user_id, u.username", nativeQuery = true)
	public List<Object[]> getUserForTracking();
	
	@Query(value = "select case when count(u) > 0 then true else false end from User u join u.roles r where u.username = :username and r.name = :roleName")
	public boolean existsRoleInUser(String username, String roleName);
	
	@Query(value = "select case when count(u) > 0 then true else false end from User u join u.roles r where u.username = :username and r.name = 'ROLE_ADMIN'")
	public boolean isAdmin(String username);

	@Query(value = "select new com.mitec.business.dto.UserDto(u.id, u.username, u.fullName) from User u")
	public List<UserDto> getAll();
	
	@Query(value = "select new com.mitec.business.dto.UserDto(u.id, u.username, u.deviceId, u.isActived, u.fullName, GROUP_CONCAT(r.desc), u.phoneNumber) \r\n"
			+ "from User u \r\n"
			+ "join u.roles r \r\n"
			+ "where :username is null or u.username like %:username% \r\n"
			+ "group by u.id")
	public List<UserDto> getPages(String username);
}
