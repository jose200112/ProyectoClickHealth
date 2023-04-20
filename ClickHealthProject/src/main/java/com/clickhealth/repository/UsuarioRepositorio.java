package com.clickhealth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clickhealth.entity.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
	Usuario findByDni(String Dni);
}
