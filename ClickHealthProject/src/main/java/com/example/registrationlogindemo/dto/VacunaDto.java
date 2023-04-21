package com.example.registrationlogindemo.dto;


import com.example.registrationlogindemo.entity.User;

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
	private int dosis;
    @NotEmpty(message = "El nombre no puede estar vacio")
	private String nombre;
	private int numLote;
	private String email;
    @NotEmpty(message = "El dni del usuario/paciente no puede estar vacio")
	private String dni;
    private User user;
}
