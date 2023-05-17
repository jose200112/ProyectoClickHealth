package com.example.registrationlogindemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.registrationlogindemo.entity.Horario;


public interface HorarioRepositorio extends JpaRepository<Horario, Long> {

}
