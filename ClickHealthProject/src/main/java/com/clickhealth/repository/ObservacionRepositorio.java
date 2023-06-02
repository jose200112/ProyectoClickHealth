package com.clickhealth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clickhealth.entity.Observacion;
import com.clickhealth.entity.Usuario;

public interface ObservacionRepositorio extends JpaRepository<Observacion, Long> {
	List<Observacion> findByUsuario(Usuario usuario);

}
