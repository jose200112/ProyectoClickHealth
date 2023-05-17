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
public class CambiaClaveDto {
	@NotEmpty(message = "El correo no puede estar vacio")
	@Email
	private String email;
	@NotEmpty(message = "Repita el correo")
	@Email
	private String email2;
}
