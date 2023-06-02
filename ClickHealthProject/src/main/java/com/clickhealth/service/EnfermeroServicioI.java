package com.clickhealth.service;

import java.util.List;

import com.clickhealth.dto.AlergiaDto;
import com.clickhealth.dto.EnfermeroDto;
import com.clickhealth.dto.VacunaDto;
import com.clickhealth.entity.Alergia;
import com.clickhealth.entity.Cita;
import com.clickhealth.entity.Enfermero;
import com.clickhealth.entity.User;
import com.clickhealth.entity.Usuario;

public interface EnfermeroServicioI {
	void saveUser(EnfermeroDto enfermeroDto);

	User findByEmail(String email);

	User findByName(String name);

	void saveVacuna(VacunaDto vacunaDto);

	public void borraEnfermero(Long id);

	public List<Enfermero> buscarEnfermeros(String dni);

	public Enfermero buscaPorSala(String sala);

	public Enfermero buscaPorDni(String dni);

	public List<Enfermero> buscarEnfermerosPorNombre(String nombre);

	void deniegaAsistencia(Cita cita);

	void confirmaAsistencia(Cita cita);

	void actualizaEnfermero(Enfermero enfermero, EnfermeroDto enfermeroDto);

	void actualizaVacuna(VacunaDto vacunaDto);

	void guardaAlergia(AlergiaDto alergiaDto);

	void guardaNuevaAlergia(AlergiaDto alergiaDto);

	List<Enfermero> buscarEnfermerosPorNombreId(String nombre, Long id);

}
