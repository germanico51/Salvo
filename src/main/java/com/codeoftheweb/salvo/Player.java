package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private long id;
    private String password;

    private String userName ;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<Score> scores;

    public long getId() {
        return id;
    }

    public String getPassword(){
        return password;
    }

    public Player(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }


    public Set<GamePlayer> getGames(){

        return gamePlayers;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setPassword(String password){
        this.password=password;
    }

    public Player() { }

    public Player(String email, String password){
        this.userName=email;
        this.password=password;

    }

    public Set<Score> getTied() {
        return getScores()
                .stream()
                .filter(score -> score.getScore() == 0.5)
                .collect(toSet());
    }

    public Set<Score> getLost() {
        return getScores()
                .stream()
                .filter(score -> score.getScore() == 0)
                .collect(toSet());
    }

    public Set<Score> getWon() {
        return getScores()
                .stream()
                .filter(score -> score.getScore() == 1)
                .collect(toSet());
    }

    public double getTotalScore() {
        return getWon().size() + getTied().size() * 0.5;
    }

    public Map<String, Object> getLeaderboardDto() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("name", getUserName());
        dto.put("total", getTotalScore());
        dto.put("won", getWon().size());
        dto.put("lost", getLost().size());
        dto.put("tied", getTied().size());
        return dto;
    }


    public Map<String, Object> getDto(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id",getId());
        dto.put("email", getUserName());

        return dto;
    }

   }
