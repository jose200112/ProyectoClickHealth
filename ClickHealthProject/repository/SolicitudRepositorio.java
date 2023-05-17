package com.example.registrationlogindemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.registrationlogindemo.entity.Solicitud;
import com.example.registrationlogindemo.entity.Usuario;

@Repository
public interface SolicitudRepositorio extends JpaRepository<Solicitud, Long> {

    Solicitud findByUsuarioAndTituloAndEstadoIsNull(Usuario usuario, String titulo);
    
    List<Solicitud> findByEstadoIsNull();
}
