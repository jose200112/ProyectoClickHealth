package com.example.registrationlogindemo.service;

import com.example.registrationlogindemo.dto.EnfermeroDto;
import com.example.registrationlogindemo.dto.VacunaDto;
import com.example.registrationlogindemo.entity.User;

public interface EnfermeroServicioI {
    void saveUser(EnfermeroDto enfermeroDto);

    User findByEmail(String email);
    
    User findByName(String name);
    
    void saveVacuna(VacunaDto vacunaDto);

}
