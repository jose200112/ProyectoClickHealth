package com.example.registrationlogindemo.service;

import com.example.registrationlogindemo.dto.UsuarioDto;
import com.example.registrationlogindemo.entity.User;

public interface UsuarioServicioI {
    void saveUsuario(UsuarioDto usuarioDto);

    User findByEmail(String email);
    
    User findByName(String name);
}
