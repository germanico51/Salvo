package com.codeoftheweb.salvo;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

  @RequestMapping(path = "/players", method = RequestMethod.POST)
 public ResponseEntity<Object> register(
     @RequestParam String username, @RequestParam String password){
     if (username.isEmpty() || password.isEmpty() ) {
         return new ResponseEntity<>("Missing data", HttpStatus.BAD_REQUEST);
     }

     if (playerRepository.findByUserName(username) !=  null) {
         return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
     }

     playerRepository.save(new Player(username, passwordEncoder.encode(password)));
     return new ResponseEntity<>(HttpStatus.CREATED);
 }

 @RequestMapping(path = "/games", method = RequestMethod.POST)
 public ResponseEntity<Object> createGame (Authentication authentication){
      if(isGuest(authentication)){
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
      }else{
       Player player =  playerRepository.findByUserName(authentication.getName());
       if (player == null){
           return new ResponseEntity<>("Error, unauthorized", HttpStatus.UNAUTHORIZED);
          }

       Game game = new Game(new Date());
          gameRepository.save(game);
          GamePlayer gamePlayer = new GamePlayer(new Date(), game, player);
          gamePlayerRepository.save(gamePlayer);

          return new ResponseEntity<>(makeMap("gpId", gamePlayer.getId()), HttpStatus.CREATED);
      }
    }

    @RequestMapping("/games")
    public Map<String, Object> getGames(Authentication authentication){
        Map<String,Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)){
            Map<String, Object> guest = new LinkedHashMap<>();
            guest.put("username", "Guest");
            dto.put("player", guest);
        }else{
            Player player = playerRepository.findByUserName(authentication.getName());
            dto.put("player", player.getDto());
        }
        dto.put("games", gameRepository.findAll()
                .stream()
                .map(Game::getDto)
                .collect(toList()));
        return dto;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable Long gamePlayerId, Authentication authentication) {

        if(isGuest(authentication)){
            return new ResponseEntity<>(makeMap("Error","No esta logueado"), HttpStatus.UNAUTHORIZED);
        }
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer =gamePlayerRepository.findById(gamePlayerId).orElse(null);

        if (player == null){
            return new ResponseEntity<>(makeMap("Error","no se encontro player"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer == null){
            return new ResponseEntity<>(makeMap("Error","No se encontro gameplayer"), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer.getPlayer().getId() != player.getId()){
            return new ResponseEntity<>(makeMap("Error","Id del gameplayer no coincide"), HttpStatus.CONFLICT);
        }
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("idGame", gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getGame().getCreationDate().getTime());
        dto.put("gamePlayers", gamePlayer.getGame()
                .getGamePlayers()
                .stream()
                .map(GamePlayer::getDto)
                .collect(toList()));
        dto.put("ships", getShipList(gamePlayer.getShips()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream()
                .flatMap(gp -> gp.getSalvoes()
                        .stream()
                        .map(Salvo::getDto)
                )
                .collect(toList())
        );
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping("/leaderboard")
    public List<Map<String, Object>> getPlayers() {
        return playerRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Player::getTotalScore).reversed())
                .map(Player::getLeaderboardDto)
                .collect(toList());
    }

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> joinGame(@PathVariable Long gameId, Authentication authentication){
        if(isGuest(authentication)){
            return new ResponseEntity<>(makeMap("Error","Debe estar logueado"), HttpStatus.UNAUTHORIZED);
        }
        Player player = playerRepository.findByUserName(authentication.getName());
        Game game = gameRepository.getOne(gameId);
        if (game.getGamePlayers().stream().count() == 1){
            if (game.getGamePlayers().stream().map(gp -> gp.getPlayer().getUserName()).collect(Collectors.toList()).contains(authentication.getName())){
                return new ResponseEntity<>(makeMap("Error","Ya eres un jugador"), HttpStatus.UNAUTHORIZED);
            }else{
                GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(new Date(),game, player));
                return new ResponseEntity<>(makeMap("gpId",gamePlayer.getId()), HttpStatus.CREATED);
            }
        }else{
            return new  ResponseEntity<>(makeMap("Error","Juego completo"),HttpStatus.FORBIDDEN);
        }
    }

    private List<Map<String, Object>> getShipList(Set<Ship> ships) {
         return ships
                .stream()
                .map(Ship::getDto)
                .collect(toList());
    }

    public static Map<String, Object> makeMap(String key, Object value){
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put(key, value);
        return map;
    }

}
