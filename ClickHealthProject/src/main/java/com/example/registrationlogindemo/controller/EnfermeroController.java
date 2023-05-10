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
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.registrationlogindemo.dto.AlergiaDto;
import com.example.registrationlogindemo.dto.VacunaDto;
import com.example.registrationlogindemo.entity.Alergia;
import com.example.registrationlogindemo.entity.Cita;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;
import com.example.registrationlogindemo.entity.Vacuna;
import com.example.registrationlogindemo.repository.AlergiaRepositorio;
import com.example.registrationlogindemo.repository.CitaRepositorio;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.repository.UsuarioRepositorio;
import com.example.registrationlogindemo.repository.VacunaRepositorio;
import com.example.registrationlogindemo.service.EnfermeroServicioI;
import com.example.registrationlogindemo.service.UsuarioServicioI;
import com.example.registrationlogindemo.service.impl.EnfermeroServicioImpl;

import jakarta.validation.Valid;

@Controller
public class EnfermeroController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	EnfermeroServicioI enfermeroServicio;
	
	@Autowired
	UsuarioRepositorio usuarioRepo;
	
	@Autowired
	CitaRepositorio citaRepo;
	
	@Autowired
	UsuarioServicioI usuarioServicioI;
	
	@Autowired
	VacunaRepositorio vacunaRepo;
	
	@Autowired
	AlergiaRepositorio alergiaRepo;
	
	@GetMapping("/enfermero/inicioEnfermero")
	public String inicioEnfermero() {
		return "InicioEnfermero";
	}

	@GetMapping("/enfermero/buscadorUsuarioVacuna")
	public String actualizaBuscadorUsuario(Model model, @Param("nombre") String nombre,Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		List<Usuario> usuarios = usuarioServicioI.buscarUsuariosEnfermero(nombre, user.getEnfermero());
		model.addAttribute("nombre",nombre);
		model.addAttribute("usuarios",usuarios);
		return "BuscadorUsuarioVacuna";
	}
	
	@GetMapping("/enfermero/registroVacuna/{id}")
	public String getRegistroVacunas(Model model, @PathVariable Long id,Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> usuario = usuarioRepo.findById(id);
		
		if(usuario.isPresent()) {
			if(usuario.get().getEnfermero().getId() != user.getEnfermero().getId()) {
				return "redirect:/enfermero/buscadorUsuarioVacuna?error";
			} else {
				VacunaDto vacuna = new VacunaDto();
				model.addAttribute("vacuna", vacuna);
				model.addAttribute("usuario", usuario.get());
				return "RegistroVacuna";
			}

		} else {
			return "redirect:/enfermero/buscadorUsuarioVacuna?error";
		}
	}
	
	@PostMapping("/enfermero/guardaVacuna/{id}")
	public String registraVacuna(@Valid @ModelAttribute("vacuna") VacunaDto vacuna, BindingResult result,
			Model model, Principal principal, @PathVariable Long id) {
		User user = userRepository.findByEmail(principal.getName());
		vacuna.setUser(user);
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		Usuario usuarioAsignado = null;
		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getEnfermero().getId() != user.getEnfermero().getId()) {
				return "redirect:/enfermero/buscadorUsuarioVacuna?error";
			} else {
				usuarioAsignado = existeUsuario.get();
				vacuna.setUsuario(usuarioAsignado);
			}
		} else {
			return "redirect:/enfermero/buscadorUsuarioVacuna?error";
		}
		
		if(vacuna.getFecha().isBlank()) {
			result.rejectValue("fecha", null, "Ingrese la fecha de inoculacion");
		}
		
		if(vacuna.getDosis() > 3 || vacuna.getDosis() <= 0) {
			result.rejectValue("dosis", null, "Ingrese un numero de dosis valido");
		}
		
		if(vacuna.getNumLote() <= 0 || vacuna.getNumLote() > 100000000) {
			result.rejectValue("numLote",null, "Ingrese un numero de lote valido");
		}
		
		if (result.hasErrors()) {
			model.addAttribute("vacuna", vacuna);
			model.addAttribute("usuario",existeUsuario.get());
			model.addAttribute("result", result);
			return "RegistroVacuna";
		}
		
		enfermeroServicio.saveVacuna(vacuna);
		

		return "redirect:/enfermero/registroVacuna/{id}?success";
	}
	
	@GetMapping("/enfermero/modificaVacunaBuscador/{id}")
	public String modificaVacuna(@PathVariable Long id,Model model,  Principal principal,@Param("nombre") String nombre) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getEnfermero().getId() != user.getEnfermero().getId()) {
				return "redirect:/enfermero/buscadorUsuarioVacuna?error";
			} else {
				List<Vacuna> vacunas = usuarioServicioI.buscarVacunasUsuario(existeUsuario.get());
				model.addAttribute("usuario", existeUsuario.get());
				model.addAttribute("vacunas", vacunas);
				return "ModificaVacuna";
			}
		} else {
			return "redirect:/enfermero/buscadorUsuarioVacuna?error";
		}
		
	}
	
	@GetMapping("/enfermero/registroActualizaVacuna/{id}/{idVacuna}")
	public String getRegistroActualizaVacunas(Model model, @PathVariable Long id,@PathVariable Long idVacuna,Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		Optional<Vacuna> existeVacuna = vacunaRepo.findById(idVacuna);
		

		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getEnfermero().getId() != user.getEnfermero().getId()) {
				return "redirect:/enfermero/buscadorUsuarioVacuna?error";
			} else {
				if(existeVacuna.isPresent()) {
					model.addAttribute("usuario", existeUsuario.get());
					model.addAttribute("vacuna", existeVacuna.get().toDto());
					return "RegistroActualizaVacuna";
				} else {
					return "redirect:/enfermero/modificaVacunaBuscador/{id}?error";
				}
			}
		} else {
			return "redirect:/enfermero/buscadorUsuarioVacuna?error";
		}
	}
	
	@PostMapping("/enfermero/actualizaVacuna/{id}/{idVacuna}")
	public String actualizaVacuna(@PathVariable Long id,@PathVariable Long idVacuna,Model model,Principal principal, @Valid @ModelAttribute("vacuna") VacunaDto vacuna, BindingResult result) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		Optional<Vacuna> existeVacuna = vacunaRepo.findById(idVacuna);
		

		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getEnfermero().getId() != user.getEnfermero().getId()) {
				return "redirect:/enfermero/buscadorUsuarioVacuna?error";
			} else {
				if(existeVacuna.isPresent()) {
					vacuna.setUsuario(existeUsuario.get());
					vacuna.setEnfermero(user.getEnfermero());
					vacuna.setId(idVacuna);
					
				} else {
					return "redirect:/enfermero/modificaVacunaBuscador/{id}?error";
				}
			}
		} else {
			return "redirect:/enfermero/buscadorUsuarioVacuna?error";
		}
		
		
		if(vacuna.getFecha().isBlank()) {
			result.rejectValue("fecha", null, "Ingrese la fecha de inoculacion");
		}
		
		if(vacuna.getDosis() > 3 || vacuna.getDosis() <= 0) {
			result.rejectValue("dosis", null, "Ingrese un numero de dosis valido");
		}
		
		if(vacuna.getNumLote() <= 0 || vacuna.getNumLote() > 100000000) {
			result.rejectValue("numLote",null, "Ingrese un numero de lote valido");
		}
		
		if (result.hasErrors()) {
			model.addAttribute("vacuna", vacuna);
			model.addAttribute("usuario",existeUsuario.get());
			model.addAttribute("result", result);
			return "RegistroVacuna";
		}
		
		enfermeroServicio.actualizaVacuna(vacuna);
		
		return "redirect:/enfermero/modificaVacunaBuscador/{id}?success";
	}
	
	@GetMapping("/enfermero/borraVacuna/{id}/{idVacuna}")
	public String borraVacuna(@PathVariable Long id,@PathVariable Long idVacuna,Model model,Principal principal,RedirectAttributes redirectAttrs) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> existeUsuario = usuarioRepo.findById(id);
		Optional<Vacuna> existeVacuna = vacunaRepo.findById(idVacuna);
		

		
		if(existeUsuario.isPresent()) {
			if(existeUsuario.get().getEnfermero().getId() != user.getEnfermero().getId()) {
				return "redirect:/enfermero/buscadorUsuarioVacuna?error";
			} else {
				if(existeVacuna.isPresent()) {
					vacunaRepo.deleteById(idVacuna);
					String exito = "La vacuna ha sido borrada";
					model.addAttribute("usuario", existeUsuario.get());
					redirectAttrs.addFlashAttribute("exito",exito);
					return "redirect:/enfermero/modificaVacunaBuscador/{id}";
				} else {
					return "redirect:/enfermero/modificaVacunaBuscador/{id}?error";
				}
			}
		} else {
			return "redirect:/enfermero/buscadorUsuarioVacuna?error";
		}
		
	}
	
	@GetMapping("/enfermero/buscadorUsuarioAlergia")
	public String actualizaBuscadorUsuarioAlergia(Model model, @Param("nombre") String nombre,Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		List<Usuario> usuarios = usuarioServicioI.buscarUsuariosEnfermero(nombre, user.getEnfermero());
		model.addAttribute("nombre",nombre);
		model.addAttribute("usuarios",usuarios);
		return "BuscadorUsuarioAlergia";
	}
	
	@GetMapping("/enfermero/registroAlergia/{id}")
	public String getRegistroAlergias(Model model, @PathVariable Long id,Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		Optional<Usuario> usuario = usuarioRepo.findById(id);
		
		if(usuario.isPresent()) {
			if(usuario.get().getEnfermero().getId() != user.getEnfermero().getId()) {
				return "redirect:/enfermero/buscadorUsuarioAlergia?error";
			} else {
				AlergiaDto alergia = new AlergiaDto();
				model.addAttribute("alergia", alergia);
				model.addAttribute("usuario", usuario.get());
				return "RegistroAlergia";
			}

		} else {
			return "redirect:/enfermero/buscadorUsuarioAlergia?error";
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
	
	
	@GetMapping("/enfermero/consultaCitas")
	public String consultaCitasMedico(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
        Calendar calendar = Calendar.getInstance();
        java.util.Date fechaActual = calendar.getTime();
        Date fechaSql = new Date(fechaActual.getTime());
        
        List<Cita> citas = citaRepo.findByEnfermeroAndFechaAndAsistenciaIsNullAndConfirmadaIsTrue(user.getEnfermero(), fechaSql);
        
        model.addAttribute("citas",citas);
        
		return "ConsultaCitasEnfermero";
	}
	
	@GetMapping("/enfermero/confirmaAsistencia/{id}")
	public String confirmaAsistencia(@PathVariable Long id) {
		Optional<Cita> cita = citaRepo.findById(id);
		Cita existeCita = cita.get();
		
		enfermeroServicio.confirmaAsistencia(existeCita);
		
		return "redirect:/enfermero/consultaCitas";
	}
	
	@GetMapping("/enfermero/deniegaAsistencia/{id}")
	public String deniegaAsistencia(@PathVariable Long id) {
		Optional<Cita> cita = citaRepo.findById(id);
		Cita existeCita = cita.get();
		
		enfermeroServicio.deniegaAsistencia(existeCita);
		
		return "redirect:/enfermero/consultaCitas";
	}
	
}
