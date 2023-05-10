package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
    

    @GetMapping("/login")
    public String loginForm() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
        	return "login";
        } 
        
        
        return "redirect:/redirige";
        
    }
    
    
    @RequestMapping("/redirige")
    public String redirigeRol(HttpServletRequest request) {
    	
        if(request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin/inicioAdmin";
        }
        
        if(request.isUserInRole("ROLE_ENFERMERO")) {
        	return "redirect:/enfermero/inicioEnfermero";
        }
        
        if(request.isUserInRole("ROLE_USUARIO")) {
        	return "redirect:/usuario/inicioUsuario";
        }
        
        if(request.isUserInRole("ROLE_MEDICO")) {
        	return "redirect:/medico/inicioMedico";
        }
                
		return "redirect:/login";
    }
    
    
}
