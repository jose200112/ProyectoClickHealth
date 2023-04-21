package com.example.registrationlogindemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.registrationlogindemo.entity.Usuario;


public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
	Usuario findByDni(String Dni);
}
