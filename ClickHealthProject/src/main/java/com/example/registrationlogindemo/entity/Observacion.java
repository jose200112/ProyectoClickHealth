package com.example.registrationlogindemo.entity;

import java.util.List;

import com.example.registrationlogindemo.dto.ObservacionDto;

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
@Table(name = "OBSERVACION")
public class Observacion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_OBS")
	private Long id;
	
	@Column(name="SINTOMAS")
	private String sintomas;
	
	@Column(name="DIAGNOSTICO")
	private String diagnostico;
	
	@Column(name="TRATAMIENTO")
	private String tratamiento;
	
	@Column(name="DESCRIPCION")
	private String descripcion;
	
	@ManyToOne
	@JoinColumn(name = "ID_MED")
	private Medico medico;
	
	@ManyToOne
	@JoinColumn(name = "ID_USUARIO")
	private Usuario usuario;
	
	public ObservacionDto toDto() {
		ObservacionDto observacionDto = new ObservacionDto();
		
		observacionDto.setId(id);
		observacionDto.setDescripcion(this.descripcion);
		observacionDto.setDiagnostico(this.diagnostico);
		observacionDto.setSintomas(this.sintomas);
		observacionDto.setTratamiento(this.tratamiento);
		
		return observacionDto;
	}

	
}
