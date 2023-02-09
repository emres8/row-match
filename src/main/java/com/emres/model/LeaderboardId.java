package com.emres.model;

import java.io.Serializable;

// Create class for composite key
public class LeaderboardId implements Serializable {
    private Long tournamentId;

    private Long userId;


    public LeaderboardId(){

    }
    public LeaderboardId(Long tournamentId, Long userId) {
        this.tournamentId = tournamentId;
        this.userId = userId;
    }
}
