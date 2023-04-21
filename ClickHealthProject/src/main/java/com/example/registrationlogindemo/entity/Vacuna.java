package com.example.registrationlogindemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "VACUNA")
public class Vacuna {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idVac;
	@Column(name = "NOMBRE")
	private String nombre;
	@Column(name = "DOSIS")
	private int dosis;
	@Column(name = "NUM_LOTE")
	private int numLote;
	@ManyToOne
	@JoinColumn(name = "ID_ENF")
	private Enfermero enfermero;
	@ManyToOne
	@JoinColumn(name = "ID_USUARIO")
	private Usuario usuario;
}
