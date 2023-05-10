package com.example.registrationlogindemo.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.registrationlogindemo.entity.Tramo;


public interface TramoRepositorio extends JpaRepository<Tramo, Long> {
	List<Tramo> findByTiempoGreaterThanEqualAndTiempoLessThan(LocalTime comienza,LocalTime termina);



	@Query("SELECT t FROM Tramo t " +
		       "JOIN t.tramosHorario th " +
		       "JOIN th.horario h " +
		       "WHERE h.medico.id = :idMedico " +
		       "AND NOT EXISTS (SELECT c FROM Cita c WHERE c.tramo = t AND c.fecha = :fecha AND c.medico.id = :idMedico)")
		List<Tramo> findTramosDisponibles(@Param("idMedico") Long idMedico, @Param("fecha") LocalDate fecha);
	
	
	@Query("SELECT t FROM Tramo t " +
		       "JOIN t.tramosHorario th " +
		       "JOIN th.horario h " +
		       "WHERE h.enfermero.id = :idEnfermero " +
		       "AND NOT EXISTS (SELECT c FROM Cita c WHERE c.tramo = t AND c.fecha = :fecha AND c.enfermero.id = :idEnfermero)")
		List<Tramo> findTramosDisponiblesEnfermero(@Param("idEnfermero") Long idMedico, @Param("fecha") LocalDate fecha);
	
	
	Tramo findByTiempo(LocalTime hora);
}
