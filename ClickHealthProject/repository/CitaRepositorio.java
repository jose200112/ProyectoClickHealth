package com.example.registrationlogindemo.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.registrationlogindemo.entity.Cita;
import com.example.registrationlogindemo.entity.Enfermero;
import com.example.registrationlogindemo.entity.Medico;
import com.example.registrationlogindemo.entity.Usuario;

public interface CitaRepositorio extends JpaRepository<Cita,Long> {
	
    @Query("SELECT c FROM Cita c WHERE c.asistencia IS NULL")
    List<Cita> findCitasSinAsistencia();
    
    List<Cita> findByMedicoAndFechaAndAsistenciaIsNullAndConfirmadaIsTrue(Medico medico,Date fecha);
    
    
    List<Cita> findByEnfermeroAndFechaAndAsistenciaIsNullAndConfirmadaIsTrue(Enfermero enfermero,Date fecha);
    
    Cita findByUsuarioAndAsistenciaIsNull(Usuario usuario);

}
