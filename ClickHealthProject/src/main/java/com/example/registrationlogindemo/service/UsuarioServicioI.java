package com.example.registrationlogindemo.service;

import java.util.List;

import com.example.registrationlogindemo.dto.BajaDto;
import com.example.registrationlogindemo.dto.SolicitudDto;
import com.example.registrationlogindemo.dto.UsuarioDto;
import com.example.registrationlogindemo.entity.Alergia;
import com.example.registrationlogindemo.entity.Enfermero;
import com.example.registrationlogindemo.entity.Medico;
import com.example.registrationlogindemo.entity.Observacion;
import com.example.registrationlogindemo.entity.Solicitud;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;
import com.example.registrationlogindemo.entity.Vacuna;

public interface UsuarioServicioI {
	void saveUsuario(UsuarioDto usuarioDto);

	User findByEmail(String email);

	User findByName(String name);

	public List<Usuario> buscarUsuarios(String nombre);

	public void borraUsuario(Long id);

	public void borraUsuarioForm(BajaDto baja, Usuario usuario);

	public Usuario buscaPorDni(String dni);

	public void guardaSolicitud(Usuario usuario, SolicitudDto solicitudDto, String seleccionado);

	public void guardaSolicitudEnfermero(Usuario usuario, SolicitudDto solicitudDto, String codigo,
			String seleccionado);

	public void guardaSolicitudMedico(Usuario usuario, SolicitudDto solicitudDto, String codigo, String seleccionado);

	void DeniegaSolicitud(Solicitud solicitud);

	void AceptaSolicitud(Solicitud solicitud);

	void actualizaUsuario(Usuario usuario, UsuarioDto usuarioDto);

	List<Usuario> buscarUsuariosEnfermero(String nombre, Enfermero enfermero);

	List<Vacuna> buscarVacunasUsuario(Usuario usuario);

	List<Alergia> buscarAlergiasUsuario(Usuario usuario);

	List<Usuario> buscarUsuariosMedico(String nombre, Medico medico);

	List<Observacion> buscarObservacionesUsuario(Usuario usuario);

}
