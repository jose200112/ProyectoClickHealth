package com.clickhealth.dto;

import java.sql.Date;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDto {
	private Date fecha;
	private String titulo;
	private String descripcion;
	private Long dias;
}
