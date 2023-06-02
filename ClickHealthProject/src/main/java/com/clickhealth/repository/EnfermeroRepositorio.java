package com.clickhealth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.clickhealth.entity.Enfermero;
import com.clickhealth.entity.Usuario;

public interface EnfermeroRepositorio extends JpaRepository<Enfermero, Long> {
	@Query("SELECT e " + "FROM Enfermero e " + "LEFT JOIN e.usuarios u " + "GROUP BY e.id, e.nombre "
			+ "ORDER BY COUNT(u.id) ASC " + "LIMIT 1")
	Enfermero findEnfermeroConMenosUsuarios();

	@Query("SELECT e FROM Enfermero e WHERE e.dni LIKE %?1%")
	List<Enfermero> buscarEnfermerosDni(String dni);

	Enfermero findByCodigo(String codigo);

	Enfermero findByDni(String sala);

	Enfermero findBySala(String sala);

	@Query("SELECT e FROM Enfermero e WHERE CONCAT(e.apellidos, ' ', e.nombre) LIKE %?1%")
	List<Enfermero> buscarPorNombreCompleto(String nombreCompleto);

	@Query("SELECT e " + "FROM Enfermero e " + "LEFT JOIN e.usuarios u " + "WHERE e.id <> :enfermeroId "
			+ "GROUP BY e.id, e.nombre " + "ORDER BY COUNT(u.id) ASC " + "LIMIT 1")
	Enfermero EnfermeroConMenosUsuariosExcluye(@Param("enfermeroId") Long enfermeroId);

	@Query("SELECT e FROM Enfermero e WHERE CONCAT(e.apellidos, ' ', e.nombre) LIKE %?1% AND e.id <> ?2")
	List<Enfermero> buscarPorNombreCompletoId(String nombreCompleto, Long id);

}
