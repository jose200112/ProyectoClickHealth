package com.example.registrationlogindemo.dto;


import java.sql.Date;

import com.example.registrationlogindemo.entity.Enfermero;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.entity.Usuario;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlergiaDto {
	private Long id;
    @NotEmpty(message = "La causa no puede estar vacia")
	private String causa;
	private String gravedad;
    @NotEmpty(message = "El tratamiento no puede estar vacio")
	private String tratamiento;
    private String descripcion;
    private Usuario usuario;
    private Enfermero enfermero;
}
