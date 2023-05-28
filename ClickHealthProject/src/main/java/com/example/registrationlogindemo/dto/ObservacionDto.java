package com.example.registrationlogindemo.dto;

import java.time.LocalTime;

import com.example.registrationlogindemo.entity.Medico;
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
public class ObservacionDto {
	private Long id;

	@NotEmpty(message = "Especifique los sintomas del paciente")
	private String sintomas;

	@NotEmpty(message = "Indique al menos un diagnostico provisional")
	private String diagnostico;

	private String tratamiento;

	@NotEmpty(message = "Redacte una descripcion")
	private String descripcion;

	private Medico medico;

	private Usuario usuario;
}
