package com.example.registrationlogindemo.service.impl;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.registrationlogindemo.dto.AlergiaDto;
import com.example.registrationlogindemo.dto.EnfermeroDto;
import com.example.registrationlogindemo.dto.VacunaDto;
import com.example.registrationlogindemo.entity.Alergia;
import com.example.registrationlogindemo.entity.Cita;
import com.example.registrationlogindemo.entity.Enfermero;
import com.example.registrationlogindemo.entity.Horario;
import com.example.registrationlogindemo.entity.Mensaje;
import com.example.registrationlogindemo.entity.Role;
import com.example.registrationlogindemo.entity.Solicitud;
import com.example.registrationlogindemo.entity.Tramo;
import com.example.registrationlogindemo.entity.TramoHorario;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;
import com.example.registrationlogindemo.entity.Vacuna;
import com.example.registrationlogindemo.repository.AlergiaRepositorio;
import com.example.registrationlogindemo.repository.CitaRepositorio;
import com.example.registrationlogindemo.repository.EnfermeroRepositorio;
import com.example.registrationlogindemo.repository.HorarioRepositorio;
import com.example.registrationlogindemo.repository.MensajeRepositorio;
import com.example.registrationlogindemo.repository.RoleRepository;
import com.example.registrationlogindemo.repository.SolicitudRepositorio;
import com.example.registrationlogindemo.repository.TramoHorarioRepositorio;
import com.example.registrationlogindemo.repository.TramoRepositorio;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.repository.UsuarioRepositorio;
import com.example.registrationlogindemo.repository.VacunaRepositorio;
import com.example.registrationlogindemo.service.EnfermeroServicioI;



@Service
public class EnfermeroServicioImpl implements EnfermeroServicioI {

	@Autowired
    private UserRepository userRepository;
	@Autowired
    private RoleRepository roleRepository;
	@Autowired
    private PasswordEncoder passwordEncoder;
	@Autowired
    private EnfermeroRepositorio enfermeroRepo;
	@Autowired
    private HorarioRepositorio horarioRepo;
	@Autowired
    private TramoRepositorio tramoRepo;
	@Autowired
    private TramoHorarioRepositorio tramoHorarioRepo;
	@Autowired
    private VacunaRepositorio vacunaRepo;
	@Autowired
    private UsuarioRepositorio usuarioRepo;
	@Autowired
	private CitaRepositorio citaRepo;
	@Autowired
	private MensajeRepositorio mensajeRepo;
	@Autowired
	private SolicitudRepositorio solicitudRepo;
	@Autowired
	private AlergiaRepositorio alergiaRepo;

    public EnfermeroServicioImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           EnfermeroRepositorio enfermeroRepo,
                           HorarioRepositorio horarioRepo,
                           TramoRepositorio tramoRepo,
                           TramoHorarioRepositorio tramoHorarioRepo,
                           VacunaRepositorio vacunaRepo,
                           UsuarioRepositorio usuarioRepo,
                           CitaRepositorio citaRepo,
                           MensajeRepositorio mensajeRepo,
                           SolicitudRepositorio solicitudRepo,
                           AlergiaRepositorio alergiaRepo) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.enfermeroRepo = enfermeroRepo;
        this.horarioRepo = horarioRepo;
        this.tramoRepo = tramoRepo;
        this.tramoHorarioRepo = tramoHorarioRepo;
        this.usuarioRepo = usuarioRepo;
        this.citaRepo = citaRepo;
        this.mensajeRepo = mensajeRepo;
        this.solicitudRepo = solicitudRepo;
        this.alergiaRepo = alergiaRepo;
    }

	@Override
	public void saveUser(EnfermeroDto enfermeroDto) {
		
        Enfermero enfermero = new Enfermero();
        
        enfermero.setNombre(enfermeroDto.getNombre());
        enfermero.setApellidos(enfermeroDto.getApellidos());
        enfermero.setDni(enfermeroDto.getDni());
        enfermero.setSala(enfermeroDto.getSala());
    	String codigo = "ENF";
    	Enfermero existeEnfermero;
    	
        do {
        	Random rand = new Random();
            int num = rand.nextInt(99999) + 1;
            codigo = codigo + num;
        	existeEnfermero = enfermeroRepo.findByCodigo(codigo);
        }while(existeEnfermero != null);
        
        enfermero.setCodigo(codigo);
        enfermeroRepo.save(enfermero);
        
        User user = new User();
        user.setName(enfermeroDto.getName());
        user.setEmail(enfermeroDto.getEmail());
        user.setPassword(passwordEncoder.encode(enfermeroDto.getPassword()));
        user.setEnfermero(enfermero);
        
        Role role = roleRepository.findByName("ROLE_ENFERMERO");
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
        
        Horario horario = new Horario();
        horario.setComienza(enfermeroDto.getComienza());
        horario.setTermina(enfermeroDto.getTermina());
        horario.setEnfermero(enfermero);
        		        
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
	public void saveVacuna(VacunaDto vacunaDto) {
		Vacuna vacuna = new Vacuna();
		Enfermero enfermero = vacunaDto.getUser().getEnfermero();
		vacuna.setEnfermero(enfermero);
		vacuna.setDosis(vacunaDto.getDosis());
		vacuna.setNombre(vacunaDto.getNombre());
		vacuna.setNumLote(vacunaDto.getNumLote());
		vacuna.setFecha(Date.valueOf(vacunaDto.getFecha()));
		vacuna.setUsuario(vacunaDto.getUsuario());
		vacunaRepo.save(vacuna);
	}
	
	@Override
	public void borraEnfermero(Long id) {
		Optional<Enfermero> enfermero = enfermeroRepo.findById(id);
		if (!enfermero.isEmpty()) {
			Enfermero enfermeroEncontrado = enfermero.get();
			userRepository.delete(enfermeroEncontrado.getCuenta());
			for (Vacuna vacuna : enfermeroEncontrado.getVacunas()) {
				vacuna.setEnfermero(null);
				vacunaRepo.save(vacuna);
			}
			
			for(Alergia alergia: enfermeroEncontrado.getAlergias()) {
				alergia.setEnfermero(enfermeroEncontrado);
				alergiaRepo.save(alergia);
			}
			
			for(Horario horario: enfermeroEncontrado.getHorarios()) {
				for(TramoHorario tramoHorario: horario.getTramoHorarios()) {
					tramoHorarioRepo.delete(tramoHorario);
				}
				
				horarioRepo.delete(horario);
			}
					
			
			for(Cita cita: enfermeroEncontrado.getCitas()) {
				if(cita.getAsistencia() == null) {
					citaRepo.delete(cita);
				} else {
					cita.setEnfermero(null);
					citaRepo.save(cita);
				}
			}
			
			for(Solicitud solicitud: enfermeroEncontrado.getSolicitudes()) {
				if(solicitud.getEstado() == null) {
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
				} else {
					solicitud.setEnfermero(null);
					solicitudRepo.save(solicitud);
				}
			}
			
			for(Usuario usuario: enfermeroEncontrado.getUsuarios()) {
				Enfermero enfermeroMenosUsuarios = enfermeroRepo.EnfermeroConMenosUsuariosExcluye(id);
				usuario.setEnfermero(enfermeroMenosUsuarios);
				for(Vacuna vacuna: usuario.getVacunas()) {
					vacuna.setEnfermero(enfermeroMenosUsuarios);
					vacunaRepo.save(vacuna);
				}
				
				for(Alergia alergia: usuario.getAlergias()){
					alergia.setEnfermero(enfermeroMenosUsuarios);
					alergiaRepo.save(alergia);
				}
				
				usuarioRepo.save(usuario);
			}
			
			enfermeroRepo.deleteById(id);
		}

	}
	
	@Override
	public List<Enfermero> buscarEnfermeros(String dni){
		return enfermeroRepo.buscarEnfermerosDni(dni);
	}
	
	@Override
	public Enfermero buscaPorDni(String dni) {
		return enfermeroRepo.findByDni(dni);
	}
	
	@Override
	public Enfermero buscaPorSala(String sala) {
		return enfermeroRepo.findBySala(sala);
	}
	
	
	@Override
	public List<Enfermero> buscarEnfermerosPorNombre(String nombre){
		return enfermeroRepo.buscarPorNombreCompleto(nombre);
	}
	
	@Override
	public List<Enfermero> buscarEnfermerosPorNombreId(String nombre,Long id){
		return enfermeroRepo.buscarPorNombreCompletoId(nombre, id);
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
		mensaje.setDescripcion("Debido a su neglicencia, una persona ha perdido su oportunidad de conseguir una cita medica. "
				+ "Por favor, sea responsable y puntual con sus "
				+ "citas médicas para permitir que otros pacientes reciban la atención que merecen");
		mensaje.setUsuario(cita.getUsuario());
		mensaje.setFecha(fecha);
		
		mensajeRepo.save(mensaje);
	}
	
	@Override
	public void actualizaEnfermero(Enfermero enfermero, EnfermeroDto enfermeroDto) {
		User user = enfermero.getCuenta();
		user.setName(enfermeroDto.getName());
		user.setEmail(enfermeroDto.getEmail());
		user.setPassword(passwordEncoder.encode(enfermeroDto.getPassword()));
		
		userRepository.save(user);
		
		Horario horario = enfermero.getHorarios().get(0);
		
		horario.setComienza(enfermeroDto.getComienza());
		horario.setTermina(enfermeroDto.getTermina());
		
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
	        
	        enfermero.setDni(enfermeroDto.getDni());
	        enfermero.setNombre(enfermeroDto.getNombre());
	        enfermero.setApellidos(enfermeroDto.getApellidos());
	        enfermero.setSala(enfermeroDto.getSala());
	        
	        enfermeroRepo.save(enfermero);
		
	}
	
	@Override
	public void actualizaVacuna(VacunaDto vacunaDto) {
		Vacuna vacuna = new Vacuna();
		vacuna.setId(vacunaDto.getId());
		vacuna.setEnfermero(vacunaDto.getEnfermero());
		vacuna.setUsuario(vacunaDto.getUsuario());
		vacuna.setDosis(vacunaDto.getDosis());
		vacuna.setFecha(Date.valueOf(vacunaDto.getFecha()));
		vacuna.setNombre(vacunaDto.getNombre());
		vacuna.setNumLote(vacunaDto.getNumLote());
		
		vacunaRepo.save(vacuna);
	}
	
	@Override
	public void guardaAlergia(AlergiaDto alergiaDto) {
		Alergia alergia = new Alergia();
		alergia.setId(alergiaDto.getId());
		alergia.setGravedad(alergiaDto.getGravedad());
		alergia.setTratamiento(alergiaDto.getTratamiento());
		alergia.setDescripcion(alergiaDto.getDescripcion());
		alergia.setCausa(alergiaDto.getCausa());
		alergia.setUsuario(alergiaDto.getUsuario());
		alergia.setEnfermero(alergiaDto.getEnfermero());
		
		alergiaRepo.save(alergia);
	}
	
	@Override
	public void guardaNuevaAlergia(AlergiaDto alergiaDto) {
		Alergia alergia = new Alergia();
		alergia.setGravedad(alergiaDto.getGravedad());
		alergia.setTratamiento(alergiaDto.getTratamiento());
		alergia.setDescripcion(alergiaDto.getDescripcion());
		alergia.setCausa(alergiaDto.getCausa());
		alergia.setUsuario(alergiaDto.getUsuario());
		alergia.setEnfermero(alergiaDto.getEnfermero());
		
		alergiaRepo.save(alergia);
	}
}
