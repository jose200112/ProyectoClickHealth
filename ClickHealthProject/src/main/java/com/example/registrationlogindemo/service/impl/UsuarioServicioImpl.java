package com.example.registrationlogindemo.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.registrationlogindemo.dto.BajaDto;
import com.example.registrationlogindemo.dto.SolicitudDto;
import com.example.registrationlogindemo.dto.UsuarioDto;
import com.example.registrationlogindemo.entity.Alergia;
import com.example.registrationlogindemo.entity.Baja;
import com.example.registrationlogindemo.entity.Cita;
import com.example.registrationlogindemo.entity.Enfermero;
import com.example.registrationlogindemo.entity.Medico;
import com.example.registrationlogindemo.entity.Mensaje;
import com.example.registrationlogindemo.entity.Observacion;
import com.example.registrationlogindemo.entity.Role;
import com.example.registrationlogindemo.entity.Solicitud;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;
import com.example.registrationlogindemo.entity.Vacuna;
import com.example.registrationlogindemo.repository.AlergiaRepositorio;
import com.example.registrationlogindemo.repository.BajaRepositorio;
import com.example.registrationlogindemo.repository.CitaRepositorio;
import com.example.registrationlogindemo.repository.EnfermeroRepositorio;
import com.example.registrationlogindemo.repository.MedicoRepositorio;
import com.example.registrationlogindemo.repository.MensajeRepositorio;
import com.example.registrationlogindemo.repository.ObservacionRepositorio;
import com.example.registrationlogindemo.repository.RoleRepository;
import com.example.registrationlogindemo.repository.SolicitudRepositorio;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.repository.UsuarioRepositorio;
import com.example.registrationlogindemo.repository.VacunaRepositorio;
import com.example.registrationlogindemo.service.UsuarioServicioI;

@Service
public class UsuarioServicioImpl implements UsuarioServicioI {

	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;
	private EnfermeroRepositorio enfermeroRepo;
	private MedicoRepositorio medicoRepo;
	private UsuarioRepositorio usuarioRepo;
	private VacunaRepositorio vacunaRepo;
	private BajaRepositorio bajaRepo;
	private SolicitudRepositorio solicitudRepo;
	private MensajeRepositorio mensajeRepo;
	private CitaRepositorio citaRepo;
	private AlergiaRepositorio alergiaRepo;
	private ObservacionRepositorio observacionRepo;

	public UsuarioServicioImpl(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder, EnfermeroRepositorio enfermeroRepo, MedicoRepositorio medicoRepo,
			UsuarioRepositorio usuarioRepo, VacunaRepositorio vacunaRepo, BajaRepositorio bajaRepo,
			SolicitudRepositorio solicitudRepo, MensajeRepositorio mensajeRepo, CitaRepositorio citaRepo,
			AlergiaRepositorio alergiaRepo, ObservacionRepositorio observacionRepo) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.enfermeroRepo = enfermeroRepo;
		this.medicoRepo = medicoRepo;
		this.usuarioRepo = usuarioRepo;
		this.vacunaRepo = vacunaRepo;
		this.bajaRepo = bajaRepo;
		this.solicitudRepo = solicitudRepo;
		this.mensajeRepo = mensajeRepo;
		this.citaRepo = citaRepo;
		this.alergiaRepo = alergiaRepo;
		this.observacionRepo = observacionRepo;
	}

	@Override
	public void saveUsuario(UsuarioDto usuarioDto) {

		Usuario usuario = new Usuario();
		usuario.setNombre(usuarioDto.getNombre());
		usuario.setApellidos(usuarioDto.getApellidos());
		usuario.setDni(usuarioDto.getDni());

		usuario.setEnfermero(enfermeroRepo.findEnfermeroConMenosUsuarios());
		usuario.setMedico(medicoRepo.findMedicoConMenosUsuarios());

		usuarioRepo.save(usuario);

		User user = new User();
		user.setName(usuarioDto.getName());
		user.setEmail(usuarioDto.getEmail());
		user.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));
		user.setUsuario(usuario);

		Role role = roleRepository.findByName("ROLE_USUARIO");
		user.setRoles(Arrays.asList(role));
		userRepository.save(user);

	}

	@Override
	public List<Usuario> buscarUsuarios(String nombre) {
		return usuarioRepo.buscarPorNombreCompleto(nombre);
	}

	@Override
	public List<Usuario> buscarUsuariosEnfermero(String nombre, Enfermero enfermero) {
		return usuarioRepo.buscarPorNombreCompletoEnfermero(nombre, enfermero);
	}

	@Override
	public List<Vacuna> buscarVacunasUsuario(Usuario usuario) {
		return vacunaRepo.findByUsuario(usuario);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User findByName(String name) {
		return userRepository.findByName(name);
	}

	@Override
	public void borraUsuario(Long id) {
		Optional<Usuario> usuario = usuarioRepo.findById(id);
		if (!usuario.isEmpty()) {
			Usuario usuarioEncontrado = usuario.get();
			userRepository.delete(usuarioEncontrado.getCuenta());

			for (Vacuna vacuna : usuarioEncontrado.getVacunas()) {
				vacunaRepo.delete(vacuna);
			}

			for (Solicitud solicitud : usuarioEncontrado.getSolicitud()) {
				solicitudRepo.delete(solicitud);
			}

			for (Cita cita : usuarioEncontrado.getCitas()) {
				citaRepo.delete(cita);
			}

			for (Mensaje mensaje : usuarioEncontrado.getMensajes()) {
				mensajeRepo.delete(mensaje);
			}

			for (Alergia alergia : usuarioEncontrado.getAlergias()) {
				alergiaRepo.delete(alergia);
			}

			for (Observacion observacion : usuarioEncontrado.getObservaciones()) {
				observacionRepo.delete(observacion);
			}

			usuarioRepo.deleteById(id);
		}
	}

	@Override
	public void borraUsuarioForm(BajaDto bajaDto, Usuario usuario) {
		Baja baja = new Baja();
		baja.setCausa(bajaDto.getCausa());
		baja.setCausa(bajaDto.getDescripcion());
		bajaRepo.save(baja);

		if (usuario != null) {
			userRepository.delete(usuario.getCuenta());
			for (Vacuna vacuna : usuario.getVacunas()) {
				vacunaRepo.delete(vacuna);
			}

			for (Solicitud solicitud : usuario.getSolicitud()) {
				solicitudRepo.delete(solicitud);
			}

			for (Cita cita : usuario.getCitas()) {
				citaRepo.delete(cita);
			}

			usuarioRepo.deleteById(usuario.getId());
		}

	}

	@Override
	public Usuario buscaPorDni(String dni) {
		return usuarioRepo.findByDni(dni);
	}

	@Override
	public void guardaSolicitudEnfermero(Usuario usuario, SolicitudDto solicitudDto, String codigoEnfermero,
			String seleccionado) {
		Solicitud solicitud = new Solicitud();
		solicitud.setTitulo(seleccionado);
		solicitud.setDescripcion(solicitudDto.getDescripcion());
		solicitud.setEnfermero(enfermeroRepo.findByCodigo(codigoEnfermero));
		solicitud.setUsuario(usuario);
		solicitud.setEstado(null);
		solicitudRepo.save(solicitud);
	}

	@Override
	public void guardaSolicitudMedico(Usuario usuario, SolicitudDto solicitudDto, String codigoMedico,
			String seleccionado) {
		Solicitud solicitud = new Solicitud();
		solicitud.setTitulo(seleccionado);
		solicitud.setDescripcion(solicitudDto.getDescripcion());
		solicitud.setMedico(medicoRepo.findByCodigo(codigoMedico));
		solicitud.setUsuario(usuario);
		solicitud.setEstado(null);
		solicitudRepo.save(solicitud);
	}

	@Override
	public void guardaSolicitud(Usuario usuario, SolicitudDto solicitudDto, String seleccionado) {
		Solicitud solicitud = new Solicitud();
		solicitud.setTitulo(seleccionado);
		solicitud.setDescripcion(solicitudDto.getDescripcion());
		solicitud.setUsuario(usuario);
		solicitud.setEstado(null);
		solicitudRepo.save(solicitud);
	}

	@Override
	public void AceptaSolicitud(Solicitud solicitud) {

		if (solicitud.getTitulo().equalsIgnoreCase("baja usuario")) {
			borraUsuario(solicitud.getUsuario().getId());
		}

		if (solicitud.getTitulo().equalsIgnoreCase("cambio medico")) {
			Usuario usuario = usuarioRepo.findByDni(solicitud.getUsuario().getDni());
			usuario.setMedico(solicitud.getMedico());
			usuarioRepo.save(usuario);

			for (Observacion observacion : usuario.getObservaciones()) {
				observacion.setMedico(solicitud.getMedico());
				observacionRepo.save(observacion);
			}

			Mensaje mensaje = new Mensaje();
			Calendar calendar = Calendar.getInstance();
			java.util.Date fechaActual = calendar.getTime();
			Date fecha = new Date(fechaActual.getTime());

			mensaje.setTitulo("Solicitud aceptada");
			mensaje.setDescripcion("Su solicitud de cambio de medico ha sido aceptada");
			mensaje.setFecha(fecha);
			mensaje.setUsuario(solicitud.getUsuario());

			mensajeRepo.save(mensaje);

			solicitud.setEstado("ACEPTADA");
			solicitudRepo.save(solicitud);
		}

		if (solicitud.getTitulo().equalsIgnoreCase("cambio enfermero")) {
			Usuario usuario = usuarioRepo.findByDni(solicitud.getUsuario().getDni());
			usuario.setEnfermero(solicitud.getEnfermero());
			usuarioRepo.save(usuario);

			for (Vacuna vacuna : usuario.getVacunas()) {
				vacuna.setEnfermero(solicitud.getEnfermero());
				vacunaRepo.save(vacuna);
			}

			for (Alergia alergia : usuario.getAlergias()) {
				alergia.setEnfermero(solicitud.getEnfermero());
				alergiaRepo.save(alergia);
			}

			Mensaje mensaje = new Mensaje();
			Calendar calendar = Calendar.getInstance();
			java.util.Date fechaActual = calendar.getTime();
			Date fecha = new Date(fechaActual.getTime());

			mensaje.setTitulo("Solicitud aceptada");
			mensaje.setDescripcion("Su solicitud de cambio de enfermero ha sido aceptada");
			mensaje.setFecha(fecha);
			mensaje.setUsuario(solicitud.getUsuario());

			mensajeRepo.save(mensaje);

			solicitud.setEstado("ACEPTADA");
			solicitudRepo.save(solicitud);
		}
	}

	@Override
	public void DeniegaSolicitud(Solicitud solicitud) {
		if (solicitud.getTitulo().equalsIgnoreCase("baja usuario")) {
			Mensaje mensaje = new Mensaje();
			Calendar calendar = Calendar.getInstance();
			java.util.Date fechaActual = calendar.getTime();
			Date fecha = new Date(fechaActual.getTime());

			mensaje.setTitulo("Solicitud denegada");
			mensaje.setDescripcion("Su solicitud de baja de usuario ha sido denegada");
			mensaje.setFecha(fecha);
			mensaje.setUsuario(solicitud.getUsuario());

			mensajeRepo.save(mensaje);

			solicitud.setEstado("DENEGADA");
			solicitudRepo.save(solicitud);
		}

		if (solicitud.getTitulo().equalsIgnoreCase("cambio medico")) {

			Mensaje mensaje = new Mensaje();
			Calendar calendar = Calendar.getInstance();
			java.util.Date fechaActual = calendar.getTime();
			Date fecha = new Date(fechaActual.getTime());

			mensaje.setTitulo("Solicitud denegada");
			mensaje.setDescripcion("Su solicitud de cambio de medico ha sido denegada");
			mensaje.setFecha(fecha);
			mensaje.setUsuario(solicitud.getUsuario());

			mensajeRepo.save(mensaje);

			solicitud.setEstado("DENEGADA");
			solicitudRepo.save(solicitud);
		}

		if (solicitud.getTitulo().equalsIgnoreCase("cambio enfermero")) {

			Mensaje mensaje = new Mensaje();
			Calendar calendar = Calendar.getInstance();
			java.util.Date fechaActual = calendar.getTime();
			Date fecha = new Date(fechaActual.getTime());

			mensaje.setTitulo("Solicitud denegada");
			mensaje.setDescripcion("Su solicitud de cambio de enfermero ha sido denegada");
			mensaje.setFecha(fecha);
			mensaje.setUsuario(solicitud.getUsuario());

			mensajeRepo.save(mensaje);

			solicitud.setEstado("DENEGADA");
			solicitudRepo.save(solicitud);
		}
	}

	@Override
	public void actualizaUsuario(Usuario usuario, UsuarioDto usuarioDto) {
		User user = usuario.getCuenta();
		user.setName(usuarioDto.getName());
		user.setEmail(usuarioDto.getEmail());
		user.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));

		userRepository.save(user);

		usuario.setDni(usuarioDto.getDni());
		usuario.setNombre(usuarioDto.getNombre());
		usuario.setApellidos(usuarioDto.getApellidos());
		usuario.setCuenta(user);

		usuarioRepo.save(usuario);
	}

	@Override
	public List<Alergia> buscarAlergiasUsuario(Usuario usuario) {
		return alergiaRepo.findByUsuario(usuario);
	}

	@Override
	public List<Usuario> buscarUsuariosMedico(String nombre, Medico medico) {
		return usuarioRepo.buscarPorNombreCompletoMedico(nombre, medico);
	}

	@Override
	public List<Observacion> buscarObservacionesUsuario(Usuario usuario) {
		return observacionRepo.findByUsuario(usuario);
	}
}
