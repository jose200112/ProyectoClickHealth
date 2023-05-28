package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.dto.CambiaClaveDto;
import com.example.registrationlogindemo.dto.MedicoDto;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.service.EmailServiceI;
import com.example.registrationlogindemo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {

	private UserService userService;

	@Autowired
	UserRepository userRepository;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/login")
	public String loginForm() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}

		return "redirect:/redirige";

	}

	@RequestMapping("/redirige")
	public String redirigeRol(HttpServletRequest request) {

		if (request.isUserInRole("ROLE_ADMIN")) {
			return "redirect:/admin/inicioAdmin";
		}

		if (request.isUserInRole("ROLE_ENFERMERO")) {
			return "redirect:/enfermero/inicioEnfermero";
		}

		if (request.isUserInRole("ROLE_USUARIO")) {
			return "redirect:/usuario/inicioUsuario";
		}

		if (request.isUserInRole("ROLE_MEDICO")) {
			return "redirect:/medico/inicioMedico";
		}

		return "redirect:/login";
	}

	@GetMapping("/claveOlvidada")
	public String claveOlvidada(Model model) {
		CambiaClaveDto cambiaClave = new CambiaClaveDto();
		model.addAttribute("cambiaClave", cambiaClave);
		return "ClaveOlvidada";
	}

	@PostMapping("/cambiaClave")
	public String cambiaClave(@Valid @ModelAttribute("cambiaClave") CambiaClaveDto cambiaClave, BindingResult result,
			Model model) {
		User user = userRepository.findByEmail(cambiaClave.getEmail());

		if (user == null) {
			result.rejectValue("email", null, "No existe ninguna cuenta con este correo");
		}

		if (!cambiaClave.getEmail().equals(cambiaClave.getEmail2())) {
			result.rejectValue("email2", null, "Los correos no coinciden");
		}

		if (result.hasErrors()) {
			model.addAttribute("cambiaClave", cambiaClave);
			return "ClaveOlvidada";
		}

		userService.actualizaClave(user);

		return "redirect:/claveOlvidada?success";
	}

}
