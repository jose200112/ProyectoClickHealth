package com.example.registrationlogindemo.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.registrationlogindemo.dto.EnfermeroDto;
import com.example.registrationlogindemo.dto.VacunaDto;
import com.example.registrationlogindemo.entity.Enfermero;
import com.example.registrationlogindemo.entity.Horario;
import com.example.registrationlogindemo.entity.Role;
import com.example.registrationlogindemo.entity.Tramo;
import com.example.registrationlogindemo.entity.TramoHorario;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Vacuna;
import com.example.registrationlogindemo.repository.EnfermeroRepositorio;
import com.example.registrationlogindemo.repository.HorarioRepositorio;
import com.example.registrationlogindemo.repository.RoleRepository;
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

    public EnfermeroServicioImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           EnfermeroRepositorio enfermeroRepo,
                           HorarioRepositorio horarioRepo,
                           TramoRepositorio tramoRepo,
                           TramoHorarioRepositorio tramoHorarioRepo,
                           VacunaRepositorio vacunaRepo,
                           UsuarioRepositorio usuarioRepo) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.enfermeroRepo = enfermeroRepo;
        this.horarioRepo = horarioRepo;
        this.tramoRepo = tramoRepo;
        this.tramoHorarioRepo = tramoHorarioRepo;
        this.usuarioRepo = usuarioRepo;
    }

	@Override
	public void saveUser(EnfermeroDto enfermeroDto) {
		
        Enfermero enfermero = new Enfermero();
        
        enfermero.setNombre(enfermeroDto.getNombre());
        enfermero.setApellidos(enfermeroDto.getApellidos());
        enfermero.setDni(enfermeroDto.getDni());
        
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

        List<Tramo> tramos = tramoRepo.findByTiempoGreaterThanEqualAndTiempoLessThanEqual(horario.getComienza(), horario.getTermina());
        
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
		vacuna.setUsuario(usuarioRepo.findByDni(vacunaDto.getDni()));
		vacunaRepo.save(vacuna);
	}


}
