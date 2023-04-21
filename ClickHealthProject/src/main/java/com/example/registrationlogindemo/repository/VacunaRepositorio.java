package com.example.registrationlogindemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.registrationlogindemo.entity.Vacuna;


public interface VacunaRepositorio extends JpaRepository<Vacuna,Long> {

}
