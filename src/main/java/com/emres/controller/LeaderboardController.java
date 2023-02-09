package com.emres.controller;

import com.emres.exception.ResourceNotFoundException;
import com.emres.model.Leaderboard;
import com.emres.model.LeaderboardId;
import com.emres.model.User;
import com.emres.model.Tournament;
import com.emres.repository.LeaderboardRepository;
import com.emres.repository.TournamentRepository;
import com.emres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/leaderboard")
public class LeaderboardController {

    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final LeaderboardRepository leaderboardRepository;

    @Autowired
    public LeaderboardController(UserRepository userRepository, TournamentRepository tournamentRepository, LeaderboardRepository leaderboardRepository) {
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    @GetMapping("/{tournamentId}/{groupId}")
    public List<Leaderboard> getLeaderboard(@PathVariable("tournamentId") long tournamentId,
                                                                    @PathVariable("groupId") long groupId) {
        return leaderboardRepository.findAllByTournamentIdAndGroupIdOrderByScoreDesc(tournamentId, groupId);
    }

    @GetMapping("/rank/{tournamentId}/{userId}")
    public ResponseEntity<Integer> getRank(@PathVariable("tournamentId") Long tournamentId,
                                           @PathVariable("userId") Long userId) {
        LeaderboardId id = new LeaderboardId(tournamentId, userId);
        Leaderboard leaderboard = leaderboardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leaderboard", "id", userId));

        int rank = leaderboardRepository.countByTournamentIdAndGroupIdAndScoreGreaterThan(
                tournamentId, leaderboard.getGroupId(), leaderboard.getScore());
        return ResponseEntity.ok(rank + 1);
    }
    @PostMapping("/enter/{tournamentId}/{userId}")
    public ResponseEntity enterTournament(@PathVariable("tournamentId") long tournamentId,
                                          @PathVariable("userId") long userId){
        Tournament tournament = tournamentRepository.getTournamentById(tournamentId);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));


        // Check if user is eligible to enter
        if (user.getLevel() < 20 || user.getCoin() < 1000) {
            return new ResponseEntity<>("User is not eligible", HttpStatus.BAD_REQUEST);
        }
        // Check if the tournament is active
        if (!tournament.getStatus().equals(Tournament.Status.FINISHED)) {
            return new ResponseEntity<>("Tournament is not active for entries", HttpStatus.BAD_REQUEST);
        }

        //TODO: check if reward is claimed

        //TODO: group might be full
        long groupId = (user.getLevel() - 1) % 100;

        //TODO: if user levelups and then tries to enter this code won't work

        // Check if user is already in the tournament
        Leaderboard leaderboard = leaderboardRepository.findByTournamentIdAndGroupIdAndUserId(tournamentId,groupId,userId);

        if(leaderboard != null){
            return new ResponseEntity<>("User is already in the tournament", HttpStatus.BAD_REQUEST);
        }

        user.setCoin(user.getCoin() - 1000);
        userRepository.save(user);


        leaderboard = new Leaderboard(tournamentId, groupId, userId, 0);
        leaderboardRepository.save(leaderboard);


        List<Leaderboard> leaderboards =  leaderboardRepository.findAllByTournamentIdAndGroupIdOrderByScoreDesc(tournamentId, groupId);

        return new ResponseEntity<>(leaderboards, HttpStatus.OK);
    }
}
