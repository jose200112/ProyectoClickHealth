package com.clickhealth.service;

import com.clickhealth.dto.UsuarioDto;
import com.clickhealth.entity.User;

public interface UsuarioServicioI {
    void saveUsuario(UsuarioDto usuarioDto);

    User findByEmail(String email);
    
    User findByName(String name);
}
