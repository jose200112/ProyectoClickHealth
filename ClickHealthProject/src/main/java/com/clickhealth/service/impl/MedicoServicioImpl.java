package com.clickhealth.service.impl;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clickhealth.dto.AlergiaDto;
import com.clickhealth.dto.EnfermeroDto;
import com.clickhealth.dto.MedicoDto;
import com.clickhealth.dto.ObservacionDto;
import com.clickhealth.entity.Alergia;
import com.clickhealth.entity.Cita;
import com.clickhealth.entity.Enfermero;
import com.clickhealth.entity.Horario;
import com.clickhealth.entity.Medico;
import com.clickhealth.entity.Mensaje;
import com.clickhealth.entity.Observacion;
import com.clickhealth.entity.Role;
import com.clickhealth.entity.Solicitud;
import com.clickhealth.entity.Tramo;
import com.clickhealth.entity.TramoHorario;
import com.clickhealth.entity.User;
import com.clickhealth.entity.Usuario;
import com.clickhealth.repository.CitaRepositorio;
import com.clickhealth.repository.HorarioRepositorio;
import com.clickhealth.repository.MedicoRepositorio;
import com.clickhealth.repository.MensajeRepositorio;
import com.clickhealth.repository.ObservacionRepositorio;
import com.clickhealth.repository.RoleRepository;
import com.clickhealth.repository.SolicitudRepositorio;
import com.clickhealth.repository.TramoHorarioRepositorio;
import com.clickhealth.repository.TramoRepositorio;
import com.clickhealth.repository.UserRepository;
import com.clickhealth.repository.UsuarioRepositorio;
import com.clickhealth.service.MedicoServicioI;

@Service
public class MedicoServicioImpl implements MedicoServicioI {
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;
	private MedicoRepositorio medicoRepo;
	private HorarioRepositorio horarioRepo;
	private TramoRepositorio tramoRepo;
	private TramoHorarioRepositorio tramoHorarioRepo;
	private UsuarioRepositorio usuarioRepo;
	private CitaRepositorio citaRepo;
	private MensajeRepositorio mensajeRepo;
	private SolicitudRepositorio solicitudRepo;
	private ObservacionRepositorio observacionRepo;

	public MedicoServicioImpl(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder, MedicoRepositorio medicoRepo, HorarioRepositorio horarioRepo,
			TramoRepositorio tramoRepo, TramoHorarioRepositorio tramoHorarioRepo, UsuarioRepositorio usuarioRepo,
			CitaRepositorio citaRepo, MensajeRepositorio mensajeRepo, SolicitudRepositorio solicitudRepo,
			ObservacionRepositorio observacionRepo) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.medicoRepo = medicoRepo;
		this.horarioRepo = horarioRepo;
		this.tramoRepo = tramoRepo;
		this.tramoHorarioRepo = tramoHorarioRepo;
		this.usuarioRepo = usuarioRepo;
		this.citaRepo = citaRepo;
		this.mensajeRepo = mensajeRepo;
		this.solicitudRepo = solicitudRepo;
		this.observacionRepo = observacionRepo;
	}

	@Override
	public void saveMedico(MedicoDto medicoDto) {

		Medico medico = new Medico();
		medico.setNombre(medicoDto.getNombre());
		medico.setApellidos(medicoDto.getApellidos());
		medico.setDni(medicoDto.getDni());
		medico.setSala(medicoDto.getSala());
		String codigo = "MED";
		Medico existeMedico;

		do {
			Random rand = new Random();
			int num = rand.nextInt(99999) + 1;
			codigo = codigo + num;
			existeMedico = medicoRepo.findByCodigo(codigo);
		} while (existeMedico != null);
		
		medico.setCodigo(codigo);

		medicoRepo.save(medico);

		User user = new User();
		user.setName(medicoDto.getName());
		user.setEmail(medicoDto.getEmail());
		user.setPassword(passwordEncoder.encode(medicoDto.getPassword()));
		user.setMedico(medico);

		Role role = roleRepository.findByName("ROLE_MEDICO");
		user.setRoles(Arrays.asList(role));
		userRepository.save(user);

		Horario horario = new Horario();
		horario.setComienza(medicoDto.getComienza());
		horario.setTermina(medicoDto.getTermina());
		horario.setMedico(medico);

		horarioRepo.save(horario);

		List<Tramo> tramos = tramoRepo.findByTiempoGreaterThanEqualAndTiempoLessThan(horario.getComienza(),
				horario.getTermina());

		for (Tramo horas : tramos) {
			TramoHorario tramoHorario = new TramoHorario();
			tramoHorario.setHorario(horario);
			tramoHorario.setTramo(horas);
			tramoHorarioRepo.save(tramoHorario);
		}

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
	public Medico buscaPorDni(String dni) {
		return medicoRepo.findByDni(dni);
	}

	@Override
	public Medico buscaPorSala(String sala) {
		return medicoRepo.findBySala(sala);
	}

	@Override
	public void borraMedico(Long id) {
		Optional<Medico> medico = medicoRepo.findById(id);
		if (!medico.isEmpty()) {
			Medico medicoEncontrado = medico.get();
			userRepository.delete(medicoEncontrado.getCuenta());

			for (Horario horario : medicoEncontrado.getHorarios()) {
				for (TramoHorario tramoHorario : horario.getTramoHorarios()) {
					tramoHorarioRepo.delete(tramoHorario);
				}

				horarioRepo.delete(horario);
			}

			for (Cita cita : medicoEncontrado.getCitas()) {
				if (cita.getAsistencia() == null) {
					citaRepo.delete(cita);
				} else {
					cita.setMedico(null);
					citaRepo.save(cita);
				}
			}

			for (Solicitud solicitud : medicoEncontrado.getSolicitudes()) {
				if (solicitud.getEstado() == null) {
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
				} else {
					solicitud.setMedico(null);
					solicitudRepo.save(solicitud);
				}
			}

			for (Usuario usuario : medicoEncontrado.getUsuarios()) {
				Medico medicoConMenos = medicoRepo.findMedicoConMenosUsuariosExcluye(id);

				usuario.setMedico(medicoConMenos);

				for (Observacion observacion : usuario.getObservaciones()) {
					observacion.setMedico(medicoConMenos);
					observacionRepo.save(observacion);
				}

				usuarioRepo.save(usuario);
			}

			medicoRepo.deleteById(id);
		}
	}

	@Override
	public List<Medico> buscarMedicosPorNombre(String nombre) {
		return medicoRepo.buscarPorNombreCompleto(nombre);
	}

	@Override
	public List<Medico> buscarMedicosPorNombreId(String nombre, Long id) {
		return medicoRepo.buscarPorNombreCompletoId(nombre, id);
	}

	@Override
	public void confirmaAsistencia(Cita cita) {
		cita.setAsistencia("PRESENTADO");
		citaRepo.save(cita);
	}

	@Override
	public void deniegaAsistencia(Cita cita) {
		cita.setAsistencia("NO PRESENTADO");
		citaRepo.save(cita);

		Calendar calendar = Calendar.getInstance();
		java.util.Date fechaActual = calendar.getTime();
		Date fecha = new Date(fechaActual.getTime());

		Mensaje mensaje = new Mensaje();
		mensaje.setTitulo("No se ha presentado a su cita");
		mensaje.setDescripcion(
				"Debido a su neglicencia, una persona ha perdido su oportunidad de conseguir una cita medica. "
						+ "Por favor, sea responsable y puntual con sus "
						+ "citas médicas para permitir que otros pacientes reciban la atención que merecen");
		mensaje.setUsuario(cita.getUsuario());
		mensaje.setFecha(fecha);

		mensajeRepo.save(mensaje);
	}

	@Override
	public void actualizaMedico(Medico medico, MedicoDto medicoDto) {
		User user = medico.getCuenta();
		user.setName(medicoDto.getName());
		user.setEmail(medicoDto.getEmail());
		user.setPassword(passwordEncoder.encode(medicoDto.getPassword()));

		userRepository.save(user);

		Horario horario = medico.getHorarios().get(0);

		horario.setComienza(medicoDto.getComienza());
		horario.setTermina(medicoDto.getTermina());

		horarioRepo.save(horario);

		for (TramoHorario tramoHorario : horario.getTramoHorarios()) {
			tramoHorarioRepo.delete(tramoHorario);
		}

		List<Tramo> tramos = tramoRepo.findByTiempoGreaterThanEqualAndTiempoLessThan(horario.getComienza(),
				horario.getTermina());

		for (Tramo horas : tramos) {
			TramoHorario tramoHorario = new TramoHorario();
			tramoHorario.setHorario(horario);
			tramoHorario.setTramo(horas);
			tramoHorarioRepo.save(tramoHorario);
		}

		medico.setDni(medicoDto.getDni());
		medico.setNombre(medicoDto.getNombre());
		medico.setApellidos(medicoDto.getApellidos());
		medico.setSala(medicoDto.getSala());

		medicoRepo.save(medico);

	}

	@Override
	public void guardaObservacion(ObservacionDto observacionDto) {
		Observacion observacion = new Observacion();

		observacion.setId(observacionDto.getId());
		observacion.setTratamiento(observacionDto.getTratamiento());
		observacion.setSintomas(observacionDto.getSintomas());
		observacion.setDiagnostico(observacionDto.getDiagnostico());
		observacion.setDescripcion(observacionDto.getDescripcion());
		observacion.setUsuario(observacionDto.getUsuario());
		observacion.setMedico(observacionDto.getMedico());

		observacionRepo.save(observacion);
	}

	@Override
	public void guardaNuevaObservacion(ObservacionDto observacionDto) {
		Observacion observacion = new Observacion();

		observacion.setTratamiento(observacionDto.getTratamiento());
		observacion.setSintomas(observacionDto.getSintomas());
		observacion.setDiagnostico(observacionDto.getDiagnostico());
		observacion.setDescripcion(observacionDto.getDescripcion());
		observacion.setUsuario(observacionDto.getUsuario());
		observacion.setMedico(observacionDto.getMedico());

		observacionRepo.save(observacion);
	}

}
