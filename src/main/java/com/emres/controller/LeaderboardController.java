package com.emres.controller;

import com.emres.exception.ResourceNotFoundException;
import com.emres.helpers.LeaderboardHelpers;
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

        //TODO : handle when leaderboard exists but user is not in tournament
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
        if (tournament.getStatus().equals(Tournament.Status.FINISHED)) {
            return new ResponseEntity<>("Tournament is not active for entries", HttpStatus.BAD_REQUEST);
        }

        //TODO: check if reward is claimed from last tournament (logic needed to detect last tournament)



        // Check if user is already in the tournament
        Leaderboard leaderboard = leaderboardRepository.findByTournamentIdAndUserId(tournamentId,userId);

        if(leaderboard != null){
            return new ResponseEntity<>("User is already in the tournament", HttpStatus.BAD_REQUEST);
        }

        user.setCoin(user.getCoin() - 1000);
        userRepository.save(user);

        //TODO: group might be full
        long groupId = (long) (user.getLevel() - 1) / 100;

        leaderboard = new Leaderboard(tournamentId, groupId, userId);
        leaderboardRepository.save(leaderboard);


        List<Leaderboard> leaderboards =  leaderboardRepository.findAllByTournamentIdAndGroupIdOrderByScoreDesc(tournamentId, groupId);

        return new ResponseEntity<>(leaderboards, HttpStatus.OK);
    }

    @PostMapping("/claim-reward/{tournamentId}/{userId}")
    public ResponseEntity claimReward(@PathVariable("tournamentId") long tournamentId,
                                          @PathVariable("userId") long userId) {
        Tournament tournament = tournamentRepository.getTournamentById(tournamentId);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        LeaderboardId id = new LeaderboardId(tournamentId, userId);
        Leaderboard leaderboard = leaderboardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leaderboard", "id", userId));

        // Check if the tournament is finished
        if (!tournament.getStatus().equals(Tournament.Status.FINISHED)) {
            return new ResponseEntity<>("Tournament is not finished yet. Cannot claim reward", HttpStatus.BAD_REQUEST);
        }

        // Check if reward is already claimed
        if (leaderboard.getIsClaimed()){
            return new ResponseEntity<>("Reward is already claimed", HttpStatus.BAD_REQUEST);
        }

        int rank = leaderboardRepository.countByTournamentIdAndGroupIdAndScoreGreaterThan(
                tournamentId, leaderboard.getGroupId(), leaderboard.getScore());

        int reward = LeaderboardHelpers.calculateReward(rank);

        leaderboard.setIsClaimed(true);
        leaderboardRepository.save(leaderboard);

        user.setCoin(user.getCoin() + reward);
        return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);

    }
    }

