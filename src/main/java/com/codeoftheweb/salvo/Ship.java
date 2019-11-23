package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class    Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator( name = "native", strategy ="native")
    private long id;
    private String shipType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="location")
    private List<String> locations = new ArrayList<>();

    public Ship () {

    }

    public Ship (String shipType, List<String> locations, GamePlayer gamePlayer) {
        this.shipType=shipType;
        this.locations=locations;
        this.gamePlayer=gamePlayer;
    }
    public Ship (String shipType, List<String> locations ) {
        this.shipType=shipType;
        this.locations=locations;

    }

    public String getShipType() {
        return shipType;
    }

    public List<String> getLocations() {
        return locations;
    }


    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }


    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Map<String, Object> getDto(){
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put("type", getShipType());
        dto.put("shipLocations", getLocations());
        return dto;
    }


}
