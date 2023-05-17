package com.example.registrationlogindemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.registrationlogindemo.entity.Medico;
import com.example.registrationlogindemo.entity.Usuario;



public interface MedicoRepositorio extends JpaRepository<Medico, Long> {

    @Query("SELECT m " +
            "FROM Medico m " +
            "LEFT JOIN m.usuarios u " +
            "GROUP BY m.id, m.nombre " +
            "ORDER BY COUNT(u.id) ASC " +
            "LIMIT 1")
     Medico findMedicoConMenosUsuarios();
    
    
    Medico findBySala(String sala);
    
    Medico findByCodigo(String codigo);
    
    Medico findByDni(String dni);
    
    @Query("SELECT m FROM Medico m WHERE CONCAT(m.apellidos, ' ', m.nombre) LIKE %?1%")
    List<Medico> buscarPorNombreCompleto(String nombreCompleto);
    
    @Query("SELECT m " +
            "FROM Medico m " +
            "LEFT JOIN m.usuarios u " +
            "WHERE m.id <> :medicoId " +
            "GROUP BY m.id, m.nombre " +
            "ORDER BY COUNT(u.id) ASC " +
            "LIMIT 1")
    Medico findMedicoConMenosUsuariosExcluye(@Param("medicoId") Long medicoId);
    
    
    @Query("SELECT m FROM Medico m WHERE CONCAT(m.apellidos, ' ', m.nombre) LIKE %?1% AND m.id <> ?2")
    List<Medico> buscarPorNombreCompletoId(String nombreCompleto, Long id);



}
