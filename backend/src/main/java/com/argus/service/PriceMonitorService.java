package com.argus.service;

import com.argus.model.Product;
import com.argus.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PriceMonitorService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ScraperService scraperService;

    @Autowired
    private EmailService emailService;

    // Roda a cada 1 hora (3600000 ms)
    @Scheduled(fixedRate = 3600000)
    public void monitorPrices() {
        System.out.println("🤖 Robô Argus iniciado: Verificando preços...");

        List<Product> products = repository.findAll();

        for (Product product : products) {
            try {
                BigDecimal newPrice = scraperService.getLivePrice(product.getUrl());

                if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                System.out.println("🔎 Verificando: " + product.getName());
                System.out.println("   - Preço Banco: R$ " + product.getCurrentPrice());
                System.out.println("   - Preço Loja:  R$ " + newPrice);
                System.out.println("   - Alvo:        " + (product.getTargetPrice() != null ? "R$ " + product.getTargetPrice() : "Não definido"));

                boolean shouldSendEmail = false;

                if (product.getTargetPrice() != null) {
                    if (newPrice.compareTo(product.getTargetPrice()) <= 0) {

                        if (product.getCurrentPrice().compareTo(product.getTargetPrice()) > 0) {
                            System.out.println("🚨 ALERTA: O produto atingiu o preço alvo!");
                            shouldSendEmail = true;
                        }
                    }
                }
                else {
                    if (newPrice.compareTo(product.getCurrentPrice()) < 0) {
                        System.out.println("📉 ALERTA: Preço caiu (sem alvo definido)!");
                        shouldSendEmail = true;
                    }
                }

                if (shouldSendEmail) {
                    product.setCurrentPrice(newPrice);
                    emailService.sendPriceAlert(product);
                }

                product.setCurrentPrice(newPrice);
                product.setLastCheckedAt(LocalDateTime.now());
                repository.save(product);

                Thread.sleep(5000);

            } catch (Exception e) {
                System.err.println("❌ Erro ao monitorar " + product.getName() + ": " + e.getMessage());
            }
        }
        System.out.println("💤 Ciclo finalizado. Voltando a dormir.");
    }
}