package com.example.registrationlogindemo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.registrationlogindemo.entity.Cita;
import com.example.registrationlogindemo.repository.CitaRepositorio;
import com.example.registrationlogindemo.service.EmailServiceI;

@Component
public class ScheduledTasks {

	CitaRepositorio citaRepo;

	@Autowired
	EmailServiceI emailServiceI;

	@Scheduled(cron = "0 00 8 * * *") // Ejecutar todos los días a las 08:00 AM
	public void executeTask() {
		LocalDate fechaDiaSiguiente = LocalDate.now().plusDays(1);

		List<Cita> citas = citaRepo.findByFechaAndConfirmadaIsFalse(fechaDiaSiguiente);
		for (Cita cita : citas) {
			String contenido = "<html><body>" + "<img src='https://i.imgur.com/ymmyp91.png'/>"
					+ "<h2>¡Bienvenido/a a ClickHealth!</h2>" + "<p>Estimado " + cita.getUsuario().getNombre() + " "
					+ cita.getUsuario().getApellidos() + ",</p>"
					+ "<p>le recordamos que debe confirmar su cita para mañana.</p>" + "</body></html>";

			emailServiceI.enviarCorreo(cita.getUsuario().getCuenta().getEmail(), "Confirmacion de cita", contenido);
		}
		System.out.println("La tarea programada se ha ejecutado a las 08:00 AM.");
	}
}
