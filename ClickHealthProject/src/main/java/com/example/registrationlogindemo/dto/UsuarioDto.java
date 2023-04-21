package com.example.registrationlogindemo.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
	private Long id;
	@NotEmpty(message = "El nombre de usuario no puede estar vacio")
	private String name;
	@NotEmpty(message = "El correo no puede estar vacio")
	@Email
	private String email;
	@NotEmpty(message = "La clave no puede estar vacia")
	private String password;
	
	@NotEmpty(message = "El dni no puede estar vacio")
	private String dni;
	@NotEmpty(message = "El nombre del enfermero no puede estar vacio")
	private String nombre;
	
	@NotEmpty(message = "Los apellidos del enfermero no pueden estar vacios")
	private String apellidos;

}
