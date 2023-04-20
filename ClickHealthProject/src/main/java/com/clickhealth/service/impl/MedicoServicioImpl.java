package com.clickhealth.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clickhealth.dto.MedicoDto;
import com.clickhealth.entity.Enfermero;
import com.clickhealth.entity.Horario;
import com.clickhealth.entity.Medico;
import com.clickhealth.entity.Role;
import com.clickhealth.entity.Tramo;
import com.clickhealth.entity.TramoHorario;
import com.clickhealth.entity.User;
import com.clickhealth.repository.HorarioRepositorio;
import com.clickhealth.repository.MedicoRepositorio;
import com.clickhealth.repository.RoleRepository;
import com.clickhealth.repository.TramoHorarioRepositorio;
import com.clickhealth.repository.TramoRepositorio;
import com.clickhealth.repository.UserRepository;
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

    public MedicoServicioImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           MedicoRepositorio medicoRepo,
                           HorarioRepositorio horarioRepo,
                           TramoRepositorio tramoRepo,
                           TramoHorarioRepositorio tramoHorarioRepo) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.medicoRepo = medicoRepo;
        this.horarioRepo = horarioRepo;
        this.tramoRepo = tramoRepo;
        this.tramoHorarioRepo = tramoHorarioRepo;
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
}
