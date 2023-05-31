package com.example.registrationlogindemo.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.sql.Date;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.clickhealth.object.Columna;
import com.example.registrationlogindemo.dto.BajaDto;
import com.example.registrationlogindemo.dto.MensajeDto;
import com.example.registrationlogindemo.dto.SolicitudDto;
import com.example.registrationlogindemo.dto.UsuarioDto;
import com.example.registrationlogindemo.dto.VacunaDto;
import com.example.registrationlogindemo.entity.Alergia;
import com.example.registrationlogindemo.entity.Cita;
import com.example.registrationlogindemo.entity.Enfermero;
import com.example.registrationlogindemo.entity.Medico;
import com.example.registrationlogindemo.entity.Mensaje;
import com.example.registrationlogindemo.entity.Observacion;
import com.example.registrationlogindemo.entity.Solicitud;
import com.example.registrationlogindemo.entity.Tramo;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;
import com.example.registrationlogindemo.entity.Vacuna;
import com.example.registrationlogindemo.repository.CitaRepositorio;
import com.example.registrationlogindemo.repository.EnfermeroRepositorio;
import com.example.registrationlogindemo.repository.MedicoRepositorio;
import com.example.registrationlogindemo.repository.MensajeRepositorio;
import com.example.registrationlogindemo.repository.SolicitudRepositorio;
import com.example.registrationlogindemo.repository.TramoRepositorio;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.service.EnfermeroServicioI;
import com.example.registrationlogindemo.service.MedicoServicioI;
import com.example.registrationlogindemo.service.UsuarioServicioI;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UsuarioControlador {

	@Autowired
	UserRepository userRepository;

	@Autowired
	TramoRepositorio tramoRepo;

	@Autowired
	CitaRepositorio citaRepo;

	@Autowired
	MedicoRepositorio medicoRepo;

	@Autowired
	MedicoServicioI medicoServicioI;

	@Autowired
	SolicitudRepositorio solicitudRepo;

	@Autowired
	UsuarioServicioI usuarioServicioI;

	@Autowired
	EnfermeroRepositorio enfermeroRepo;

	@Autowired
	EnfermeroServicioI enfermeroServicioI;

	@Autowired
	MensajeRepositorio mensajeRepo;

	@GetMapping("/usuario/inicioUsuario")
	public String inicioUsuario(Principal principal, Model model) {
		User user = userRepository.findByEmail(principal.getName());
		Cita cita = citaRepo.findByUsuarioAndAsistenciaIsNull(user.getUsuario());

		LocalDate fecha = LocalDate.now();
		Date fechaActual = Date.valueOf(fecha);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaActual);

		calendar.add(Calendar.DAY_OF_MONTH, -5);

		java.util.Date fechaRestada = calendar.getTime();
		java.sql.Date fechaRestadaSql = new java.sql.Date(fechaRestada.getTime());

		List<Mensaje> mensajes = mensajeRepo.findMensajesRecientes(fechaRestadaSql, fechaActual, user.getUsuario());

		if (mensajes != null) {
			List<MensajeDto> mensajesAsistencia = new ArrayList<>();
			List<MensajeDto> mensajesSolicitudAceptada = new ArrayList<>();
			List<MensajeDto> mensajesSolicitudDenegada = new ArrayList<>();
			for (Mensaje mensaje : mensajes) {
				long diferenciaMillis = fechaActual.getTime() - mensaje.getFecha().getTime();

				long dias = TimeUnit.MILLISECONDS.toDays(diferenciaMillis);

				MensajeDto mensajeDto = mensaje.toDto(dias);

				if (mensaje.getTitulo().equalsIgnoreCase("No se ha presentado a su cita")) {
					mensajesAsistencia.add(mensajeDto);
				}

				if (mensaje.getTitulo().equalsIgnoreCase("Solicitud aceptada")) {
					mensajesSolicitudAceptada.add(mensajeDto);
				}

				if (mensaje.getTitulo().equalsIgnoreCase("Solicitud denegada")) {
					mensajesSolicitudDenegada.add(mensajeDto);
				}
			}

			if(cita != null) {
				Calendar calendario = Calendar.getInstance();
				calendario.setTime(cita.getFecha());

				calendario.add(Calendar.DAY_OF_MONTH, -1);

				java.util.Date fechaRestadaCita = calendario.getTime();
				java.sql.Date fechaRestadaCitaSql = new java.sql.Date(fechaRestadaCita.getTime());
				
				if (fechaActual.equals(fechaRestadaCitaSql) && cita.isConfirmada() == false) {
					model.addAttribute("plazoAbierto", true);

				}
			}
			
			model.addAttribute("mensajesAsistencia", mensajesAsistencia);
			model.addAttribute("mensajesSolicitudAceptada", mensajesSolicitudAceptada);
			model.addAttribute("mensajesSolicitudDenegada", mensajesSolicitudDenegada);
		}

		return "InicioUsuario";
	}

	@GetMapping("/usuario/reservaCitaMedico")
	public String reservaCitaMedico(Model model) {
		Calendar cal = Calendar.getInstance();
		int mes = cal.get(Calendar.MONTH);
		int diaActual = cal.get(Calendar.DAY_OF_MONTH);

		DateFormatSymbols symbols = new DateFormatSymbols(new Locale("es", "ES"));
		String[] meses = symbols.getMonths();
		String mesActual = meses[mes];
		List<Columna> columna = getCalendario();
		model.addAttribute("columna", columna);
		model.addAttribute("diaActual", diaActual);
		model.addAttribute("mesActual", mesActual.toUpperCase());
		return "ReservaCitaMedico";
	}

	@GetMapping("/usuario/horasDisponiblesMedico")
	public String getHorasDisponiblesMedico(@RequestParam("dia") Integer dia, Principal principal, Model model,
			HttpSession session, RedirectAttributes redirectAttrs) {
		User user = userRepository.findByEmail(principal.getName());
		Usuario usuario = user.getUsuario();

		LocalDate fechaActual = LocalDate.now();
		int diaActual = fechaActual.getDayOfMonth();
		LocalDate ultimoDiaMesActual = fechaActual.with(TemporalAdjusters.lastDayOfMonth());
		int diaUltimo = ultimoDiaMesActual.getDayOfMonth();

		if (dia <= diaActual || dia > diaUltimo) {
			String error = "Dia no valido";
			redirectAttrs.addFlashAttribute("error", error);
			return "redirect:/usuario/reservaCitaMedico";
		}

		LocalDate fecha = LocalDate.now().withDayOfMonth(dia);
		List<Tramo> tramos = tramoRepo.findTramosDisponibles(usuario.getMedico().getId(), fecha);
		model.addAttribute("tramos", tramos);
		model.addAttribute("diaSeleccionado", dia);
		return "HorasDisponiblesMedico";
	}

	@PostMapping("/usuario/creaCitaMedico")
	public String creaCitaMedico(@RequestParam("tramoSeleccionado") String tramoSeleccionado,
			@RequestParam("diaSeleccionado") Integer dia, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		LocalDate fecha = LocalDate.now().withDayOfMonth(dia);
		Usuario usuario = user.getUsuario();

		LocalDate fechaActual = LocalDate.now();

		if (citaRepo.findByUsuarioAndAsistenciaIsNull(usuario) != null) {
			return "redirect:/usuario/reservaCitaMedico?error";
		}

		Cita cita = new Cita();
		cita.setUsuario(usuario);
		cita.setMedico(usuario.getMedico());
		cita.setFecha(Date.valueOf(fecha));

		long diferenciaEnDias = ChronoUnit.DAYS.between(fechaActual, cita.getFecha().toLocalDate());
		if (diferenciaEnDias <= 1) {
			cita.setConfirmada(true);
		}
		cita.setAsistencia(null);
		cita.setTramo(tramoRepo.findByTiempo(LocalTime.parse(tramoSeleccionado)));
		citaRepo.save(cita);

		return "redirect:/usuario/reservaCitaMedico?success";
	}

	@GetMapping("/usuario/reservaCitaEnfermero")
	public String reservaCitaEnfermero(Model model) {
		Calendar cal = Calendar.getInstance();
		int mes = cal.get(Calendar.MONTH);
		int diaActual = cal.get(Calendar.DAY_OF_MONTH);

		DateFormatSymbols symbols = new DateFormatSymbols(new Locale("es", "ES"));
		String[] meses = symbols.getMonths();
		String mesActual = meses[mes];
		List<Columna> columna = getCalendario();
		model.addAttribute("columna", columna);
		model.addAttribute("diaActual", diaActual);
		model.addAttribute("mesActual", mesActual);
		return "ReservaCitaEnfermero";
	}

	@GetMapping("/usuario/horasDisponiblesEnfermero")
	public String getHorasDisponiblesEnfermero(@RequestParam("dia") Integer dia, Principal principal, Model model,
			HttpSession session, RedirectAttributes redirectAttrs) {
		User user = userRepository.findByEmail(principal.getName());
		Usuario usuario = user.getUsuario();

		LocalDate fechaActual = LocalDate.now();
		int diaActual = fechaActual.getDayOfMonth();
		LocalDate ultimoDiaMesActual = fechaActual.with(TemporalAdjusters.lastDayOfMonth());
		int diaUltimo = ultimoDiaMesActual.getDayOfMonth();

		if (dia <= diaActual || dia > diaUltimo) {
			String error = "Dia no valido";
			redirectAttrs.addFlashAttribute("error", error);
			return "redirect:/usuario/reservaCitaMedico";
		}

		LocalDate fecha = LocalDate.now().withDayOfMonth(dia);
		List<Tramo> tramos = tramoRepo.findTramosDisponiblesEnfermero(usuario.getEnfermero().getId(), fecha);
		model.addAttribute("tramos", tramos);
		model.addAttribute("diaSeleccionado", dia);
		return "HorasDisponiblesEnfermero";
	}

	@PostMapping("/usuario/creaCitaEnfermero")
	public String creaCitaEnfermero(@RequestParam("tramoSeleccionado") String tramoSeleccionado,
			@RequestParam("diaSeleccionado") Integer dia, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		LocalDate fecha = LocalDate.now().withDayOfMonth(dia);
		Usuario usuario = user.getUsuario();

		LocalDate fechaActual = LocalDate.now();

		if (citaRepo.findByUsuarioAndAsistenciaIsNull(usuario) != null) {
			return "redirect:/usuario/reservaCitaEnfermero?error";
		}

		Cita cita = new Cita();
		cita.setUsuario(usuario);
		cita.setEnfermero(usuario.getEnfermero());
		cita.setFecha(Date.valueOf(fecha));

		long diferenciaEnDias = ChronoUnit.DAYS.between(fechaActual, cita.getFecha().toLocalDate());
		if (diferenciaEnDias <= 1) {
			cita.setConfirmada(true);
		}
		cita.setAsistencia(null);
		cita.setTramo(tramoRepo.findByTiempo(LocalTime.parse(tramoSeleccionado)));
		citaRepo.save(cita);

		return "redirect:/usuario/reservaCitaEnfermero?success";
	}

	@GetMapping("/usuario/vacunas")
	public String getVacunas(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		List<Vacuna> vacunas = user.getUsuario().getVacunas();
		model.addAttribute("vacunas", vacunas);
		return "VacunasRegistradas";
	}
	
	@GetMapping("/usuario/alergias")
	public String getAlergias(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		List<Alergia> alergias = user.getUsuario().getAlergias();
		model.addAttribute("alergias", alergias);
		return "AlergiasRegistradas";
	}
	
	@GetMapping("/usuario/observaciones")
	public String getObservaciones(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		List<Observacion> observaciones = user.getUsuario().getObservaciones();
		model.addAttribute("observaciones",observaciones);
		return "ObservacionesRegistradas";
	}
	
	@GetMapping("/usuario/registro")
	public String getRegistro(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		List<Observacion> observaciones = user.getUsuario().getObservaciones();
		List<Alergia> alergias = user.getUsuario().getAlergias();
		List<Vacuna> vacunas = user.getUsuario().getVacunas();
		int citasTotales = citaRepo.countByUsuarioAndAsistenciaIsNotNull(user.getUsuario());
		int citasAsistidas = citaRepo.contarCitasConfirmadasUsuario(user.getUsuario());
		double porcentajeAsistencia = 0;
		Usuario usuario = user.getUsuario();
		
		model.addAttribute("usuario",usuario);
		model.addAttribute("observaciones", observaciones);
		model.addAttribute("vacunas",vacunas);
		model.addAttribute("alergias",alergias);
		model.addAttribute("citasTotales",citasTotales);
		model.addAttribute("citasAsistidas",citasAsistidas);
		
		if(citasTotales > 0) {
		    porcentajeAsistencia = (citasAsistidas * 100.0) / citasTotales;
		}
		
		model.addAttribute("porcentajeAsistencia",porcentajeAsistencia);
		model.addAttribute("citasTotales",citasTotales);
		
		return "RegistroCompleto";
	}

	@GetMapping("/usuario/nuevaSolicitud")
	public String getNuevaSolicitud(Model model, @Param("nombre") String nombre,
			@Param("seleccionado") String seleccionado, @Param("codigoMedico") String codigoMedico,
			@Param("codigoEnfermero") String codigoEnfermero, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		List<Medico> medicos = medicoServicioI.buscarMedicosPorNombreId(nombre, user.getUsuario().getMedico().getId());
		List<Enfermero> enfermeros = enfermeroServicioI.buscarEnfermerosPorNombreId(nombre,
				user.getUsuario().getEnfermero().getId());
		SolicitudDto solicitud = new SolicitudDto();
		model.addAttribute("solicitud", solicitud);
		model.addAttribute("nombre", nombre);
		model.addAttribute("codigoMedico", codigoMedico);
		model.addAttribute("codigoEnfermero", codigoEnfermero);
		model.addAttribute("medicos", medicos);
		model.addAttribute("enfermeros", enfermeros);
		model.addAttribute("seleccionado", seleccionado);
		return "NuevaSolicitud";
	}

	@GetMapping("/usuario/nuevaSolicitudBuscadorMedico/{id}/{seleccionado}")
	public String borraUsuarioBuscador(@PathVariable Long id, Model model,
			@PathVariable("seleccionado") String seleccionado, Principal principal) {
		Optional<Medico> medico = medicoRepo.findById(id);
		model.addAttribute("seleccionado", seleccionado);
		User user = userRepository.findByEmail(principal.getName());

		String error = null;
		String exito = null;

		if (medico.isEmpty()) {
			error = "El medico no existe";
			model.addAttribute("error", error);
			return "NuevaSolicitud";
		} else {
			if (user.getUsuario().getMedico().getId() == medico.get().getId()) {
				error = "No puedes enviar una solicitud para tu mismo medico";
				model.addAttribute("error", error);
				return "NuevaSolicitud";
			} else {
				exito = "Medico seleccionado correctamente";
				model.addAttribute("exito", exito);
				model.addAttribute("codigoMedico", medico.get().getCodigo());
				SolicitudDto solicitud = new SolicitudDto();
				model.addAttribute("solicitud", solicitud);
				return "NuevaSolicitud";
			}

		}

	}

	@PostMapping("/usuario/nuevaSolicitudMedico")
	public String nuevaSolicitudMedico(@Valid @ModelAttribute("solicitud") SolicitudDto solicitud, Principal principal,
			Model model, BindingResult result, @Param("seleccionado") String seleccionado,
			@Param("codigoMedico") String codigoMedico) {
		User user = userRepository.findByEmail(principal.getName());
		Solicitud existeSolicitud = solicitudRepo.findByUsuarioAndTituloAndEstadoIsNull(user.getUsuario(),
				"cambio medico");
		model.addAttribute("codigoMedico", codigoMedico);
		model.addAttribute("seleccionado", seleccionado);

		if (existeSolicitud != null) {
			result.rejectValue("descripcion", null, "Ya tienes una solicitud pendiente");
		}

		if (solicitud.getDni().isEmpty()) {
			result.rejectValue("dni", null, "Ingrese su dni");
		} else if (!solicitud.getDni().equalsIgnoreCase(user.getUsuario().getDni())) {
			result.rejectValue("dni", null, "Dni incorrecto");
		}

		if (solicitud.getDescripcion().isEmpty()) {
			result.rejectValue("descripcion", null, "Al menos debe tener una descripcion");
		}

		if (solicitud.getDescripcion().length() > 200) {
			result.rejectValue("descripcion", null, "Resuma un poco la descripcion por favor");
		}

		if (result.hasErrors()) {
			model.addAttribute("solicitud", solicitud);
			return "NuevaSolicitud";
		}

		model.addAttribute("exito", "Solicitud enviada");

		usuarioServicioI.guardaSolicitudMedico(user.getUsuario(), solicitud, codigoMedico, seleccionado);

		return "NuevaSolicitud";
	}

	@GetMapping("/usuario/nuevaSolicitudBuscadorEnfermero/{id}/{seleccionado}")
	public String borraUsuarioBuscadorEnfermero(@PathVariable Long id, Model model,
			@PathVariable("seleccionado") String seleccionado, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Enfermero> enfermero = enfermeroRepo.findById(id);
		model.addAttribute("seleccionado", seleccionado);
		String error = null;
		String exito = null;

		if (enfermero.isEmpty()) {
			error = "El enfermero no existe";
			model.addAttribute("error", error);
			return "NuevaSolicitud";
		} else {
			if (user.getUsuario().getEnfermero().getId() == enfermero.get().getId()) {
				error = "No puedes enviar una solicitud para tu mismo enfermero";
				model.addAttribute("error", error);
				return "NuevaSolicitud";
			} else {
				exito = "Enfermero seleccionado correctamente";
				model.addAttribute("exito", exito);
				model.addAttribute("codigoEnfermero", enfermero.get().getCodigo());
				SolicitudDto solicitud = new SolicitudDto();
				model.addAttribute("solicitud", solicitud);
				return "NuevaSolicitud";
			}
		}

	}

	@PostMapping("/usuario/nuevaSolicitudEnfermero")
	public String nuevaSolicitudEnfermero(@Valid @ModelAttribute("solicitud") SolicitudDto solicitud,
			Principal principal, Model model, BindingResult result, @Param("seleccionado") String seleccionado,
			@Param("codigoEnfermero") String codigoEnfermero) {
		User user = userRepository.findByEmail(principal.getName());
		Solicitud existeSolicitud = solicitudRepo.findByUsuarioAndTituloAndEstadoIsNull(user.getUsuario(),
				"cambio enfermero");
		model.addAttribute("codigoEnfermero", codigoEnfermero);
		model.addAttribute("seleccionado", seleccionado);

		if (existeSolicitud != null) {
			result.rejectValue("descripcion", null, "Ya tienes una solicitud pendiente");
		}

		if (solicitud.getDni().isEmpty()) {
			result.rejectValue("dni", null, "Ingrese su dni");
		} else if (!solicitud.getDni().equalsIgnoreCase(user.getUsuario().getDni())) {
			result.rejectValue("dni", null, "Dni incorrecto");
		}

		if (solicitud.getDescripcion().isEmpty()) {
			result.rejectValue("descripcion", null, "Al menos debe tener una descripcion");
		}

		if (solicitud.getDescripcion().length() > 200) {
			result.rejectValue("descripcion", null, "Resuma un poco la descripcion por favor");
		}

		if (result.hasErrors()) {
			model.addAttribute("solicitud", solicitud);
			return "NuevaSolicitud";
		}

		model.addAttribute("exito", "Solicitud enviada");

		usuarioServicioI.guardaSolicitudEnfermero(user.getUsuario(), solicitud, codigoEnfermero, seleccionado);

		return "NuevaSolicitud";
	}

	@PostMapping("/usuario/descargaVacunas")
	public ResponseEntity<byte[]> generarPDF(Principal principal) throws IOException, DocumentException {
		try {
			// Obtener las vacunas desde algún origen de datos
			User user = userRepository.findByEmail(principal.getName());
			List<Vacuna> vacunas = user.getUsuario().getVacunas();
			Usuario usuario = user.getUsuario();

			// Crear un nuevo documento PDF
			Document document = new Document();

			// Crear un ByteArrayOutputStream para escribir el contenido del PDF
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// Obtener el flujo de salida para escribir el contenido del PDF
			PdfWriter.getInstance(document, baos);

			// Abrir el documento
			document.open();

			Resource resource = new ClassPathResource("images/clickhealthlogo.PNG");

			Image image = Image.getInstance(resource.getURL().toString());
			image.setAlignment(Element.ALIGN_CENTER);
			image.scaleToFit(200f, 200f); // Ajustar el tamaño de la imagen según tus necesidades
			image.setSpacingAfter(2);
			document.add(image);

			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
			Paragraph header = new Paragraph("Vacunas del paciente " + usuario.getNombre() + " "
					+ usuario.getApellidos() + " con DNI " + usuario.getDni(), headerFont);
			header.setAlignment(Element.ALIGN_CENTER);
			header.setSpacingAfter(10f);
			document.add(header);

			// Crear la tabla de vacunas
			PdfPTable table = new PdfPTable(4);
			table.setWidthPercentage(100);
			table.setSpacingBefore(10f);
			table.setSpacingAfter(10f);

			// Configurar las celdas de la tabla
			Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
			PdfPCell cell = new PdfPCell();
			cell.setBackgroundColor(new BaseColor(13, 110, 253));
			cell.setPadding(5);

			// Agregar encabezados de columna
			cell.setPhrase(new Phrase("Nombre", font));
			table.addCell(cell);

			cell.setPhrase(new Phrase("Fecha", font));
			table.addCell(cell);

			cell.setPhrase(new Phrase("Número de Dosis", font));
			table.addCell(cell);

			cell.setPhrase(new Phrase("Número de Lote", font));
			table.addCell(cell);

			// Agregar las vacunas a la tabla
			for (Vacuna vacuna : vacunas) {
				table.addCell(vacuna.getNombre());
				table.addCell(String.valueOf(vacuna.getFecha()));
				table.addCell(String.valueOf(vacuna.getDosis()));
				table.addCell(String.valueOf(vacuna.getNumLote()));
			}

			// Agregar la tabla al documento
			document.add(table);

			// Cerrar el documento
			document.close();

			// Obtener el contenido del PDF en forma de byte array
			byte[] pdfContent = baos.toByteArray();

			// Configurar las cabeceras de la respuesta
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", "vacunas.pdf");

			// Devolver la respuesta con el contenido del PDF
			return ResponseEntity.ok().headers(headers).body(pdfContent);
		} catch (Exception e) {
			System.err.println("Ha ocurrido un error: " + e);
			return null;

		}
	}

	@PostMapping("/usuario/descargaCita")
	public ResponseEntity<byte[]> generarCitaPDF(Principal principal) throws IOException, DocumentException {
		try {

			User user = userRepository.findByEmail(principal.getName());
			Cita cita = citaRepo.findByUsuarioAndAsistenciaIsNull(user.getUsuario());
			Usuario usuario = user.getUsuario();

			Document document = new Document();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			PdfWriter.getInstance(document, baos);

			document.open();

			Resource resource = new ClassPathResource("images/clickhealthlogo.PNG");

			Image image = Image.getInstance(resource.getURL().toString());
			image.setAlignment(Element.ALIGN_CENTER);
			image.scaleToFit(200f, 200f);
			image.setSpacingAfter(2);
			document.add(image);

			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
			Paragraph header = new Paragraph("Cita del paciente " + usuario.getNombre() + " " + usuario.getApellidos()
					+ " con DNI " + usuario.getDni(), headerFont);
			header.setAlignment(Element.ALIGN_CENTER);
			header.setSpacingAfter(10f);
			document.add(header);

			// Crear una card para mostrar la información de vacunas
			Font cardFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
			float cardWidth = 300f;
			float cardHeight = 100f;
			float cardMargin = 10f;

			// Crear una nueva tarjeta
			PdfPTable card = new PdfPTable(2);
			card.setWidthPercentage(100);
			card.setWidths(new float[] { 1, 3 });
			card.setSpacingBefore(cardMargin);
			card.setSpacingAfter(cardMargin);

			// Agregar el nombre de la vacuna en la primera celda
			if (cita.getMedico() != null) {
				PdfPCell nameCell = new PdfPCell(new Phrase("Medico:", cardFont));
				nameCell.setBorder(Rectangle.BOX);
				card.addCell(nameCell);

				PdfPCell nameValueCell = new PdfPCell(
						new Phrase(cita.getMedico().getNombre() + " " + cita.getMedico().getApellidos(), cardFont));
				nameValueCell.setBorder(Rectangle.BOX);
				card.addCell(nameValueCell);

				// Agregar la hora de la cita en la siguiente celda
				PdfPCell roomCell = new PdfPCell(new Phrase("Sala:", cardFont));
				roomCell.setBorder(Rectangle.BOX);
				card.addCell(roomCell);

				PdfPCell roomValueCell = new PdfPCell(new Phrase(String.valueOf(cita.getMedico().getSala()), cardFont));
				roomValueCell.setBorder(Rectangle.BOX);
				card.addCell(roomValueCell);
			}

			if (cita.getEnfermero() != null) {
				PdfPCell nameCell = new PdfPCell(new Phrase("Enfermero:", cardFont));
				nameCell.setBorder(Rectangle.BOX);
				card.addCell(nameCell);

				PdfPCell nameValueCell = new PdfPCell(new Phrase(
						cita.getEnfermero().getNombre() + " " + cita.getEnfermero().getApellidos(), cardFont));
				nameValueCell.setBorder(Rectangle.BOX);
				card.addCell(nameValueCell);

				// Agregar la hora de la cita en la siguiente celda
				PdfPCell roomCell = new PdfPCell(new Phrase("Sala:", cardFont));
				roomCell.setBorder(Rectangle.BOX);
				card.addCell(roomCell);

				PdfPCell roomValueCell = new PdfPCell(
						new Phrase(String.valueOf(cita.getEnfermero().getSala()), cardFont));
				roomValueCell.setBorder(Rectangle.BOX);
				card.addCell(roomValueCell);
			}

			PdfPCell dateCell = new PdfPCell(new Phrase("Fecha:", cardFont));
			dateCell.setBorder(Rectangle.BOX);
			card.addCell(dateCell);

			PdfPCell dateValueCell = new PdfPCell(new Phrase(String.valueOf(cita.getFecha()), cardFont));
			dateValueCell.setBorder(Rectangle.BOX);
			card.addCell(dateValueCell);

			// Agregar la hora de la cita en la siguiente celda
			PdfPCell timeCell = new PdfPCell(new Phrase("Hora:", cardFont));
			timeCell.setBorder(Rectangle.BOX);
			card.addCell(timeCell);

			PdfPCell timeValueCell = new PdfPCell(new Phrase(String.valueOf(cita.getTramo().getTiempo()), cardFont));
			timeValueCell.setBorder(Rectangle.BOX);
			card.addCell(timeValueCell);

			// Agregar la confirmación de la cita en la siguiente celda
			PdfPCell confirmedCell = new PdfPCell(new Phrase("Confirmada:", cardFont));
			confirmedCell.setBorder(Rectangle.BOX);
			card.addCell(confirmedCell);

			if (cita.isConfirmada()) {
				PdfPCell confirmedValueCell = new PdfPCell(new Phrase("Si", cardFont));
				confirmedValueCell.setBorder(Rectangle.BOX);
				card.addCell(confirmedValueCell);
			} else {
				PdfPCell confirmedValueCell = new PdfPCell(new Phrase("No", cardFont));
				confirmedValueCell.setBorder(Rectangle.BOX);
				card.addCell(confirmedValueCell);
			}

			// Agregar la tarjeta al documento
			document.add(card);

			// Cerrar el documento
			document.close();

			// Obtener el contenido del PDF en forma de byte array
			byte[] pdfContent = baos.toByteArray();

			// Configurar las cabeceras de la respuesta
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", "cita.pdf");

			// Devolver la respuesta con el contenido del PDF
			return ResponseEntity.ok().headers(headers).body(pdfContent);
		} catch (Exception e) {
			System.err.println("Ha ocurrido un error: " + e);
			return null;

		}
	}

	@GetMapping("/usuario/citasUsuario")
	public String citasUsuario(Principal principal, Model model) {
		User user = userRepository.findByEmail(principal.getName());
		Cita cita = citaRepo.findByUsuarioAndAsistenciaIsNull(user.getUsuario());

		if (cita != null) {
			if (cita.isConfirmada()) {
				model.addAttribute("confirmada", "Confirmada");
				model.addAttribute("cita", cita);
			} else {
				model.addAttribute("confirmada", "Por confirmar");
				model.addAttribute("cita", cita);

				LocalDate fecha = LocalDate.now();
				Date fechaActual = Date.valueOf(fecha);

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(cita.getFecha());

				calendar.add(Calendar.DAY_OF_MONTH, -1);

				java.util.Date fechaRestada = calendar.getTime();
				java.sql.Date fechaRestadaSql = new java.sql.Date(fechaRestada.getTime());

				if (fechaActual.equals(fechaRestadaSql)) {
					model.addAttribute("plazoAbierto", true);

				}

			}

			if (cita.getEnfermero() != null) {
				model.addAttribute("tipo", cita.getEnfermero());
				model.addAttribute("sanitario", "Enfermero");

			}

			if (cita.getMedico() != null) {
				model.addAttribute("tipo", cita.getMedico());
				model.addAttribute("sanitario", "Medico");
			}

			return "CitasUsuario";

		} else {
			return "CitasUsuario";
		}

	}

	@GetMapping("/usuario/confirmaAsistencia/{id}")
	public String confirmaAsistencia(Principal principal, RedirectAttributes redirectAttrs, @PathVariable Long id) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Cita> existeCita = citaRepo.findById(id);

		if (existeCita.isPresent()) {
			if (existeCita.get().getUsuario().getId() != user.getUsuario().getId()) {
				redirectAttrs.addFlashAttribute("error", "Cita no encontrada");
				return "redirect:/usuario/citasUsuario";
			} else {
				Cita cita = existeCita.get();
				cita.setConfirmada(true);
				citaRepo.save(cita);
				redirectAttrs.addFlashAttribute("exito", "Cita confirmada con exito");
				return "redirect:/usuario/citasUsuario";
			}
		} else {
			redirectAttrs.addFlashAttribute("error", "Cita no encontrada");
			return "redirect:/usuario/citasUsuario";
		}

	}

	@GetMapping("/usuario/cancelaCita/{id}")
	public String cancelaCita(Principal principal, RedirectAttributes redirectAttrs, @PathVariable Long id) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Cita> existeCita = citaRepo.findById(id);

		if (existeCita.isPresent()) {
			if (existeCita.get().getUsuario().getId() != user.getUsuario().getId()) {
				redirectAttrs.addFlashAttribute("error", "Cita no encontrada");
				return "redirect:/usuario/citasUsuario";
			} else {
				Cita cita = existeCita.get();
				citaRepo.delete(cita);
				redirectAttrs.addFlashAttribute("exito", "Cita cancelada con exito");
				return "redirect:/usuario/citasUsuario";
			}
		} else {
			redirectAttrs.addFlashAttribute("error", "Cita no encontrada");
			return "redirect:/usuario/citasUsuario";
		}

	}

	@PostMapping("/usuario/nuevaSolicitudBaja")
	public String nuevaSolicitudBaja(@Valid @ModelAttribute("solicitud") SolicitudDto solicitud, Principal principal,
			Model model, BindingResult result, @Param("seleccionado") String seleccionado) {
		User user = userRepository.findByEmail(principal.getName());
		Solicitud existeSolicitud = solicitudRepo.findByUsuarioAndTituloAndEstadoIsNull(user.getUsuario(),
				"baja usuario");
		model.addAttribute("seleccionado", seleccionado);

		if (existeSolicitud != null) {
			result.rejectValue("descripcion", null, "Ya tienes una solicitud pendiente");
		}

		if (solicitud.getDni().isEmpty()) {
			result.rejectValue("dni", null, "Ingrese su dni");
		} else if (!solicitud.getDni().equalsIgnoreCase(user.getUsuario().getDni())) {
			result.rejectValue("dni", null, "Dni incorrecto");
		}

		if (solicitud.getDescripcion().isEmpty()) {
			result.rejectValue("descripcion", null, "Al menos debe tener una descripcion");
		}

		if (solicitud.getDescripcion().length() > 200) {
			result.rejectValue("descripcion", null, "Resuma un poco la descripcion por favor");
		}

		if (result.hasErrors()) {
			model.addAttribute("solicitud", solicitud);
			return "NuevaSolicitud";
		}

		model.addAttribute("exito", "Solicitud enviada");

		usuarioServicioI.guardaSolicitud(user.getUsuario(), solicitud, seleccionado);

		return "NuevaSolicitud";
	}

	private List<Columna> getCalendario() {
		List<Integer> numeros = new ArrayList();
		int margen = 0;
		LocalDate fechaActual = LocalDate.now();
		// int mesActual = fechaActual.getMonthValue();

		LocalDate primerDiaMesActual = fechaActual.withDayOfMonth(1);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String diaSemanaPrimerDiaMesActual = primerDiaMesActual.getDayOfWeek().toString();

		Calendar cal = Calendar.getInstance();
		int ultimoDia = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		switch (diaSemanaPrimerDiaMesActual.toString()) {
		case "MONDAY":
			margen = 0;
			break;
		case "TUESDAY":
			margen = 1;
			break;
		case "WEDNESDAY":
			margen = 2;
			break;
		case "THURSDAY":
			margen = 3;
			break;
		case "FRIDAY":
			margen = 4;
			break;
		case "SATURDAY":
			margen = 5;
			break;
		case "SUNDAY":
			margen = 6;
			break;
		}

		for (int i = 0; i < margen; i++) {
			numeros.add(0);
		}

		for (int i = 1; i <= ultimoDia; i++) {
			numeros.add(i);
		}

		List<Columna> columnas = new ArrayList();

		for (int i = 0; i < numeros.size(); i += 7) {
			Columna columna = new Columna();
			if (i < numeros.size()) {
				columna.setNumUno(numeros.get(i));
			}
			if (i + 1 < numeros.size()) {
				columna.setNumDos(numeros.get(i + 1));
			}
			if (i + 2 < numeros.size()) {
				columna.setNumTres(numeros.get(i + 2));
			}

			if (i + 3 < numeros.size()) {
				columna.setNumCuatro(numeros.get(i + 3));
			}

			if (i + 4 < numeros.size()) {
				columna.setNumCinco(numeros.get(i + 4));
			}
			if (i + 5 < numeros.size()) {
				columna.setNumSeis(numeros.get(i + 5));
			}

			if (i + 6 < numeros.size()) {
				columna.setNumSiete(numeros.get(i + 6));
			}
			columnas.add(columna);

		}

		return columnas;
	}

}
