package com.clickhealth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clickhealth.entity.Solicitud;
import com.clickhealth.entity.Usuario;

@Repository
public interface SolicitudRepositorio extends JpaRepository<Solicitud, Long> {

	Solicitud findByUsuarioAndTituloAndEstadoIsNull(Usuario usuario, String titulo);

	List<Solicitud> findByEstadoIsNull();
}
