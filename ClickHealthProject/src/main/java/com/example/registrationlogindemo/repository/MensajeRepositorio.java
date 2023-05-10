package com.example.registrationlogindemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.registrationlogindemo.entity.Mensaje;

public interface MensajeRepositorio extends JpaRepository<Mensaje, Long>  {

}
