package com.example.registrationlogindemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

	@GetMapping("usuario/usuarioInicio")
	public String usuarioInicio() {
		return "UsuarioInicio";
	}

	@GetMapping("/usuario/solicitudCambio")
	public String registroSolicitud() {
		return "SolicitudCambio";
	}

	@GetMapping("/usuario/solicitudBaja")
	public String getSolicitudBaja() {
		return "solicitudBaja";
	}

	@GetMapping("/usuario/registroVacuna")
	public String getRegistroVacunas() {
		return "registroVacuna";
	}

	@GetMapping("/usuario/reservaCita")
	public String getReservaCita() {
		return "calendar";
	}
}
