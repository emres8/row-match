package com.emres.model;

import  com.emres.model.LeaderboardId;
import jakarta.persistence.*;



@Entity
@Table(name = "leaderboard")
@IdClass(LeaderboardId.class)
public class Leaderboard {


    @Id
    @Column(name = "tournament_id")
    private Long tournamentId;

    @Id
    @Column(name = "user_id")
    Long userId;

    @Column(name = "group_id")
    private Long groupId;
    @Column(name = "score")
    private Integer score;

    @Column(name = "is_claimed")
    private Boolean isClaimed;
    /*
    @ManyToOne
    @JoinColumn(name = "tournament_id", insertable = false, updatable = false)
    private Tournament tournament;
    */

    @Embedded
    Audit audit;
    public Leaderboard() {
    }

    public Leaderboard(Long tournamentId, Long groupId, Long userId, Integer score, Boolean isClaimed) {
        this.tournamentId = tournamentId;
        this.groupId = groupId;
        this.userId = userId;
        this.score = score;
        this.isClaimed = isClaimed;
        this.audit = new Audit();
    }

    public Leaderboard(Long tournamentId, Long groupId, Long userId) {
        this.tournamentId = tournamentId;
        this.groupId = groupId;
        this.userId = userId;
        this.score = 0;
        this.isClaimed = false;
        this.audit = new Audit();
    }


    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getIsClaimed() {
        return isClaimed;
    }

    public void setIsClaimed(Boolean isClaimed) {
        this.isClaimed = isClaimed;
    }
}
