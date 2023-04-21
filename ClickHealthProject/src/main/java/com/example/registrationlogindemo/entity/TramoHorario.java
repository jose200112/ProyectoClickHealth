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
@Table(name="TRAMO_HORARIO")
public class TramoHorario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID_TRAMO_HORARIO")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="ID_TRAMO")
	Tramo tramo;
	
	@ManyToOne
	@JoinColumn(name="ID_HORARIO")
	Horario horario;

}
