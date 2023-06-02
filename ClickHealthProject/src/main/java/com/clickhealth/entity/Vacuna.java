package com.clickhealth.entity;

import java.sql.Date;

import org.thymeleaf.util.StringUtils;

import com.clickhealth.dto.VacunaDto;

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
	private Long id;
	@Column(name = "NOMBRE")
	private String nombre;
	@Column(name = "DOSIS")
	private int dosis;
	@Column(name = "NUM_LOTE")
	private int numLote;
	@Column(name = "FECHA")
	private Date fecha;
	@ManyToOne
	@JoinColumn(name = "ID_ENF")
	private Enfermero enfermero;
	@ManyToOne
	@JoinColumn(name = "ID_USUARIO")
	private Usuario usuario;

	public VacunaDto toDto() {
		VacunaDto vacuna = new VacunaDto();
		vacuna.setId(this.id);
		vacuna.setFecha(StringUtils.toString(this.fecha));
		vacuna.setDosis(this.dosis);
		vacuna.setNombre(this.nombre);
		vacuna.setNumLote(this.numLote);

		return vacuna;
	}
}
