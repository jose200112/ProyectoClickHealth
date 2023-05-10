package com.example.registrationlogindemo.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDto {
	private String titulo;
	
	private String descripcion;
	
	private String dni;
	
	private String dato;
	
	private char estado;
}
