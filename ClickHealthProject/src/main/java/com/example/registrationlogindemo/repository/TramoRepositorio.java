package com.example.registrationlogindemo.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.registrationlogindemo.entity.Tramo;


public interface TramoRepositorio extends JpaRepository<Tramo, Long> {
	List<Tramo> findByTiempoGreaterThanEqualAndTiempoLessThanEqual(LocalTime comienza,LocalTime termina);

	@Query("SELECT t FROM Tramo t " +
		       "JOIN t.tramosHorario th " +
		       "JOIN th.horario h " +
		       "WHERE h.medico.id = :idMedico " +
		       "AND NOT EXISTS (SELECT c FROM Cita c WHERE c.tramo = t)")
		List<Tramo> findTramosDisponibles(@Param("idMedico") Long idMedico);



}
