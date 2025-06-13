package com.mitec.business.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.Suggestion;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
	
	@Query(value = "select * from suggestion s where s.name = :name and s.phone = :phone limit 1", nativeQuery = true)
	public Optional<Suggestion> findByNameAndPhone(String name, String phone);
}
