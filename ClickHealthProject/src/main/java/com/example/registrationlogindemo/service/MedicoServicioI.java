package com.example.registrationlogindemo.service;

import java.util.List;

import com.example.registrationlogindemo.dto.MedicoDto;
import com.example.registrationlogindemo.dto.ObservacionDto;
import com.example.registrationlogindemo.entity.Cita;
import com.example.registrationlogindemo.entity.Medico;
import com.example.registrationlogindemo.entity.User;

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
