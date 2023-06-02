package com.clickhealth.service.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.clickhealth.service.EmailServiceI;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class EmailService implements EmailServiceI {

	private final String SENDGRID_API_KEY = "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";

	@Override
	public void enviarCorreo(String destinatario, String asunto, String contenido) {
		Email from = new Email("clickhealthproject@gmail.com");
		Email to = new Email(destinatario);
		Content content = new Content("text/html", contenido);
		Mail mail = new Mail(from, asunto, to, content);

		SendGrid sg = new SendGrid(SENDGRID_API_KEY);
		Request request = new Request();

		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);

			// Verificar la respuesta del envío
			if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
				// Envío exitoso
			} else {
				// Error en el envío
			}
		} catch (IOException ex) {
			// Manejar la excepción
		}
	}
}
