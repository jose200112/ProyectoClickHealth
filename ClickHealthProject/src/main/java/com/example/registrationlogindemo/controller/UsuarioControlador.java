package com.example.registrationlogindemo.controller;

import java.security.Principal;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clickhealth.object.Columna;
import com.example.registrationlogindemo.dto.VacunaDto;
import com.example.registrationlogindemo.entity.Tramo;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;
import com.example.registrationlogindemo.entity.Vacuna;
import com.example.registrationlogindemo.repository.TramoRepositorio;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.service.UsuarioServicioI;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsuarioControlador {
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	TramoRepositorio tramoRepo;
	
	@GetMapping("/usuario/inicioUsuario")
	public String inicioUsuario() {
		return "InicioUsuario";
	}
	
	@GetMapping("/usuario/reservaCita")
	  public String index(Model model) {
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
	    return "ReservaCita";
	  }
	
	@GetMapping("/usuario/horasDisponibles")
	public String getHorasDisponibles(@RequestParam("dia") Integer dia, Principal principal, Model model, HttpSession session) {
		User user = userRepository.findByEmail(principal.getName());
		Usuario usuario = user.getUsuario();
        LocalDate fecha = LocalDate.now().withDayOfMonth(dia);
        System.out.println(fecha);
		List<Tramo> tramos = tramoRepo.findTramosDisponibles(usuario.getMedico().getId());
		model.addAttribute("tramos", tramos);
		System.out.println(dia);
		model.addAttribute("diaSeleccionado", dia);
	    return "HorasDisponibles";
	}
	
	@PostMapping("/usuario/creaCita")
	public String creaCita(@RequestParam("tramoSeleccionado") String tramoSeleccionado, @RequestParam("diaSeleccionado") Integer dia) {
		System.out.println(tramoSeleccionado);
		System.out.println(dia);
		return "redirect:/usuario/reservaCita?success";
	}
	
	@GetMapping("/usuario/vacunas")
	public String getVacunas(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		List<Vacuna> vacunas = user.getUsuario().getVacunas();
		model.addAttribute("vacunas", vacunas);
		return "VacunasRegistradas";
	}
	
	@GetMapping("/usuario/solicitud")
	public String getNuevaSolicitud() {
		return "NuevaSolicitud";
	}
	
	
	private List<Columna> getCalendario(){
		List<Integer> numeros = new ArrayList();
    	int margen = 0;
        LocalDate fechaActual = LocalDate.now();
        //int mesActual = fechaActual.getMonthValue();

        LocalDate primerDiaMesActual = fechaActual.withDayOfMonth(1);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String diaSemanaPrimerDiaMesActual = primerDiaMesActual.getDayOfWeek().toString();
        
        Calendar cal = Calendar.getInstance(); 
        int ultimoDia = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                
        switch(diaSemanaPrimerDiaMesActual.toString()) {
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
        
        for(int i = 0; i < margen; i++) {
        	numeros.add(0);
        }
        
        for(int i = 1; i <= ultimoDia; i++) {
        	numeros.add(i);
        }
        
        List<Columna> columnas = new ArrayList();
        
        for (int i = 0; i < numeros.size(); i += 7) {
        	Columna columna = new Columna();
        	if(i < numeros.size()) {
        		columna.setNumUno(numeros.get(i));
        	}
        	if(i+1 < numeros.size()) {
        		columna.setNumDos(numeros.get(i+1));
        	}
        	if(i+2 < numeros.size()) {
        		columna.setNumTres(numeros.get(i+2));
        	}
        	
        	if(i+3 < numeros.size()) {
        		columna.setNumCuatro(numeros.get(i+3));
        	}
        	
        	if(i+4 < numeros.size()) {
        		columna.setNumCinco(numeros.get(i+4));
        	}
        	if(i+5 < numeros.size()) {
        		columna.setNumSeis(numeros.get(i+5));
        	}
        	
        	if(i+6<numeros.size()) {
        		columna.setNumSiete(numeros.get(i+6));
        	}
            columnas.add(columna);


        }
        
        return columnas;
	}
	
	
}
