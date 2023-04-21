package com.example.registrationlogindemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.registrationlogindemo.entity.Enfermero;


public interface EnfermeroRepositorio extends JpaRepository<Enfermero,Long> {
    @Query("SELECT e " +
            "FROM Enfermero e " +
            "LEFT JOIN e.usuarios u " +
            "GROUP BY e.id, e.nombre " +
            "ORDER BY COUNT(u.id) ASC " +
            "LIMIT 1")
     Enfermero findEnfermeroConMenosUsuarios();
}
