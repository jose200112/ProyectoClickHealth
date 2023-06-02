package com.clickhealth.controller;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.clickhealth.dto.BajaDto;
import com.clickhealth.dto.EnfermeroDto;
import com.clickhealth.dto.MedicoDto;
import com.clickhealth.dto.UsuarioDto;
import com.clickhealth.entity.Enfermero;
import com.clickhealth.entity.Medico;
import com.clickhealth.entity.Solicitud;
import com.clickhealth.entity.User;
import com.clickhealth.entity.Usuario;
import com.clickhealth.repository.EnfermeroRepositorio;
import com.clickhealth.repository.MedicoRepositorio;
import com.clickhealth.repository.SolicitudRepositorio;
import com.clickhealth.repository.UsuarioRepositorio;
import com.clickhealth.service.EmailServiceI;
import com.clickhealth.service.EnfermeroServicioI;
import com.clickhealth.service.MedicoServicioI;
import com.clickhealth.service.UserService;
import com.clickhealth.service.UsuarioServicioI;
import com.itextpdf.text.Image;

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

	@Autowired
	private UsuarioRepositorio usuarioRepo;

	@Autowired
	private EnfermeroRepositorio enfermeroRepo;

	@Autowired
	private MedicoRepositorio medicoRepo;

	@Autowired
	private SolicitudRepositorio solicitudRepo;

	@Autowired
	EmailServiceI emailServiceI;

	private static final Pattern REGEXP = Pattern.compile("[0-9]{8}[A-Z]");
	private static final String DIGITO_CONTROL = "TRWAGMYFPDXBNJZSQVHLCKE";
	private static final String[] INVALIDOS = new String[] { "00000000T", "00000001R", "99999999R" };

	public AdminController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("admin/inicioAdmin")
	public String inicioAdmin() {
		return "InicioAdmin";
	}

	@GetMapping("/admin/registroUsuario")
	public String formularioRegistroUsuario(Model model) {
		UsuarioDto usuario = new UsuarioDto();
		model.addAttribute("usuario", usuario);
		return "RegistroUsuario";
	}

	@PostMapping("/admin/guardaUsuario")
	public String registraUsuario(@Valid @ModelAttribute("usuario") UsuarioDto usuario, BindingResult result,
			Model model) {
		User existeEmail = usuarioServicioI.findByEmail(usuario.getEmail());
		User existeNombre = usuarioServicioI.findByName(usuario.getName());
		Usuario existeDni = usuarioServicioI.buscaPorDni(usuario.getDni());
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

		if (existeDni != null) {
			result.rejectValue("dni", null, "Ya existe un usuario con ese dni");
		}

		if (validarDNI(usuario.getDni()) == false) {
			result.rejectValue("dni", null, "Dni no valido");
		}

		if (result.hasErrors()) {
			model.addAttribute("usuario", usuario);
			return "RegistroUsuario";
		}
		usuarioServicioI.saveUsuario(usuario);
		String contenido = "";
		contenido = "<html><body>" + "<img src='https://i.imgur.com/ymmyp91.png'/>" + "<br><br>" + "Bienvenido "
				+ usuario.getNombre() + " " + usuario.getApellidos() + " a ClickHealth.<br>"
				+ "¡Ya ha sido registrado con exito en nuestro sistema!<br>"
				+ "A continuación, le proporcionamos su clave de acceso: <b>" + usuario.getPassword() + "</b>"
				+ "</body></html>";

		emailServiceI.enviarCorreo(usuario.getEmail(), "¡Bienvenido a ClickHealth!", contenido);
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
		Enfermero existeDni = enfermeroServicioI.buscaPorDni(enfermero.getDni());
		Enfermero existeSala = enfermeroServicioI.buscaPorSala(enfermero.getSala());
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

		if (existeDni != null) {
			result.rejectValue("dni", null, "Ya existe un enfermero con ese dni");
		}

		if (existeSala != null) {
			result.rejectValue("sala", null, "Esa sala ya esta ocupada");
		}

		try {
			if (enfermero.getComienza().compareTo(enfermero.getTermina()) > 0
					|| enfermero.getComienza().compareTo(enfermero.getTermina()) == 0
					|| enfermero.getComienza().compareTo(LocalTime.of(07, 00, 0)) < 0
					|| enfermero.getTermina().compareTo(LocalTime.of(20, 00, 0)) > 0) {
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
		String contenido = "";
		contenido = "<html><body>" + "<img src='https://i.imgur.com/ymmyp91.png'/>"
				+ "<h2>¡Bienvenido/a a ClickHealth!</h2>" + "<p>Estimado " + enfermero.getNombre() + ",</p>"
				+ "<p>Te damos la bienvenida a nuestro equipo de enfermeros. Nos complace informarte que tu alta ha sido procesada con éxito.</p>"
				+ "<p>A continuación, te proporcionamos algunos detalles importantes:</p>"
				+ "Su horario comienza De <strong>" + enfermero.getComienza() + "</strong> a " + enfermero.getTermina()
				+ " en la sala <strong>" + enfermero.getSala() + "</strong>.<br>"
				+ "<strong>Su clave de acceso al sistema:</strong>" + enfermero.getPassword() + ".<br>"
				+ "<p>Estamos encantados de tener a bordo a un profesional como tú. Esperamos que te sientas cómodo y que disfrutes de tu trabajo con nosotros.</p>"
				+ "<p>Si tienes alguna pregunta o necesitas más información, no dudes en ponerte en contacto con nosotros.</p>"
				+ "<p>¡Te deseamos mucho éxito en tu nueva posición!</p>" + "<p>Saludos cordiales,</p>"
				+ "<p>El equipo de ClickHealth</p>" + "</body></html>";

		emailServiceI.enviarCorreo(enfermero.getEmail(), "¡Bienvenido a ClickHealth!", contenido);
		return "redirect:/admin/registroEnfermero?success";
	}

	@GetMapping("/admin/registroMedico")
	public String formularioRegistroMedico(Model model) {
		MedicoDto medico = new MedicoDto();
		model.addAttribute("medico", medico);
		return "RegistroMedico";
	}

	@PostMapping("/admin/guardaMedico")
	public String registraMedico(@Valid @ModelAttribute("medico") MedicoDto medico, BindingResult result, Model model) {
		User existeEmail = medicoServicioI.findByEmail(medico.getEmail());
		User existeNombre = medicoServicioI.findByName(medico.getName());
		Medico existeDni = medicoServicioI.buscaPorDni(medico.getDni());
		Medico existeSala = medicoServicioI.buscaPorSala(medico.getSala());
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

		if (existeDni != null) {
			result.rejectValue("dni", null, "Ya existe un medico con ese dni");
		}

		if (existeSala != null) {
			result.rejectValue("sala", null, "Esa sala ya esta ocupada");
		}

		try {
			if (medico.getComienza().compareTo(medico.getTermina()) > 0
					|| medico.getComienza().compareTo(medico.getTermina()) == 0
					|| medico.getComienza().compareTo(LocalTime.of(07, 00, 0)) < 0
					|| medico.getTermina().compareTo(LocalTime.of(20, 00, 0)) > 0) {
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

		String contenido = "<html><body>" + "<img src='https://i.imgur.com/ymmyp91.png'/>"
				+ "<h2>¡Bienvenido/a a ClickHealth!</h2>" + "<p>Estimado " + medico.getNombre() + ",</p>"
				+ "<p>Te damos la bienvenida a nuestro equipo de medicos. Nos complace informarte que tu alta ha sido procesada con éxito.</p>"
				+ "<p>A continuación, te proporcionamos algunos detalles importantes:</p>"
				+ "Su horario comienza De <strong>" + medico.getComienza() + "</strong> a <strong>"
				+ medico.getTermina() + "</strong> en la sala <strong>" + medico.getSala() + "</strong>.<br>"
				+ "Su clave de acceso al sistema:<strong>" + medico.getPassword() + "</strong>.<br>"
				+ "<p>Estamos encantados de tener a bordo a un profesional como tú. Esperamos que te sientas cómodo y que disfrutes de tu trabajo con nosotros.</p>"
				+ "<p>Si tienes alguna pregunta o necesitas más información, no dudes en ponerte en contacto con nosotros.</p>"
				+ "<p>¡Te deseamos mucho éxito en tu nueva posición!</p>" + "<p>Saludos cordiales,</p>"
				+ "<p>El equipo de ClickHealth</p>" + "</body></html>";

		emailServiceI.enviarCorreo(medico.getEmail(), "¡Bienvenido a ClickHealth!", contenido);

		return "redirect:/admin/registroMedico?success";
	}

	public static boolean validarDNI(String dni) {
		return Arrays.binarySearch(INVALIDOS, dni) < 0 // (1)
				&& REGEXP.matcher(dni).matches() // (2)
				&& dni.charAt(8) == DIGITO_CONTROL.charAt(Integer.parseInt(dni.substring(0, 8)) % 23); // (3)
	}

	/**
	 * Borra un usuario por id
	 * 
	 * @param id (id)
	 * @return String
	 */
	@GetMapping("/admin/borraUsuarioBuscador/{id}/{formulario}")
	public String borraUsuarioBuscador(@PathVariable Long id, Model model,
			@PathVariable("formulario") String formulario) {
		Optional<Usuario> usuario = usuarioRepo.findById(id);
		model.addAttribute("formulario", formulario);
		String error = null;
		String exito = null;

		if (usuario.isEmpty()) {
			error = "El usuario no existe";
			model.addAttribute("error", error);
			return "BorraUsuario";
		} else {
			exito = "Usuario dado de baja con exito";
			model.addAttribute("exito", exito);
			model.addAttribute("formulario", formulario);
			usuarioServicioI.borraUsuario(id);
			return "BorraUsuario";
		}

	}

	/**
	 * Busca a usuarios por nombre y apellidos
	 * 
	 * @param model  modelo
	 * @param nombre nombre
	 * @return String
	 */
	@GetMapping("/admin/borraUsuario")
	public String borraUsuario(Model model, @Param("nombre") String nombre, @Param("formulario") String formulario) {
		List<Usuario> usuarios = usuarioServicioI.buscarUsuarios(nombre);
		BajaDto baja = new BajaDto();
		model.addAttribute("baja", baja);
		model.addAttribute("nombre", nombre);
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("formulario", formulario);
		return "BorraUsuario";
	}

	@PostMapping("/admin/borraUsuarioForm")
	public String borraUsuarioForm(@Valid @ModelAttribute("baja") BajaDto baja, @Param("formulario") String formulario,
			Model model, BindingResult result) {
		Usuario usuario = usuarioRepo.findByDni(baja.getDni());
		model.addAttribute("formulario", "por formulario");

		if (baja.getDni().isEmpty()) {
			result.rejectValue("dni", null, "El dni no puede estar vacio");
		} else {
			if (usuario == null) {
				result.rejectValue("dni", null, "No existe usuario con tal dni");
			}
		}

		if (baja.getDescripcion().isEmpty()) {
			result.rejectValue("descripcion", null, "La descripcion no puede estar vacia");
		}

		if (baja.getCausa().isEmpty()) {
			result.rejectValue("causa", null, "Debe especificar una causa");
		}

		if (result.hasErrors()) {
			model.addAttribute("baja", baja);
			return "BorraUsuario";
		}

		usuarioServicioI.borraUsuarioForm(baja, usuario);

		model.addAttribute("exito", "Usuario dado de baja con exito");

		return "BorraUsuario";
	}

	/**
	 * Borra un Enfermero por id
	 * 
	 * @param id (id)
	 * @return String
	 */
	@GetMapping("/admin/borraEnfermeroBuscador/{id}")
	public String borraEnfermero(@PathVariable Long id, Model model) {
		Optional<Enfermero> enfermero = enfermeroRepo.findById(id);
		String error = null;
		String exito = null;

		if (enfermero.isEmpty()) {
			error = "El enfermero no existe";
			model.addAttribute("error", error);
			return "BorraEnfermero";
		} else {
			exito = "Enfermero dado de baja con exito";
			model.addAttribute("exito", exito);
			enfermeroServicioI.borraEnfermero(id);
			return "BorraEnfermero";
		}
	}

	/**
	 * Busca a enfermeros por nombre y apellidos
	 * 
	 * @param model  modelo
	 * @param nombre nombre
	 * @return String
	 */
	@GetMapping("/admin/borraEnfermero")
	public String buscaEnfermero(Model model, @Param("nombre") String nombre) {
		List<Enfermero> enfermeros = enfermeroServicioI.buscarEnfermerosPorNombre(nombre);
		model.addAttribute("nombre", nombre);
		model.addAttribute("enfermeros", enfermeros);
		return "BorraEnfermero";
	}

	/**
	 * Borra un Enfermero por id
	 * 
	 * @param id (id)
	 * @return String
	 */
	@GetMapping("/admin/borraMedicoBuscador/{id}")
	public String borraMedico(@PathVariable Long id, Model model) {
		Optional<Medico> medico = medicoRepo.findById(id);
		String error = null;
		String exito = null;

		if (medico.isEmpty()) {
			error = "El medico no existe";
			model.addAttribute("error", error);
			return "BorraMedico";
		} else {
			exito = "Medico dado de baja con exito";
			model.addAttribute("exito", exito);
			medicoServicioI.borraMedico(id);
			return "BorraMedico";
		}
	}

	/**
	 * Busca a medicos por nombre y apellidos
	 * 
	 * @param model  modelo
	 * @param nombre nombre
	 * @return String
	 */
	@GetMapping("/admin/borraMedico")
	public String buscaMedico(Model model, @Param("nombre") String nombre) {
		List<Medico> medicos = medicoServicioI.buscarMedicosPorNombre(nombre);
		model.addAttribute("nombre", nombre);
		model.addAttribute("medicos", medicos);
		return "BorraMedico";
	}

	@GetMapping("/admin/resuelveSolicitud")
	public String resuelveSolicitud(Model model) {
		List<Solicitud> solicitudes = solicitudRepo.findByEstadoIsNull();
		model.addAttribute("solicitudes", solicitudes);
		return "ResuelveSolicitud";
	}

	@GetMapping("/admin/resuelveSolicitudAceptada/{id}")
	public String resuelveSolicitudAceptada(@PathVariable Long id, Model model) {
		System.out.println(id);

		Optional<Solicitud> solicitud = solicitudRepo.findById(id);
		Solicitud s = solicitud.get();

		usuarioServicioI.AceptaSolicitud(s);

		return "redirect:/admin/resuelveSolicitud";
	}

	@GetMapping("/admin/resuelveSolicitudDenegada/{id}")
	public String resuelveSolicitudDenegada(@PathVariable String id, Model model) {
		Optional<Solicitud> existeSolicitud = solicitudRepo.findById(Long.parseLong(id));
		Solicitud solicitud = existeSolicitud.get();

		usuarioServicioI.DeniegaSolicitud(solicitud);

		return "redirect:/admin/resuelveSolicitud";
	}

	@GetMapping("/admin/actualizaBuscadorUsuario")
	public String actualizaBuscadorUsuario(Model model, @Param("nombre") String nombre) {
		List<Usuario> usuarios = usuarioServicioI.buscarUsuarios(nombre);
		model.addAttribute("nombre", nombre);
		model.addAttribute("usuarios", usuarios);
		return "BuscadorUsuario";
	}

	@GetMapping("/admin/actualizaUsuario/{id}")
	public String actualizaUsuario(@PathVariable Long id, Model modelo) {
		Optional<Usuario> usuario = usuarioRepo.findById(id);

		if (usuario.isPresent()) {
			modelo.addAttribute("usuario", usuario.get().toDto());
		} else {
			return "redirect:/admin/actualizaBuscadorUsuario?error";
		}

		return "ActualizaUsuario";
	}

	@PostMapping("/admin/usuarioActualizado/{id}")
	public String usuarioActualizado(@Valid @ModelAttribute("usuario") UsuarioDto usuario, BindingResult result,
			@PathVariable Long id, Model model) {
		Optional<Usuario> usuarioActualizado = usuarioRepo.findById(id);
		User existeEmail = usuarioServicioI.findByEmail(usuario.getEmail());
		User existeNombre = usuarioServicioI.findByName(usuario.getName());
		Usuario existeDni = usuarioServicioI.buscaPorDni(usuario.getDni());
		EmailValidator validator = EmailValidator.getInstance();
		Usuario usuarioActualizar = null;

		if (usuarioActualizado.isPresent()) {
			usuarioActualizar = usuarioActualizado.get();
		} else {
			return "redirect:/admin/actualizaBuscadorUsuario?error";
		}

		if (!usuarioActualizar.getCuenta().getEmail().equalsIgnoreCase(usuario.getEmail()) && existeEmail != null) {
			result.rejectValue("email", null, "El usuario ya existe");
		}

		if (!validator.isValid(usuario.getEmail())) {
			result.rejectValue("email", null, "El email no es valido");
		}

		if (!usuarioActualizar.getCuenta().getName().equalsIgnoreCase(usuario.getName()) && existeNombre != null) {
			result.rejectValue("name", null, "El nombre de usuario ya existe");
		}

		if (!usuarioActualizar.getDni().equalsIgnoreCase(usuario.getDni()) && existeDni != null) {
			result.rejectValue("dni", null, "Ya existe un usuario con ese dni");
		}

		if (validarDNI(usuario.getDni()) == false) {
			result.rejectValue("dni", null, "Dni no valido");
		}

		if (result.hasErrors()) {
			model.addAttribute("usuario", usuario);
			model.addAttribute("result", result);
			return "ActualizaUsuario";
		}

		usuarioServicioI.actualizaUsuario(usuarioActualizar, usuario);

		return "redirect:/admin/actualizaBuscadorUsuario?success";
	}

	@GetMapping("/admin/actualizaBuscadorEnfermero")
	public String actualizaBuscadorEnfermero(Model model, @Param("nombre") String nombre) {
		List<Enfermero> enfermeros = enfermeroServicioI.buscarEnfermerosPorNombre(nombre);
		model.addAttribute("nombre", nombre);
		model.addAttribute("enfermeros", enfermeros);
		return "BuscadorEnfermero";
	}

	@GetMapping("/admin/actualizaEnfermero/{id}")
	public String actualizaEnfermero(@PathVariable Long id, Model modelo) {
		Optional<Enfermero> enfermero = enfermeroRepo.findById(id);

		if (enfermero.isPresent()) {
			modelo.addAttribute("enfermero", enfermero.get().toDto());
		} else {
			return "redirect:/admin/actualizaBuscadorEnfermero?error";
		}

		return "ActualizaEnfermero";
	}

	@PostMapping("/admin/enfermeroActualizado/{id}")
	public String enfermeroActualizado(@Valid @ModelAttribute("enfermero") EnfermeroDto enfermero, BindingResult result,
			@PathVariable Long id, Model model) {
		User existeEmail = enfermeroServicioI.findByEmail(enfermero.getEmail());
		User existeNombre = enfermeroServicioI.findByName(enfermero.getName());
		Enfermero existeDni = enfermeroServicioI.buscaPorDni(enfermero.getDni());
		Enfermero existeSala = enfermeroServicioI.buscaPorSala(enfermero.getSala());
		EmailValidator validator = EmailValidator.getInstance();
		Optional<Enfermero> enfermeroActualizado = enfermeroRepo.findById(id);
		Enfermero enfermeroActualizar = null;

		if (enfermeroActualizado.isPresent()) {
			enfermeroActualizar = enfermeroActualizado.get();
		} else {
			return "redirect:/admin/actualizaBuscadorEnfermero?error";
		}

		if (!enfermeroActualizar.getCuenta().getEmail().equalsIgnoreCase(enfermero.getEmail()) && existeEmail != null) {
			result.rejectValue("email", null, "El usuario ya existe");
		}

		if (!validator.isValid(enfermero.getEmail())) {
			result.rejectValue("email", null, "El email no es valido");
		}

		if (!enfermeroActualizar.getCuenta().getName().equalsIgnoreCase(enfermero.getName()) && existeNombre != null) {
			result.rejectValue("name", null, "El nombre de usuario ya existe");
		}

		if (!enfermeroActualizar.getDni().equalsIgnoreCase(enfermero.getDni()) && existeDni != null) {
			result.rejectValue("dni", null, "Ya existe un enfermero con ese dni");
		}

		if (!enfermeroActualizar.getSala().equalsIgnoreCase(enfermero.getSala()) && existeSala != null) {
			result.rejectValue("sala", null, "Esa sala ya esta ocupada");
		}

		try {
			if (enfermero.getComienza().compareTo(enfermero.getTermina()) > 0
					|| enfermero.getComienza().compareTo(enfermero.getTermina()) == 0
					|| enfermero.getComienza().compareTo(LocalTime.of(07, 00, 0)) < 0
					|| enfermero.getTermina().compareTo(LocalTime.of(20, 00, 0)) > 0) {
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
			model.addAttribute("result", result);
			return "ActualizaEnfermero";
		}

		enfermeroServicioI.actualizaEnfermero(enfermeroActualizar, enfermero);

		return "redirect:/admin/actualizaBuscadorEnfermero?success";
	}

	@GetMapping("/admin/actualizaBuscadorMedico")
	public String actualizaBuscadorMedico(Model model, @Param("nombre") String nombre) {
		List<Medico> medicos = medicoServicioI.buscarMedicosPorNombre(nombre);
		model.addAttribute("nombre", nombre);
		model.addAttribute("medicos", medicos);
		return "BuscadorMedico";
	}

	@GetMapping("/admin/actualizaMedico/{id}")
	public String actualizaMedico(@PathVariable Long id, Model modelo) {
		Optional<Medico> medico = medicoRepo.findById(id);

		if (medico.isPresent()) {
			modelo.addAttribute("medico", medico.get().toDto());
		} else {
			return "redirect:/admin/actualizaBuscadorMedico?error";
		}

		return "ActualizaMedico";
	}

	@PostMapping("/admin/medicoActualizado/{id}")
	public String medicoActualizado(@Valid @ModelAttribute("medico") MedicoDto medico, BindingResult result,
			Model model, @PathVariable Long id) {
		User existeEmail = medicoServicioI.findByEmail(medico.getEmail());
		User existeNombre = medicoServicioI.findByName(medico.getName());
		Medico existeDni = medicoServicioI.buscaPorDni(medico.getDni());
		Medico existeSala = medicoServicioI.buscaPorSala(medico.getSala());
		EmailValidator validator = EmailValidator.getInstance();
		Optional<Medico> medicoActualizado = medicoRepo.findById(id);
		Medico medicoActualizar = null;

		if (medicoActualizado.isPresent()) {
			medicoActualizar = medicoActualizado.get();
		} else {
			return "redirect:/admin/actualizaBuscadorMedico?error";
		}

		if (!medicoActualizar.getCuenta().getEmail().equalsIgnoreCase(medico.getEmail()) && existeEmail != null) {
			result.rejectValue("email", null, "El usuario ya existe");
		}

		if (!validator.isValid(medico.getEmail())) {
			result.rejectValue("email", null, "El email no es valido");
		}

		if (!medicoActualizar.getCuenta().getName().equalsIgnoreCase(medico.getName()) && existeNombre != null) {
			result.rejectValue("name", null, "El nombre de usuario ya existe");
		}

		if (!medicoActualizar.getDni().equalsIgnoreCase(medico.getDni()) && existeDni != null) {
			result.rejectValue("dni", null, "Ya existe un medico con ese dni");
		}

		if (!medicoActualizar.getSala().equalsIgnoreCase(medico.getSala()) && existeSala != null) {
			result.rejectValue("sala", null, "Esa sala ya esta ocupada");
		}

		try {
			if (medico.getComienza().compareTo(medico.getTermina()) > 0
					|| medico.getComienza().compareTo(medico.getTermina()) == 0
					|| medico.getComienza().compareTo(LocalTime.of(07, 00, 0)) < 0
					|| medico.getTermina().compareTo(LocalTime.of(20, 00, 0)) > 0) {
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
			model.addAttribute("result", result);
			return "ActualizaMedico";
		}

		medicoServicioI.actualizaMedico(medicoActualizar, medico);

		return "redirect:/admin/actualizaBuscadorMedico?success";

	}

}
