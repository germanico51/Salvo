package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator ="native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="salvo_location")
    private List<String> salvoLocations=new ArrayList<>();

    public Salvo() {
    }

    public Salvo(GamePlayer gamePlayer, int turn, List<String> salvoLocations) {
        this.gamePlayer = gamePlayer;
        this.turn = turn;
        this.salvoLocations=salvoLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer){this.gamePlayer=gamePlayer;}

    public void setTurn(int turn) {
        this.turn = turn;
    }


    public int getTurn() {
        return turn;
    }


    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public Map<String,Object> getDto(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", getTurn());
        dto.put("player", getGamePlayer().getPlayer().getId());
        dto.put("salvoLocations", getSalvoLocations());
        return dto;
    }


}
