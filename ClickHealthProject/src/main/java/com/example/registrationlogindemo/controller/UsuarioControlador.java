package com.example.registrationlogindemo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.clickhealth.object.Columna;

public class UsuarioControlador {
	
	@GetMapping("/register/citas")
	  public String index(Model model) {
	    List<Columna> columna = getCalendario();
	    model.addAttribute("columna", columna);
	    System.out.println(columna);
	    return "calendar";
	  }
	
	private List<Columna> getCalendario(){
		List<Integer> numeros = new ArrayList();
    	int margen = 0;
        LocalDate fechaActual = LocalDate.now();
        int mesActual = fechaActual.getMonthValue();

        LocalDate primerDiaMesActual = fechaActual.withDayOfMonth(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String diaSemanaPrimerDiaMesActual = primerDiaMesActual.getDayOfWeek().toString();
        
        Calendar cal = Calendar.getInstance(); // Obtenemos una instancia de Calendar
        int ultimoDia = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        System.out.println(diaSemanaPrimerDiaMesActual);
        
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
        
        for(int i = 0; i <= margen; i++) {
        	numeros.add(null);
        }
        
        for(int i = 1; i <= ultimoDia; i++) {
        	numeros.add(i);
        }
        
        List<Columna> columnas = new ArrayList();
        
        for (int i = 0; i < numeros.size(); i += 7) {
        	Columna columna = new Columna();
        	if(i < numeros.size()) {
        		columna.setNumUno(numeros.get(i)+"");
        	}
        	if(i+1 < numeros.size()) {
        		columna.setNumDos(numeros.get(i+1)+"");
        	}
        	if(i+2 < numeros.size()) {
        		columna.setNumTres(numeros.get(i+2)+"");
        	}
        	
        	if(i+3 < numeros.size()) {
        		columna.setNumCuatro(numeros.get(i+3)+"");
        	}
        	
        	if(i+4 < numeros.size()) {
        		columna.setNumCinco(numeros.get(i+4)+"");
        	}
        	if(i+5 < numeros.size()) {
        		columna.setNumSeis(numeros.get(i+5)+"");
        	}
        	
        	if(i+6<numeros.size()) {
        		columna.setNumSiete(numeros.get(i+6)+"");
        	}
            columnas.add(columna);


        }
        
        return columnas;
	}
	
}
