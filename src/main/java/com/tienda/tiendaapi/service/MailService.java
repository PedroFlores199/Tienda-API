package com.tienda.tiendaapi.service;

import com.tienda.tiendaapi.domain.Factura;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final PdfService pdfService;

    @Async
    public void enviarFactura(Factura factura) {
        try {
            byte[] pdfBytes = pdfService.generarFacturaPdf(factura);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(factura.getEmailCliente());
            helper.setSubject("Factura " + factura.getNumeroFactura());
            helper.setText("Adjuntamos la factura correspondiente a su pedido.");
            helper.addAttachment("Factura_" + factura.getNumeroFactura() + ".pdf", new ByteArrayResource(pdfBytes));
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error enviando correo de factura: " + e.getMessage());
        }
    }
}
