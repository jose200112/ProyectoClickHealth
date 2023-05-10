package com.example.registrationlogindemo.service;

import java.util.List;

import com.example.registrationlogindemo.dto.AlergiaDto;
import com.example.registrationlogindemo.dto.EnfermeroDto;
import com.example.registrationlogindemo.dto.VacunaDto;
import com.example.registrationlogindemo.entity.Alergia;
import com.example.registrationlogindemo.entity.Cita;
import com.example.registrationlogindemo.entity.Enfermero;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;

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
