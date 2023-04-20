package com.clickhealth.service;

import com.clickhealth.dto.MedicoDto;
import com.clickhealth.entity.User;

public interface MedicoServicioI {
    void saveMedico(MedicoDto medicoDto);

    User findByEmail(String email);
    
    User findByName(String name);
}
