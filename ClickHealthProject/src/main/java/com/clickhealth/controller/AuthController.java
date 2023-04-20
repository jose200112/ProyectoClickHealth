package com.clickhealth.controller;

import com.clickhealth.dto.UserDto;
import com.clickhealth.entity.User;
import com.clickhealth.service.UserService;

import jakarta.validation.Valid;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;
import java.util.List;

@Controller
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/index")
    public String home(){
        return "index";
    }
    
    @GetMapping("/Inicio")
    public String inicio() {
    	return "Inicio";
    }
    
    @GetMapping("hello")
    public String hello() {
    	return "hello";
    }

    @GetMapping("/login")
    public String loginForm() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
        	return "login";
        } 

    	return "redirect:/";
    }

    // handler method to handle user registration request
    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }
    
    

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model){
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/register?success";
    }

    @GetMapping("Enfermero/users")
    public String listRegisteredUsers(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }
    
    @GetMapping("register/inicioAdmin")
    public String inicioAdmin() {
    	return "inicioAdmin";
    }
    
    @GetMapping("/redirige")
    public String redirigeRol() {
    	String auth ="";
        Collection<? extends GrantedAuthority> authority = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for(GrantedAuthority author: authority) {
        	System.out.println(author);
        	auth  = author.toString();
        }
        if(auth.equals("ROLE_ADMIN")) {
            return "redirect:/admin/inicioAdmin";
        }
        
        if(auth.equals("ROLE_ENFERMERO")) {
        	return "redirect:/enfermero/inicioEnfermero";
        }
        
        if(auth.equals("ROLE_USUARIO")) {
        	return "redirect:/usuario/inicioUsuario";
        }
        
        if(auth.equals("ROLE_MEDICO")) {
        	return "redirect:/medico/inicioMedico";
        }
                
		return "redirect:/login";
    }
    
    
}
