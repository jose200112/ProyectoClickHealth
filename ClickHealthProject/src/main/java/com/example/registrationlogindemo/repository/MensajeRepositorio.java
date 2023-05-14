package com.example.registrationlogindemo.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.registrationlogindemo.entity.Mensaje;
import com.example.registrationlogindemo.entity.Usuario;

public interface MensajeRepositorio extends JpaRepository<Mensaje, Long>  {
	
	    @Query("SELECT m FROM Mensaje m WHERE m.fecha >= :fechaDesde AND m.fecha <= :fechaHasta AND m.usuario = :usuario")
	    List<Mensaje> findMensajesRecientes(@Param("fechaDesde") Date fechaDesde, @Param("fechaHasta") Date fechaHasta, @Param("usuario") Usuario usuario);
	

}
