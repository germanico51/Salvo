package com.codeoftheweb.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @RequestMapping("/games")
    public List<Map<String, Object>> getGames(){
        return gameRepository.findAll().stream().map(Game -> Game.getDto()).collect(toList());
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
