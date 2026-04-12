package com.nancyimmo.bailleur;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NancyImmoApplication {

	public static void main(String[] args) {
		SpringApplication.run(NancyImmoApplication.class, args);
	}

	@Bean
	CommandLineRunner verifyDatabaseConnection(DataSource dataSource) {
		return args -> {
			try (Connection connection = dataSource.getConnection()) {
				System.out.println("Connexion a la base OK : " + connection.getMetaData().getURL());
			} catch (SQLException e) {
				System.out.println("Connexion a la base KO : " + e.getMessage());
			}
		};
	}
}
