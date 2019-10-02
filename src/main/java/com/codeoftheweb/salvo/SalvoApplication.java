package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}



	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gameplayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			Player player1 = new Player("j.bauer@ctu.gov",passwordEncoder().encode("123"));
			Player player2 = new Player("c.obrian@ctu.gov","123");
			Player player3 = new Player("kim_bauer@ctu.gov","123");
			Player player4 = new Player("t.almeida@ctu.gov","123");
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

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository PlayerRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = PlayerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(),player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}
@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/web/*","/api/games","/api/login","/api/leaderboard").permitAll()
				.antMatchers("/api/**","/web/game.html**").hasAnyAuthority("USER")
				.antMatchers("/rest/**").hasAnyAuthority("ADMIN")
				.anyRequest().permitAll();

		http.formLogin()
				.usernameParameter("username")
				.passwordParameter("password")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}
		private void clearAuthenticationAttributes(HttpServletRequest request) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
			}
		}
	}


