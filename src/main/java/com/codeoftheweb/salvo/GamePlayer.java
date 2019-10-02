package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private long id;
    private Date joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Ship> ships  = new HashSet<>();


    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Salvo> salvoes = new HashSet<>();

    public GamePlayer() {
    }

    public long getId() {
        return id;
    }

    public GamePlayer(Date joinDate, Game game, Player player ){
        this.joinDate=joinDate;
        this.game=game;
        this.player=player;
    }


    public Date getJoinDate() {
        return joinDate;
    }


    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public Set<Ship> getShips(){ return ships;}



    @JsonIgnore
    public Game getGame() {
        return game;
    }

    @JsonIgnore
    public Player getPlayer() {
        return player;
    }

    public Map<String, Object> getDto (){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("player", getPlayer().getDto());
        dto.put("score", getPlayer().getScores());

        return dto;
    }


}
