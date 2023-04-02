package com.clickhealth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NurseController {
	
	@GetMapping("/enfermero/registraVacunas")
	public String getRegistroVacunas() {
		return "RegistraVacunas";
	}
	
	@GetMapping("/enfermero/consultaCita")
	public String getConsulta() {
		return "ConsultaCita";
	}
}
