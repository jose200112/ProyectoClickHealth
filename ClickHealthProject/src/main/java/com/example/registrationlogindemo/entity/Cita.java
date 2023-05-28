package com.example.registrationlogindemo.entity;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CITA")
public class Cita {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_CITA")
	private Long id;

	@Column(name = "FECHA")
	private Date fecha;

	@Column(name = "ASISTENCIA")
	private String asistencia;

	@Column(name = "CONFIRMADA")
	private boolean confirmada;

	@ManyToOne
	@JoinColumn(name = "ID_TRAMO")
	private Tramo tramo;

	@ManyToOne
	@JoinColumn(name = "ID_USUARIO")
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "ID_ENF")
	private Enfermero enfermero;

	@ManyToOne
	@JoinColumn(name = "ID_MED")
	private Medico medico;

}
