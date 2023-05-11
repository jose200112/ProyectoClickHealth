package com.example.registrationlogindemo.controller;

import java.security.Principal;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.registrationlogindemo.dto.AlergiaDto;
import com.example.registrationlogindemo.dto.ObservacionDto;
import com.example.registrationlogindemo.entity.Alergia;
import com.example.registrationlogindemo.entity.Cita;
import com.example.registrationlogindemo.entity.Observacion;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;
import com.example.registrationlogindemo.repository.CitaRepositorio;
import com.example.registrationlogindemo.repository.ObservacionRepositorio;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.repository.UsuarioRepositorio;
import com.example.registrationlogindemo.service.MedicoServicioI;
import com.example.registrationlogindemo.service.UsuarioServicioI;

import jakarta.validation.Valid;


@Controller
public class MedicoControlador {
	
	@Autowired
	CitaRepositorio citaRepo;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MedicoServicioI medicoServicioI;
	
	@Autowired
	UsuarioServicioI usuarioServicioI;
	
	@Autowired
	UsuarioRepositorio usuarioRepo;
	
	@Autowired
	ObservacionRepositorio observacionRepo;
	
	@GetMapping("/medico/inicioMedico")
	public String inicioMedico() {
		return "InicioMedico";
	}
	
	@GetMapping("/medico/consultaCitas")
	public String consultaCitasMedico(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
        Calendar calendar = Calendar.getInstance();
        java.util.Date fechaActual = calendar.getTime();
        Date fechaSql = new Date(fechaActual.getTime());
        
        List<Cita> citas = citaRepo.findByMedicoAndFechaAndAsistenciaIsNullAndConfirmadaIsTrue(user.getMedico(), fechaSql);
        
        model.addAttribute("citas",citas);
        
		return "ConsultaCitasMedico";
	}
	
	@GetMapping("/medico/confirmaAsistencia/{id}")
	public String confirmaAsistencia(@PathVariable Long id) {
		Optional<Cita> cita = citaRepo.findById(id);
		Cita existeCita = cita.get();
		
		medicoServicioI.confirmaAsistencia(existeCita);
		
		return "redirect:/medico/consultaCitas";
	}
	
	@GetMapping("/medico/deniegaAsistencia/{id}")
	public String deniegaAsistencia(@PathVariable Long id) {
		Optional<Cita> cita = citaRepo.findById(id);
		Cita existeCita = cita.get();
		
		medicoServicioI.deniegaAsistencia(existeCita);
		
		return "redirect:/medico/consultaCitas";
	}
	
	
	@GetMapping("/medico/buscadorUsuarioObservacion")
	public String actualizaBuscadorUsuarioObservacion(Model model, @Param("nombre") String nombre,Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		List<Usuario> usuarios = usuarioServicioI.buscarUsuariosMedico(nombre, user.getMedico());
		model.addAttribute("nombre",nombre);
		model.addAttribute("usuarios",usuarios);
		return "BuscadorUsuarioObservacion";
	}
	
	@GetMapping("/medico/registroObservacion/{id}")
	public String getRegistroObservacion(Model model, @PathVariable Long id,Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> usuario = usuarioRepo.findById(id);
		
		if(usuario.isPresent()) {
			if(usuario.get().getMedico().getId() != user.getMedico().getId()) {
				return "redirect:/medico/buscadorUsuarioObservacion?error";
			} else {
				ObservacionDto observacion = new ObservacionDto();
				model.addAttribute("observacion", observacion);
				model.addAttribute("usuario", usuario.get());
				return "RegistroObservacion";
			}

		} else {
			return "redirect:/medico/buscadorUsuarioObservacion?error";
		}
	}
	
	@PostMapping("/medico/guardaObservacion/{id}")
	public String registraObservacion(@Valid @ModelAttribute("observacion") ObservacionDto observacion, BindingResult result,
			Model model, Principal principal, @PathVariable Long id) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		Usuario usuarioAsignado = null;
		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getMedico().getId() != user.getMedico().getId()) {
				return "redirect:/medico/buscadorUsuarioObservacion?error";
			} else {
				usuarioAsignado = existeUsuario.get();
				observacion.setUsuario(usuarioAsignado);
				observacion.setMedico(user.getMedico());
			}
		} else {
			return "redirect:/medico/buscadorUsuarioObservacion?error";
		}

		if(observacion.getDescripcion().length() > 200) {
			result.rejectValue("descripcion", null,"Resuma un poco la descripcion por favor");
		}
		
		if(observacion.getDiagnostico().length() > 200) {
			result.rejectValue("diagnostico", null,"Resuma un poco el diagnostico por favor");
		}
		
		if(observacion.getSintomas().length() > 200) {
			result.rejectValue("sintomas", null,"Resuma un poco los sintomas por favor");
		}
		
		if (result.hasErrors()) {
			model.addAttribute("observacion", observacion);
			model.addAttribute("usuario",existeUsuario.get());
			model.addAttribute("result", result);
			return "RegistroObservacion";
		}
		
		medicoServicioI.guardaNuevaObservacion(observacion);
				

		return "redirect:/medico/registroObservacion/{id}?success";
	}
	
	@GetMapping("/medico/modificaObservacionBuscador/{id}")
	public String modificaObservacion(@PathVariable Long id,Model model,  Principal principal,@Param("nombre") String nombre) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getMedico().getId() != user.getMedico().getId()) {
				return "redirect:/medico/buscadorUsuarioAlergia?error";
			} else {
				List<Observacion> observaciones = usuarioServicioI.buscarObservacionesUsuario(existeUsuario.get());
				model.addAttribute("usuario", existeUsuario.get());
				model.addAttribute("observaciones", observaciones);
				return "ModificaObservacion";
			}
		} else {
			return "redirect:/medico/buscadorUsuarioObservacion?error";
		}
		
	}
	
	@GetMapping("/medico/registroActualizaObservacion/{id}/{idObservacion}")
	public String getRegistroActualizaObservaciones(Model model, @PathVariable Long id,@PathVariable Long idObservacion,Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		Optional<Observacion> existeObservacion = observacionRepo.findById(idObservacion);		

		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getMedico().getId() != user.getMedico().getId()) {
				return "redirect:/medico/buscadorUsuarioObservacion?error";
			} else {
				if(existeObservacion.isPresent()) {
					model.addAttribute("usuario", existeUsuario.get());
					model.addAttribute("observacion", existeObservacion.get().toDto());
					return "RegistroActualizaObservacion";
				} else {
					return "redirect:/medico/modificaObservacionBuscador/{id}?error";
				}
			}
		} else {
			return "redirect:/medico/buscadorUsuarioObservacion?error";
		}
	}
	
	@PostMapping("/medico/actualizaObservacion/{id}/{idObservacion}")
	public String actualizaAlergia(@PathVariable Long id,@PathVariable Long idObservacion,Model model,Principal principal, @Valid @ModelAttribute("observacion") ObservacionDto observacion, BindingResult result) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		Optional<Observacion> existeObservacion = observacionRepo.findById(idObservacion);
		

		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getMedico().getId() != user.getMedico().getId()) {
				return "redirect:/medico/buscadorUsuarioObservacion?error";
			} else {
				if(existeObservacion.isPresent()) {
					observacion.setUsuario(existeUsuario.get());
					observacion.setMedico(user.getMedico());
					observacion.setId(idObservacion);
					
				} else {
					return "redirect:/medico/modificaObservacionBuscador/{id}?error";
				}
			}
		} else {
			return "redirect:/medico/buscadorUsuarioAlergia?error";
		}
		
		
		if(observacion.getDiagnostico().length() > 200) {
			result.rejectValue("diagnostico", null,"Resuma un poco el diagnostico por favor");
		}
		
		if(observacion.getSintomas().length() > 200) {
			result.rejectValue("sintomas", null,"Resuma un poco los sintomas por favor");
		}
		
		if(observacion.getDescripcion().length() > 200) {
			result.rejectValue("descripcion", null,"Resuma un poco la descripcion por favor");
		}
		
		if (result.hasErrors()) {
			model.addAttribute("observacion", observacion);
			model.addAttribute("usuario",existeUsuario.get());
			model.addAttribute("result", result);
			return "RegistroActualizaAlergia";
		}
		
		medicoServicioI.guardaObservacion(observacion);
		
		return "redirect:/medico/modificaObservacionBuscador/{id}?success";
	}
	
	@GetMapping("/medico/borraObservacion/{id}/{idObservacion}")
	public String borraAlergia(@PathVariable Long id,@PathVariable Long idObservacion,Model model,Principal principal,RedirectAttributes redirectAttrs) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		Optional<Observacion> existeObservacion = observacionRepo.findById(idObservacion);		

		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getMedico().getId() != user.getMedico().getId()) {
				return "redirect:/medico/buscadorUsuarioAlergia?error";
			} else {
				if(existeObservacion.isPresent()) {
					observacionRepo.deleteById(idObservacion);
					String exito = "La observacion ha sido borrada";
					model.addAttribute("usuario", existeUsuario.get());
					redirectAttrs.addFlashAttribute("exito",exito);
					return "redirect:/medico/modificaObservacionBuscador/{id}";
				} else {
					return "redirect:/medico/modificaObservacionBuscador/{id}?error";
				}
			}
		} else {
			return "redirect:/medico/buscadorUsuarioObservacion?error";
		}
		
	}
}
