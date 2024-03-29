package com.clickhealth.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.clickhealth.entity.Cita;
import com.clickhealth.entity.Enfermero;
import com.clickhealth.entity.Medico;
import com.clickhealth.entity.Usuario;

public interface CitaRepositorio extends JpaRepository<Cita, Long> {

	@Query("SELECT c FROM Cita c WHERE c.asistencia IS NULL")
	List<Cita> findCitasSinAsistencia();

	List<Cita> findByMedicoAndFechaAndAsistenciaIsNullAndConfirmadaIsTrue(Medico medico, Date fecha);

	List<Cita> findByEnfermeroAndFechaAndAsistenciaIsNullAndConfirmadaIsTrue(Enfermero enfermero, Date fecha);

	Cita findByUsuarioAndAsistenciaIsNull(Usuario usuario);

	List<Cita> findByFechaAndConfirmadaIsFalse(LocalDate fecha);
	
    int countByUsuarioAndAsistenciaIsNotNull(Usuario usuario);
    
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.usuario = :usuario AND c.asistencia = 'PRESENTADO'")
    int contarCitasConfirmadasUsuario(Usuario usuario);

}
