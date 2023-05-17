package com.example.registrationlogindemo;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    
    @Scheduled(cron = "0 00 8 * * *") // Ejecutar todos los días a las 08:00 AM
    public void executeTask() {
        // Coloca aquí el código que deseas ejecutar cada día a las 08:00 AM
        System.out.println("La tarea programada se ha ejecutado a las 08:00 AM.");
    }
}
