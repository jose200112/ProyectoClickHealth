package com.example.registrationlogindemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.registrationlogindemo.entity.Enfermero;
import com.example.registrationlogindemo.entity.Medico;
import com.example.registrationlogindemo.entity.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
	Usuario findByDni(String dni);

	@Query("SELECT u FROM Usuario u WHERE u.dni LIKE %?1%")
	List<Usuario> buscarUsuariosDni(String dni);

	@Query("SELECT u FROM Usuario u WHERE CONCAT(u.apellidos, ' ', u.nombre) LIKE %?1%")
	List<Usuario> buscarPorNombreCompleto(String nombreCompleto);

	@Query("SELECT u FROM Usuario u JOIN u.enfermero e WHERE CONCAT(u.apellidos, ' ', u.nombre) LIKE %?1% AND e = ?2")
	List<Usuario> buscarPorNombreCompletoEnfermero(String nombreCompleto, Enfermero enfermero);

	@Query("SELECT u FROM Usuario u JOIN u.medico e WHERE CONCAT(u.apellidos, ' ', u.nombre) LIKE %?1% AND e = ?2")
	List<Usuario> buscarPorNombreCompletoMedico(String nombreCompleto, Medico medico);

}
