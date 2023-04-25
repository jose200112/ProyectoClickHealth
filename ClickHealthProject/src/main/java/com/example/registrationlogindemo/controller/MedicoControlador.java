package com.example.registrationlogindemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MedicoControlador {
	@GetMapping("/medico/inicioMedico")
	public String inicioMedico() {
		return "InicioMedico";
	}
}
