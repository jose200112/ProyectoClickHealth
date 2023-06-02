package com.clickhealth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clickhealth.entity.Alergia;
import com.clickhealth.entity.Usuario;

public interface AlergiaRepositorio extends JpaRepository<Alergia, Long> {
	List<Alergia> findByUsuario(Usuario usuario);

}
