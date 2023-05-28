package com.example.registrationlogindemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.registrationlogindemo.entity.Observacion;
import com.example.registrationlogindemo.entity.Usuario;

public interface ObservacionRepositorio extends JpaRepository<Observacion, Long> {
	List<Observacion> findByUsuario(Usuario usuario);

}
