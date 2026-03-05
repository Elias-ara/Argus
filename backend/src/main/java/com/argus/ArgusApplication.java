package com.argus;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class ArgusApplication {

	public static void main(String[] args) {
		try {
			// Tenta carregar o arquivo .env do diretório atual
			Dotenv dotenv = Dotenv.configure()
					.ignoreIfMissing() // Não quebra se não achar (ex: produção/Docker)
					.load();

			// Injeta as variáveis no sistema para o Spring ler
			dotenv.entries().forEach(entry -> {
				System.setProperty(entry.getKey(), entry.getValue());
			});

			System.out.println("✅ [ARGUS] Variáveis do .env carregadas com sucesso!");
			System.out.println("   -> DB_USER: " + System.getProperty("DB_USER")); // Log de teste (apague depois)

			// Teste de leitura de variáveis sensíveis (temporario)
			String googleId = System.getProperty("GOOGLE_CLIENT_ID");
			System.out.println("   -> GOOGLE_ID (Check): " + (googleId != null ? googleId.substring(0, 10) + "..." : "NULO"));

		} catch (Exception e) {
			System.err.println("⚠️ [ARGUS] .env não encontrado ou erro ao ler. Usando variáveis de ambiente do sistema.");
		}

		SpringApplication.run(ArgusApplication.class, args);
	}
}