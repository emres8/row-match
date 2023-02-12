package com.emres.controller;

import com.emres.model.Leaderboard;
import com.emres.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @Autowired
    public LeaderboardController(LeaderboardService leaderboardService) {
       this.leaderboardService = leaderboardService;
    }

    @GetMapping("/{tournamentId}/{groupId}")
    public List<Leaderboard> getLeaderboard(@PathVariable("tournamentId") long tournamentId,
                                            @PathVariable("groupId") long groupId) {
        return leaderboardService.getLeaderboard(tournamentId, groupId);
    }

    @GetMapping("/rank/{tournamentId}/{userId}")
    public ResponseEntity getRank(@PathVariable("tournamentId") Long tournamentId,
                                           @PathVariable("userId") Long userId) {
        return leaderboardService.getRank(tournamentId, userId);
    }
    @PostMapping("/enter/{tournamentId}/{userId}")
    public ResponseEntity enterTournament(@PathVariable("tournamentId") long tournamentId,
                                          @PathVariable("userId") long userId) {
        return leaderboardService.enterTournament(tournamentId, userId);
    }

    @PostMapping("/claim-reward/{tournamentId}/{userId}")
    public ResponseEntity claimReward(@PathVariable("tournamentId") long tournamentId,
                                      @PathVariable("userId") long userId) {
       return leaderboardService.claimReward(tournamentId, userId);
    }
    }

