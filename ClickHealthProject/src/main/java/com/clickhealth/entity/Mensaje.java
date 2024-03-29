package com.clickhealth.entity;

import java.sql.Date;

import com.clickhealth.dto.MensajeDto;

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
@Table(name = "MENSAJE")
public class Mensaje {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_MEN")
	private Long id;

	@Column(name = "FECHA")
	private Date fecha;

	@Column(name = "TITULO")
	private String titulo;

	@Column(name = "DESCRIPCION")
	private String descripcion;

	@ManyToOne
	@JoinColumn(name = "ID_USUARIO")
	Usuario usuario;

	public MensajeDto toDto(Long dias) {
		MensajeDto mensajeDto = new MensajeDto();
		mensajeDto.setTitulo(this.titulo);
		mensajeDto.setDescripcion(this.descripcion);
		mensajeDto.setDias(dias);
		return mensajeDto;
	}
}
