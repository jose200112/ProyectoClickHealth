package com.clickhealth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
	
	@GetMapping("/admin/registroUsuario")
	public String getRegistroUsuarios() {
		return "RegistroUsuario";
	}
	
	@GetMapping("/admin/registroEnfermero")
	public String getRegistroEnfermero() {
		return "RegistroEnfermero";
	}
	
	@GetMapping("/admin/registroMedico")
	public String getRegistroMedico() {
		return "RegistroMedico";
	}
	
	@GetMapping("/admin/solicitud")
	public String getSolicitud() {
		return "Solicitud";
	}
	
}
