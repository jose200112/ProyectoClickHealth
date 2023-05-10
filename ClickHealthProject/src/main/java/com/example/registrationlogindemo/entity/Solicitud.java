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
@Table(name = "SOLICITUD")
public class Solicitud {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_SOLICITUD")
	private Long id;
	
	@Column(name ="TITULO")
	private String titulo;
	
	@Column(name = "DESCRIPCION")
	private String descripcion;
	
	@Column(name="ESTADO")
	private String estado;
	
	@ManyToOne
	@JoinColumn(name = "ID_USUARIO")
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name = "ID_ENFERMERO")
	private Enfermero enfermero;
	
	@ManyToOne
	@JoinColumn(name = "ID_MEDICO")
	private Medico medico;

}
