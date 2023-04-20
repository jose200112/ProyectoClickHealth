package com.clickhealth.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clickhealth.entity.Tramo;

public interface TramoRepositorio extends JpaRepository<Tramo, Long> {
	List<Tramo> findByTiempoGreaterThanEqualAndTiempoLessThanEqual(LocalTime comienza,LocalTime termina);
}
