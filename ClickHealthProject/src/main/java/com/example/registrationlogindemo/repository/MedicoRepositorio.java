package com.example.registrationlogindemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.registrationlogindemo.entity.Medico;



public interface MedicoRepositorio extends JpaRepository<Medico, Long> {

    @Query("SELECT m " +
            "FROM Medico m " +
            "LEFT JOIN m.usuarios u " +
            "GROUP BY m.id, m.nombre " +
            "ORDER BY COUNT(u.id) ASC " +
            "LIMIT 1")
     Medico findMedicoConMenosUsuarios();

}
