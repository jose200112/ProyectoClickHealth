package com.example.registrationlogindemo.service.impl;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.registrationlogindemo.dto.EnfermeroDto;
import com.example.registrationlogindemo.dto.MedicoDto;
import com.example.registrationlogindemo.entity.Cita;
import com.example.registrationlogindemo.entity.Enfermero;
import com.example.registrationlogindemo.entity.Horario;
import com.example.registrationlogindemo.entity.Medico;
import com.example.registrationlogindemo.entity.Mensaje;
import com.example.registrationlogindemo.entity.Role;
import com.example.registrationlogindemo.entity.Solicitud;
import com.example.registrationlogindemo.entity.Tramo;
import com.example.registrationlogindemo.entity.TramoHorario;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;
import com.example.registrationlogindemo.repository.CitaRepositorio;
import com.example.registrationlogindemo.repository.HorarioRepositorio;
import com.example.registrationlogindemo.repository.MedicoRepositorio;
import com.example.registrationlogindemo.repository.MensajeRepositorio;
import com.example.registrationlogindemo.repository.RoleRepository;
import com.example.registrationlogindemo.repository.SolicitudRepositorio;
import com.example.registrationlogindemo.repository.TramoHorarioRepositorio;
import com.example.registrationlogindemo.repository.TramoRepositorio;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.repository.UsuarioRepositorio;
import com.example.registrationlogindemo.service.MedicoServicioI;



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

    public MedicoServicioImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           MedicoRepositorio medicoRepo,
                           HorarioRepositorio horarioRepo,
                           TramoRepositorio tramoRepo,
                           TramoHorarioRepositorio tramoHorarioRepo,
                           UsuarioRepositorio usuarioRepo,
                           CitaRepositorio citaRepo,
                           MensajeRepositorio mensajeRepo,
                           SolicitudRepositorio solicitudRepo) {
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
    }

	@Override
	public void saveMedico(MedicoDto medicoDto) {
        
        Medico medico = new Medico();
        medico.setNombre(medicoDto.getNombre());
        medico.setApellidos(medicoDto.getApellidos());
        medico.setDni(medicoDto.getDni());
        
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

        List<Tramo> tramos = tramoRepo.findByTiempoGreaterThanEqualAndTiempoLessThan(horario.getComienza(), horario.getTermina());
        
        for(Tramo horas:tramos) {
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
			
			for(Horario horario: medicoEncontrado.getHorarios()) {
				for(TramoHorario tramoHorario: horario.getTramoHorarios()) {
					tramoHorarioRepo.delete(tramoHorario);
				}
				
				horarioRepo.delete(horario);
			}
					
			for(Cita cita: medicoEncontrado.getCitas()) {
				if(cita.getAsistencia() == null) {
					citaRepo.delete(cita);
				} else {
					cita.setMedico(null);
					citaRepo.save(cita);
				}
			}
			
			for(Solicitud solicitud: medicoEncontrado.getSolicitudes()) {
				if(solicitud.getEstado() == null) {
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
			
			for(Usuario usuario: medicoEncontrado.getUsuarios()) {
				usuario.setMedico(medicoRepo.findMedicoConMenosUsuariosExcluye(id));
				usuarioRepo.save(usuario);
			}
			
			medicoRepo.deleteById(id);
		}
	}
	
	@Override
	public List<Medico> buscarMedicosPorNombre(String nombre){
		return medicoRepo.buscarPorNombreCompleto(nombre);
	}
	
	@Override
	public List<Medico> buscarMedicosPorNombreId(String nombre, Long id){
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
		mensaje.setDescripcion("Debido a su neglicencia, una persona ha perdido su oportunidad de asistir a su cita medica. "
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
		
		for(TramoHorario tramoHorario: horario.getTramoHorarios()) {
			tramoHorarioRepo.delete(tramoHorario);
		}
		
		
	       List<Tramo> tramos = tramoRepo.findByTiempoGreaterThanEqualAndTiempoLessThan(horario.getComienza(), horario.getTermina());
	        
	        for(Tramo horas:tramos) {
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
	
}
