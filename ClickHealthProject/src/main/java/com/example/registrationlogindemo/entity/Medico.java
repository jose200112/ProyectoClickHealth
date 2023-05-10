package com.example.registrationlogindemo.entity;

import java.util.List;

import com.example.registrationlogindemo.dto.EnfermeroDto;
import com.example.registrationlogindemo.dto.MedicoDto;

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
@Table(name = "MEDICO")
public class Medico {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_MED")
	private Long id;
	@Column(name = "CODIGO", unique=true)
	private String codigo;
	@Column(name = "SALA", unique=true)
	private String sala;
	@Column(name = "NOMBRE")
	private String nombre;
	@Column(name = "APELLIDOS")
	private String apellidos;
	@Column(name = "DNI", length = 9)
	private String dni;
	@OneToOne(mappedBy = "medico")
	User cuenta;
	@OneToMany(mappedBy = "medico")
	List<Usuario> usuarios;
	@OneToMany(mappedBy = "medico")
	List<Horario> horarios;
	@OneToMany(mappedBy="medico")
	private List<Cita> citas;
	@OneToMany(mappedBy="medico")
	private List<Solicitud> solicitudes;
	@OneToMany(mappedBy="medico")
	private List<Observacion> observaciones;
	
	public MedicoDto toDto() {
		MedicoDto medicoDto = new MedicoDto();
		medicoDto.setName(this.getCuenta().getName());
		medicoDto.setEmail(this.getCuenta().getEmail());
		
		medicoDto.setId(this.getId());
		medicoDto.setDni(this.getDni());
		medicoDto.setNombre(this.getNombre());
		medicoDto.setApellidos(this.getApellidos());
		medicoDto.setSala(this.getSala());
		
		medicoDto.setComienza(this.getHorarios().get(0).getComienza());
		medicoDto.setTermina(this.getHorarios().get(0).getTermina());
		return medicoDto;
	}

}
