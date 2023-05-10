package com.example.registrationlogindemo.entity;

import java.sql.Date;

import com.example.registrationlogindemo.dto.AlergiaDto;
import com.example.registrationlogindemo.dto.VacunaDto;

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
@Table(name = "ALERGIA")
public class Alergia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "CAUSA")
	private String causa;
	@Column(name = "GRAVEDAD")
	private String gravedad;
	@Column(name = "TRATAMIENTO")
	private String tratamiento;
	@Column(name = "DESCRIPCION")
	private String descripcion;
	@ManyToOne
	@JoinColumn(name = "ID_ENF")
	private Enfermero enfermero;
	@ManyToOne
	@JoinColumn(name = "ID_USUARIO")
	private Usuario usuario;
	
	public AlergiaDto toDto() {
		AlergiaDto alergia = new AlergiaDto();
		alergia.setId(this.id);
		alergia.setCausa(this.causa);
		alergia.setGravedad(this.gravedad);
		alergia.setTratamiento(this.tratamiento);
		alergia.setDescripcion(this.descripcion);
		
		return alergia;
	}
}
