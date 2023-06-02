package com.clickhealth.service;

import java.util.List;

import com.clickhealth.dto.BajaDto;
import com.clickhealth.dto.SolicitudDto;
import com.clickhealth.dto.UsuarioDto;
import com.clickhealth.entity.Alergia;
import com.clickhealth.entity.Enfermero;
import com.clickhealth.entity.Medico;
import com.clickhealth.entity.Observacion;
import com.clickhealth.entity.Solicitud;
import com.clickhealth.entity.User;
import com.clickhealth.entity.Usuario;
import com.clickhealth.entity.Vacuna;

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
