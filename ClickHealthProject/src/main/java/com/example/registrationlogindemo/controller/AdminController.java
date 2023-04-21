package com.example.registrationlogindemo.controller;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.registrationlogindemo.dto.EnfermeroDto;
import com.example.registrationlogindemo.dto.MedicoDto;
import com.example.registrationlogindemo.dto.UsuarioDto;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.service.EnfermeroServicioI;
import com.example.registrationlogindemo.service.MedicoServicioI;
import com.example.registrationlogindemo.service.UserService;
import com.example.registrationlogindemo.service.UsuarioServicioI;

import jakarta.validation.Valid;

@Controller
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private EnfermeroServicioI enfermeroServicioI;

	@Autowired
	private MedicoServicioI medicoServicioI;
	
	@Autowired
	private UsuarioServicioI usuarioServicioI;

	private static final Pattern REGEXP = Pattern.compile("[0-9]{8}[A-Z]");
	private static final String DIGITO_CONTROL = "TRWAGMYFPDXBNJZSQVHLCKE";
	private static final String[] INVALIDOS = new String[] { "00000000T", "00000001R", "99999999R" };

	public AdminController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("admin/inicioAdmin")
	public String inicioAdmin() {
		return "inicioAdmin";
	}

	// handler method to handle user registration request
	@GetMapping("/admin/registroUsuario")
	public String formularioRegistroUsuario(Model model) {
		UsuarioDto usuario = new UsuarioDto();
		model.addAttribute("usuario", usuario);
		return "RegistroUsuario";
	}

	// handler method to handle register user form submit request
	@PostMapping("/admin/guardaUsuario")
	public String registraUsuario(@Valid @ModelAttribute("usuario") UsuarioDto usuario, BindingResult result, Model model) {
		User existeEmail = usuarioServicioI.findByEmail(usuario.getEmail());
		User existeNombre = usuarioServicioI.findByName(usuario.getName());
		EmailValidator validator = EmailValidator.getInstance();

		if (existeEmail != null) {
			result.rejectValue("email", null, "El usuario ya existe");
		}

		if (!validator.isValid(usuario.getEmail())) {
			result.rejectValue("email", null, "El email no es valido");
		}

		if (existeNombre != null) {
			result.rejectValue("name", null, "El nombre de usuario ya existe");
		}

		if (validarDNI(usuario.getDni()) == false) {
			result.rejectValue("dni", null, "Dni no valido");
		}

		if (result.hasErrors()) {
			model.addAttribute("usuario", usuario);
			return "RegistroUsuario";
		}
		usuarioServicioI.saveUsuario(usuario);
		return "redirect:/admin/registroUsuario?success";
	}

	@GetMapping("/admin/registroEnfermero")
	public String formularioRegistroEnfermero(Model model) {
		EnfermeroDto enfermero = new EnfermeroDto();
		model.addAttribute("enfermero", enfermero);
		return "RegistroEnfermero";
	}

	@PostMapping("/admin/guardaEnfermero")
	public String registraEnfermero(@Valid @ModelAttribute("enfermero") EnfermeroDto enfermero, BindingResult result,
			Model model) {
		User existeEmail = enfermeroServicioI.findByEmail(enfermero.getEmail());
		User existeNombre = enfermeroServicioI.findByName(enfermero.getName());
		EmailValidator validator = EmailValidator.getInstance();

		if (existeEmail != null) {
			result.rejectValue("email", null, "El usuario ya existe");
		}

		if (!validator.isValid(enfermero.getEmail())) {
			result.rejectValue("email", null, "El email no es valido");
		}

		if (existeNombre != null) {
			result.rejectValue("name", null, "El nombre de usuario ya existe");
		}

		try {
			if (enfermero.getComienza().compareTo(enfermero.getTermina()) > 0
					|| enfermero.getComienza().compareTo(enfermero.getTermina()) == 0) {
				result.rejectValue("comienza", null, "El horario debe ser valido");
			}
		} catch (NullPointerException e) {
			System.err.println(e);
			result.rejectValue("comienza", null, "Por favor, rellene el horario");
		}

		if (validarDNI(enfermero.getDni()) == false) {
			result.rejectValue("dni", null, "Dni no valido");
		}

		if (result.hasErrors()) {
			model.addAttribute("enfermero", enfermero);
			return "RegistroEnfermero";
		}
		enfermeroServicioI.saveUser(enfermero);
		return "redirect:/admin/registroEnfermero?success";
	}

	
	@GetMapping("/admin/registroMedico")
	public String formularioRegistroMedico(Model model) {
		MedicoDto medico = new MedicoDto();
		model.addAttribute("medico", medico);
		return "RegistroMedico";
	}

	@PostMapping("/admin/guardaMedico")
	public String registraMedico(@Valid @ModelAttribute("medico") MedicoDto medico, BindingResult result,
			Model model) {
		User existeEmail = medicoServicioI.findByEmail(medico.getEmail());
		User existeNombre = medicoServicioI.findByName(medico.getName());
		EmailValidator validator = EmailValidator.getInstance();

		if (existeEmail != null) {
			result.rejectValue("email", null, "El usuario ya existe");
		}

		if (!validator.isValid(medico.getEmail())) {
			result.rejectValue("email", null, "El email no es valido");
		}

		if (existeNombre != null) {
			result.rejectValue("name", null, "El nombre de usuario ya existe");
		}

		try {
			if (medico.getComienza().compareTo(medico.getTermina()) > 0
					|| medico.getComienza().compareTo(medico.getTermina()) == 0) {
				result.rejectValue("comienza", null, "El horario debe ser valido");
			}
		} catch (NullPointerException e) {
			System.err.println(e);
			result.rejectValue("comienza", null, "Por favor, rellene el horario");
		}

		if (validarDNI(medico.getDni()) == false) {
			result.rejectValue("dni", null, "Dni no valido");
		}

		if (result.hasErrors()) {
			model.addAttribute("medico", medico);
			return "RegistroMedico";
		}

		medicoServicioI.saveMedico(medico);

		return "redirect:/admin/registroMedico?success";
	}

	public static boolean validarDNI(String dni) {
		return Arrays.binarySearch(INVALIDOS, dni) < 0 // (1)
				&& REGEXP.matcher(dni).matches() // (2)
				&& dni.charAt(8) == DIGITO_CONTROL.charAt(Integer.parseInt(dni.substring(0, 8)) % 23); // (3)
	}

//
//	@GetMapping("/admin/solicitud")
//	public String getSolicitud() {
//		return "Solicitud";
//	}

//	
//	@GetMapping("/admin/citas")
//	public String mostrarCalendario(Model model) {
//	    List<Cita> citasMedicas = citaMedicaService.obtenerCitasMedicas();
//	    model.addAttribute("citasMedicas", citasMedicas);
//	    return "calendar";
//	}

}
