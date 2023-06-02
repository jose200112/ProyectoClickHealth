package com.clickhealth.service;

import java.util.List;

import com.clickhealth.dto.MedicoDto;
import com.clickhealth.dto.ObservacionDto;
import com.clickhealth.entity.Cita;
import com.clickhealth.entity.Medico;
import com.clickhealth.entity.User;

public interface MedicoServicioI {
	void saveMedico(MedicoDto medicoDto);

	User findByEmail(String email);

	User findByName(String name);

	public Medico buscaPorSala(String sala);

	public Medico buscaPorDni(String dni);

	public void borraMedico(Long id);

	public List<Medico> buscarMedicosPorNombre(String nombre);

	void confirmaAsistencia(Cita cita);

	void deniegaAsistencia(Cita cita);

	void actualizaMedico(Medico medico, MedicoDto medicoDto);

	List<Medico> buscarMedicosPorNombreId(String nombre, Long id);

	void guardaNuevaObservacion(ObservacionDto observacionDto);

	void guardaObservacion(ObservacionDto observacionDto);

}
