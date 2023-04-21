package com.example.registrationlogindemo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.registrationlogindemo.dto.VacunaDto;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.repository.UsuarioRepositorio;
import com.example.registrationlogindemo.service.EnfermeroServicioI;

import jakarta.validation.Valid;

@Controller
public class EnfermeroController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	EnfermeroServicioI enfermeroServicio;
	
	@Autowired
	UsuarioRepositorio usuarioRepo;

	@GetMapping("/enfermero/registroVacuna")
	public String getRegistroVacunas(Model model) {
		VacunaDto vacuna = new VacunaDto();
		model.addAttribute("vacuna", vacuna);
		return "RegistroVacuna";
	}
	
	@PostMapping("/enfermero/guardaVacuna")
	public String registraEnfermero(@Valid @ModelAttribute("vacuna") VacunaDto vacuna, BindingResult result,
			Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		vacuna.setUser(user);
		Usuario existeUsuario = usuarioRepo.findByDni(vacuna.getDni());

		if (existeUsuario == null) {
			result.rejectValue("dni", null, "No existe ningun usuario con ese dni");
		}
		
		if (result.hasErrors()) {
			model.addAttribute("vacuna", vacuna);
			return "RegistroVacuna";
		}
		
		enfermeroServicio.saveVacuna(vacuna);
		

		return "redirect:/admin/registroVacuna?success";
	}

	@GetMapping("/enfermero/consultaCita")
	public String getConsulta() {
		return "calendar";
	}
	
	@GetMapping("/enfermero/saludo")
	public String getSaludo() {
		return "saludo";
	}
}
