package com.example.registrationlogindemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.registrationlogindemo.entity.Usuario;
import com.example.registrationlogindemo.entity.Vacuna;

public interface VacunaRepositorio extends JpaRepository<Vacuna, Long> {

	List<Vacuna> findByUsuario(Usuario usuario);
}
