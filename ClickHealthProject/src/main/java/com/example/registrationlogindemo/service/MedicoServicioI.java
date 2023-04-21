package com.example.registrationlogindemo.service;

import com.example.registrationlogindemo.dto.MedicoDto;
import com.example.registrationlogindemo.entity.User;

public interface MedicoServicioI {
    void saveMedico(MedicoDto medicoDto);

    User findByEmail(String email);
    
    User findByName(String name);
}
