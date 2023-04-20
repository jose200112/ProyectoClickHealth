package com.clickhealth.service;


import com.clickhealth.dto.EnfermeroDto;
import com.clickhealth.dto.VacunaDto;
import com.clickhealth.entity.User;


public interface EnfermeroServicioI {
    void saveUser(EnfermeroDto enfermeroDto);

    User findByEmail(String email);
    
    User findByName(String name);
    
    void saveVacuna(VacunaDto vacunaDto);

}
