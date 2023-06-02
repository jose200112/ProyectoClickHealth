package com.clickhealth.entity;

import java.util.List;

import com.clickhealth.dto.EnfermeroDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "ENFERMERO")
public class Enfermero {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_ENF")
	private Long id;
	@Column(name = "CODIGO", unique = true)
	private String codigo;
	@Column(name = "SALA", unique = true)
	private String sala;
	@Column(name = "NOMBRE")
	private String nombre;
	@Column(name = "APELLIDOS")
	private String apellidos;
	@Column(name = "DNI", length = 9, unique = true)
	private String dni;
	@OneToOne(mappedBy = "enfermero")
	User cuenta;
	@OneToMany(mappedBy = "enfermero")
	List<Usuario> usuarios;
	@OneToMany(mappedBy = "enfermero")
	List<Vacuna> vacunas;
	@OneToMany(mappedBy = "enfermero")
	List<Horario> horarios;
	@OneToMany(mappedBy = "enfermero")
	private List<Cita> citas;
	@OneToMany(mappedBy = "enfermero")
	private List<Solicitud> solicitudes;
	@OneToMany(mappedBy = "enfermero")
	private List<Alergia> alergias;

	public EnfermeroDto toDto() {
		EnfermeroDto enfermeroDto = new EnfermeroDto();
		enfermeroDto.setName(this.getCuenta().getName());
		enfermeroDto.setEmail(this.getCuenta().getEmail());

		enfermeroDto.setId(this.getId());
		enfermeroDto.setDni(this.getDni());
		enfermeroDto.setNombre(this.getNombre());
		enfermeroDto.setApellidos(this.getApellidos());
		enfermeroDto.setSala(this.getSala());

		enfermeroDto.setComienza(this.getHorarios().get(0).getComienza());
		enfermeroDto.setTermina(this.getHorarios().get(0).getTermina());
		return enfermeroDto;
	}
}
