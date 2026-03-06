package com.argus.service;

import com.argus.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${SMTP_EMAIL}")
    private String smtpMail;

    public void sendPriceAlert(Product product) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("Argus <" + smtpMail + ">");

            if (product.getUser() != null && product.getUser().getEmail() != null && !product.getUser().getEmail().isEmpty()) {
                message.setTo(product.getUser().getEmail());
            } else {
                message.setTo("eliasmirandaaraujo7@gmail.com"); // Fallback
            }

            message.setSubject("🚨 ALERTA DE PREÇO: " + product.getName());

            String text = String.format("""
                Olá! O Argus detectou uma queda de preço! 👁️
                
                Produto: %s
                Novo Preço: R$ %.2f
                
                Corra para comprar: %s
                """,
                    product.getName(),
                    product.getCurrentPrice(),
                    product.getUrl());

            message.setText(text);

            mailSender.send(message);
            System.out.println("✅ Email de alerta enviado para o produto: " + product.getName());

        } catch (Exception e) {
            System.err.println("❌ Falha ao enviar email: " + e.getMessage());
        }
    }
}