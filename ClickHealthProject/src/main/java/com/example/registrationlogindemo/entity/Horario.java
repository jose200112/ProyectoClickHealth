package com.example.registrationlogindemo.entity;

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "HORARIO")
public class Horario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_HORARIO")
	private Long id;
	@Column(name="COMIENZA")
	private LocalTime comienza;
	@Column(name="TERMINA")
	private LocalTime termina;
	@ManyToOne
	@JoinColumn(name="ID_ENF")
	Enfermero enfermero;
	@ManyToOne
	@JoinColumn(name="ID_MED")
	Medico medico;
	@OneToMany(mappedBy="horario")
	List<TramoHorario> tramoHorarios;

}
