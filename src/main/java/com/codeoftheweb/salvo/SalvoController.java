package com.codeoftheweb.salvo;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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


  @RequestMapping(path = "/players", method = RequestMethod.POST)
 public ResponseEntity<Object> register(
     @RequestParam String email, @RequestParam String password){
     if (email.isEmpty() || password.isEmpty() ) {
         return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
     }

     if (playerRepository.findByUserName(email) !=  null) {
         return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
     }

     playerRepository.save(new Player(email, password));
     return new ResponseEntity<>(HttpStatus.CREATED);
 }

    @RequestMapping("/games")
    public Map<String, Object> getGames(Authentication authentication){
        Map<String,Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)){
            dto.put("player", "Guest");
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
        public Map<String, Object> getGamePlayerView(@PathVariable Long gamePlayerId){

        GamePlayer gamePlayer = gamePlayerRepository.getOne(gamePlayerId);
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
        return dto;
    }

    @RequestMapping("/leaderboard")
    public List<Map<String, Object>> getPlayers() {
        return playerRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Player::getTotalScore).reversed())
                .map(Player::getLeaderboardDto)
                .collect(toList());
    }


    private List<Map<String, Object>> getShipList(Set<Ship> ships) {
         return ships
                .stream()
                .map(Ship::getDto)
                .collect(toList());
    }









}
