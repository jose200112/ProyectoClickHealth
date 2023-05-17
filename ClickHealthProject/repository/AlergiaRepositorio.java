package com.example.registrationlogindemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.registrationlogindemo.entity.Alergia;
import com.example.registrationlogindemo.entity.Usuario;


public interface AlergiaRepositorio extends JpaRepository<Alergia,Long> {
	List<Alergia> findByUsuario(Usuario usuario);

}
