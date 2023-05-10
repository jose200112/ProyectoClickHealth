package com.example.registrationlogindemo.dto;


import java.sql.Date;

import com.example.registrationlogindemo.entity.Enfermero;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VacunaDto {
	private Long id;
	private int dosis;
    @NotEmpty(message = "El nombre no puede estar vacio")
	private String nombre;
	private int numLote;
	private String email;
    private User user;
    private String fecha;
    private Usuario usuario;
    private Enfermero enfermero;
}
