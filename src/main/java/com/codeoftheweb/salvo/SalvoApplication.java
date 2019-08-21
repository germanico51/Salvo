package com.codeoftheweb.salvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
/*
	@Bean
	public CommandLineRunner initData(GameRepository repository) {
		return (args) -> {
			// save a couple of customers


			repository.save(new Game(new Date()));
			repository.save(new Game(Date.from(new Date().toInstant().plusSeconds(3600))));
			repository.save(new Game(Date.from(new Date().toInstant().plusSeconds(7200))));

		};
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository repository) {
		return (args) -> {
			// save a couple of customers
			repository.save(new Player("j.bauer@ctu.gov"));
			repository.save(new Player("c.obrian@ctu.gov"));
			repository.save(new Player("kim_bauer@gmail.com"));
			repository.save(new Player("t.almeida@ctu.gov"));
		};
	}
*/
	@Bean
	public CommandLineRunner initData(GamePlayerRepository repository, PlayerRepository prepository) {
		return (args) -> {
			// save a couple of customers
			repository.save(new GamePlayer());
			prepository.save(new Player("j.bauer@ctu.gov"));
		};
	}
}