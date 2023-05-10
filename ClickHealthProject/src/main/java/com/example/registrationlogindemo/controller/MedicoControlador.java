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
import com.example.registrationlogindemo.entity.Alergia;
import com.example.registrationlogindemo.entity.Cita;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;
import com.example.registrationlogindemo.repository.CitaRepositorio;
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
	public String actualizaBuscadorUsuarioAlergia(Model model, @Param("nombre") String nombre,Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		List<Usuario> usuarios = usuarioServicioI.buscarUsuariosMedico(nombre, user.getMedico());
		model.addAttribute("nombre",nombre);
		model.addAttribute("usuarios",usuarios);
		return "BuscadorUsuarioObservacion";
	}
	
	@GetMapping("/medico/registroObservacion/{id}")
	public String getRegistroAlergias(Model model, @PathVariable Long id,Principal principal) {
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
	
	@PostMapping("/enfermero/guardaAlergia/{id}")
	public String registraAlergia(@Valid @ModelAttribute("alergia") AlergiaDto alergia, BindingResult result,
			Model model, Principal principal, @PathVariable Long id) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		Usuario usuarioAsignado = null;
		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getEnfermero().getId() != user.getEnfermero().getId()) {
				return "redirect:/enfermero/buscadorUsuarioAlergia?error";
			} else {
				usuarioAsignado = existeUsuario.get();
				alergia.setUsuario(usuarioAsignado);
				alergia.setEnfermero(user.getEnfermero());
			}
		} else {
			return "redirect:/enfermero/buscadorUsuarioAlergia?error";
		}
		
		if (alergia.getDescripcion().isBlank()) {
			result.rejectValue("descripcion", null, "Escriba al menos una descripcion");
		}

		if(alergia.getDescripcion().length() > 200) {
			result.rejectValue("descripcion", null,"Resuma un poco la descripcion por favor");
		}
		
		if (result.hasErrors()) {
			model.addAttribute("alergia", alergia);
			model.addAttribute("usuario",existeUsuario.get());
			model.addAttribute("result", result);
			return "RegistroAlergia";
		}
		
		enfermeroServicio.guardaNuevaAlergia(alergia);
		

		return "redirect:/enfermero/registroAlergia/{id}?success";
	}
	
	@GetMapping("/enfermero/modificaAlergiaBuscador/{id}")
	public String modificaAlergia(@PathVariable Long id,Model model,  Principal principal,@Param("nombre") String nombre) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getEnfermero().getId() != user.getEnfermero().getId()) {
				return "redirect:/enfermero/buscadorUsuarioAlergia?error";
			} else {
				List<Alergia> alergias = usuarioServicioI.buscarAlergiasUsuario(existeUsuario.get());
				model.addAttribute("usuario", existeUsuario.get());
				model.addAttribute("alergias", alergias);
				return "ModificaAlergia";
			}
		} else {
			return "redirect:/enfermero/buscadorUsuarioAlergia?error";
		}
		
	}
	
	@GetMapping("/enfermero/registroActualizaAlergia/{id}/{idAlergia}")
	public String getRegistroActualizaAlergias(Model model, @PathVariable Long id,@PathVariable Long idAlergia,Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		Optional<Alergia> existeAlergia = alergiaRepo.findById(idAlergia);		

		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getEnfermero().getId() != user.getEnfermero().getId()) {
				return "redirect:/enfermero/buscadorUsuarioAlergia?error";
			} else {
				if(existeAlergia.isPresent()) {
					model.addAttribute("usuario", existeUsuario.get());
					model.addAttribute("alergia", existeAlergia.get().toDto());
					return "RegistroActualizaAlergia";
				} else {
					return "redirect:/enfermero/modificaAlergiaBuscador/{id}?error";
				}
			}
		} else {
			return "redirect:/enfermero/buscadorUsuarioAlergia?error";
		}
	}
	
	@PostMapping("/enfermero/actualizaAlergia/{id}/{idAlergia}")
	public String actualizaAlergia(@PathVariable Long id,@PathVariable Long idAlergia,Model model,Principal principal, @Valid @ModelAttribute("alergia") AlergiaDto alergia, BindingResult result) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		Optional<Alergia> existeAlergia = alergiaRepo.findById(idAlergia);
		

		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getEnfermero().getId() != user.getEnfermero().getId()) {
				return "redirect:/enfermero/buscadorUsuarioAlergia?error";
			} else {
				if(existeAlergia.isPresent()) {
					alergia.setUsuario(existeUsuario.get());
					alergia.setEnfermero(user.getEnfermero());
					alergia.setId(idAlergia);
					
				} else {
					return "redirect:/enfermero/modificaAlergiaBuscador/{id}?error";
				}
			}
		} else {
			return "redirect:/enfermero/buscadorUsuarioAlergia?error";
		}
		
		
		if (alergia.getDescripcion().isBlank()) {
			result.rejectValue("descripcion", null, "Escriba al menos una descripcion");
		}
		
		if(alergia.getDescripcion().length() > 200) {
			result.rejectValue("descripcion", null,"Resuma un poco la descripcion por favor");
		}
		
		if (result.hasErrors()) {
			model.addAttribute("alergia", alergia);
			model.addAttribute("usuario",existeUsuario.get());
			model.addAttribute("result", result);
			return "RegistroActualizaAlergia";
		}
		
		enfermeroServicio.guardaAlergia(alergia);
		
		return "redirect:/enfermero/modificaAlergiaBuscador/{id}?success";
	}
	
	@GetMapping("/enfermero/borraAlergia/{id}/{idAlergia}")
	public String borraAlergia(@PathVariable Long id,@PathVariable Long idAlergia,Model model,Principal principal,RedirectAttributes redirectAttrs) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		Optional<Alergia> existeAlergia = alergiaRepo.findById(idAlergia);		

		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getEnfermero().getId() != user.getEnfermero().getId()) {
				return "redirect:/enfermero/buscadorUsuarioAlergia?error";
			} else {
				if(existeAlergia.isPresent()) {
					alergiaRepo.deleteById(idAlergia);
					String exito = "La alergia ha sido borrada";
					model.addAttribute("usuario", existeUsuario.get());
					redirectAttrs.addFlashAttribute("exito",exito);
					return "redirect:/enfermero/modificaAlergiaBuscador/{id}";
				} else {
					return "redirect:/enfermero/modificaAlergiaBuscador/{id}?error";
				}
			}
		} else {
			return "redirect:/enfermero/buscadorUsuarioAlergia?error";
		}
		
	}
}
