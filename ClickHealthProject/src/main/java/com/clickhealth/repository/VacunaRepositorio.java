package com.clickhealth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clickhealth.entity.Usuario;
import com.clickhealth.entity.Vacuna;

public interface VacunaRepositorio extends JpaRepository<Vacuna, Long> {

	List<Vacuna> findByUsuario(Usuario usuario);
}
