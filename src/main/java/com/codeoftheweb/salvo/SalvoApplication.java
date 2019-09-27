package com.codeoftheweb.salvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gameplayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			Player player1 = new Player("j.bauer@ctu.gov");
			Player player2 = new Player("c.obrian@ctu.gov");
			Player player3 = new Player("kim_bauer@ctu.gov");
			Player player4 = new Player("t.almeida@ctu.gov");
			//Player player5=  new Player("jbcrodriguezsud@gmail.com");

			Date date1 = new Date();
			Date date2 = Date.from(date1.toInstant().plusSeconds(3600));
			Date date3 = Date.from(date2.toInstant().plusSeconds(3600));

			Game game1 = new Game (date1);
			Game game2 = new Game (date2);
			Game game3 = new Game (date3);

			GamePlayer gp1 = new GamePlayer(date1, game1,player1);
			GamePlayer gp2 = new GamePlayer(date1, game1,player2);

			GamePlayer gp3 = new GamePlayer(date1, game2,player2);
			GamePlayer gp4 = new GamePlayer(date1, game2,player3);

			GamePlayer gp5 = new GamePlayer(date1, game3,player3);
			GamePlayer gp6 = new GamePlayer(date1, game3,player4);

			Ship s1 = new Ship("Destroyer", Arrays.asList("A3","A4","A2"), gp1);
			Ship s2 = new Ship("Submarine", Arrays.asList("B6","B4","B5"), gp2);
			Ship s3 = new Ship("Patrol Boat", Arrays.asList("C3","C4"), gp1);
			Ship s4 = new Ship("Destroyer", Arrays.asList("D3","D4","D2"),gp2);

			gp1.addShip(s1);
			gp1.addShip(s3);
			gp2.addShip(s4);
			gp2.addShip(s2);

			// salvo juego 1 turno 1
			List<String> salvoloc1 = Arrays.asList("B6","B4", "B5");
			Salvo s1t1 = new Salvo(gp1, 1, salvoloc1);
			List<String> salvoloc2 = Arrays.asList("B2","B3", "B1");
			Salvo s1t2 = new Salvo(gp2, 1, salvoloc2);
			//salvo juego 1 turno 2
			List<String> salvoloc3 = Arrays.asList("C2","D4");
			Salvo s2t1 = new Salvo(gp1, 2, salvoloc3);
			List<String> salvoloc4 = Arrays.asList("H1","H2", "H3");
			Salvo s2t2 = new Salvo(gp2, 2, salvoloc4);

			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);
		//	playerRepository.save(player5);

			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);

			gameplayerRepository.save(gp1);
			gameplayerRepository.save(gp2);
			gameplayerRepository.save(gp3);
			gameplayerRepository.save(gp4);
			gameplayerRepository.save(gp5);
			gameplayerRepository.save(gp6);

			shipRepository.saveAll(Arrays.asList(s1,s2,s3,s4));

			salvoRepository.saveAll(Arrays.asList(s1t1,s2t1,s1t2,s2t2));

			Score score1 = new Score(1, game1, player1);
			Score score2 = new Score(0, game1, player2);
			Score score3 = new Score(0.5, game2, player2);
			Score score4 = new Score(0.5, game2, player3);
			Score score5 = new Score(0, game3, player3);
			Score score6 = new Score(1, game3, player4);


			scoreRepository.saveAll(Arrays.asList(score1, score2, score3, score4,score5,score6));



		};
	}


}