package com.mitec.business.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.dto.SeriesDto;
import com.mitec.business.model.Series;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long>{
	
	@Query(value = "select a from Series a where a.id IN :ids")
	public List<Series> getByListId(List<Long> ids);
	
	@Query(value = "select s.* from series s where s.name = :name limit 1", nativeQuery = true)
	public Series getByNameLimit1(String name);
	
	public Page<Series> findAll(Specification<Series> spec, Pageable pageable);
	
	@Query(value = "select new com.mitec.business.dto.SeriesDto(s.id, s.name) from Series s order by s.id desc")
	public List<SeriesDto> findAllDto();
}
