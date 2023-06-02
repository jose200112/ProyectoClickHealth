package com.clickhealth.dto;

import java.sql.Date;

import com.clickhealth.entity.Enfermero;
import com.clickhealth.entity.User;
import com.clickhealth.entity.Usuario;

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
