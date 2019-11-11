package com.codeoftheweb.salvo;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private long id;
    private Date creationDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Score> scores;

    public Game(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Game (){this.creationDate = new Date(); }

    public long getId() {
        return id;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Set<GamePlayer> getGamePlayers(){
        return gamePlayers;
    }

    public Map<String,Object> getDto(){
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("created", getCreationDate().getTime());
        dto.put("gamePlayers", getGamePlayers().stream().map(gamePlayer -> gamePlayer.getDto()));
        dto.put("score", this.getAllScores(getScores()));
        return dto;
    }
    private List<Map<String, Object>>getAllScores(Set<Score> scores) {
        return scores.stream().map(Score -> Score.getDto()).collect(Collectors.toList());
    }


}
