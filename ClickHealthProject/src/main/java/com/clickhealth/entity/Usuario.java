package com.clickhealth.entity;

import java.util.List;

import com.clickhealth.dto.UsuarioDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "USUARIO")
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_USUARIO")
	private Long id;
	@Column(name = "NOMBRE")
	private String nombre;
	@Column(name = "APELLIDOS")
	private String apellidos;
	@Column(name = "DNI", length = 9)
	private String dni;
	@OneToOne(mappedBy = "usuario")
	User cuenta;
	@ManyToOne
	@JoinColumn(name = "ID_MED")
	Medico medico;
	@ManyToOne
	@JoinColumn(name = "ID_ENF")
	Enfermero enfermero;
	@OneToMany(mappedBy = "usuario")
	List<Vacuna> vacunas;
	@OneToMany(mappedBy = "usuario")
	List<Mensaje> mensajes;
	@OneToMany(mappedBy = "usuario")
	private List<Cita> citas;
	@OneToMany(mappedBy = "usuario")
	private List<Solicitud> solicitud;
	@OneToMany(mappedBy = "usuario")
	private List<Alergia> alergias;
	@OneToMany(mappedBy = "usuario")
	private List<Observacion> observaciones;

	public UsuarioDto toDto() {
		UsuarioDto usuarioDto = new UsuarioDto();
		usuarioDto.setId(this.getId());
		usuarioDto.setEmail(this.getCuenta().getEmail());
		usuarioDto.setName(this.getCuenta().getName());
		usuarioDto.setApellidos(this.getApellidos());
		usuarioDto.setNombre(this.nombre);
		usuarioDto.setDni(this.getDni());
		return usuarioDto;
	}

}
