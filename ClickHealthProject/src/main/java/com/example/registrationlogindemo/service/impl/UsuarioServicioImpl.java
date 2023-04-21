package com.example.registrationlogindemo.service.impl;

import java.util.Arrays;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.registrationlogindemo.dto.UsuarioDto;
import com.example.registrationlogindemo.entity.Role;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;
import com.example.registrationlogindemo.repository.EnfermeroRepositorio;
import com.example.registrationlogindemo.repository.MedicoRepositorio;
import com.example.registrationlogindemo.repository.RoleRepository;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.repository.UsuarioRepositorio;
import com.example.registrationlogindemo.service.UsuarioServicioI;



@Service
public class UsuarioServicioImpl implements UsuarioServicioI {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private EnfermeroRepositorio enfermeroRepo;
    private MedicoRepositorio medicoRepo;
    private UsuarioRepositorio usuarioRepo;

    public UsuarioServicioImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           EnfermeroRepositorio enfermeroRepo,
                           MedicoRepositorio medicoRepo,
                           UsuarioRepositorio usuarioRepo) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.enfermeroRepo = enfermeroRepo;
        this.medicoRepo = medicoRepo;
        this.usuarioRepo = usuarioRepo;
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
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findByName(String name) {
    	return userRepository.findByName(name);
    }

}
