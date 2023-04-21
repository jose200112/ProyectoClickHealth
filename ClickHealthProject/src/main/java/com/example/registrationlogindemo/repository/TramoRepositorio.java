package com.example.registrationlogindemo.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.registrationlogindemo.entity.Tramo;


public interface TramoRepositorio extends JpaRepository<Tramo, Long> {
	List<Tramo> findByTiempoGreaterThanEqualAndTiempoLessThanEqual(LocalTime comienza,LocalTime termina);
}
