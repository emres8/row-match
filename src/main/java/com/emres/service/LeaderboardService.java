package com.emres.service;

import com.emres.exception.ResourceNotFoundException;
import com.emres.helpers.LeaderboardHelpers;
import com.emres.model.Leaderboard;
import com.emres.model.LeaderboardId;
import com.emres.model.Tournament;
import com.emres.model.User;
import com.emres.repository.LeaderboardRepository;
import com.emres.repository.TournamentRepository;
import com.emres.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class LeaderboardService {

    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final LeaderboardRepository leaderboardRepository;


    @Autowired
    public LeaderboardService(UserRepository userRepository, TournamentRepository tournamentRepository, LeaderboardRepository leaderboardRepository) {
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    @Cacheable(value = "leaderboard", key = "#groupId + '-' + #tournamentId")
    public List<Leaderboard> getLeaderboard(long tournamentId, long groupId) {
        return leaderboardRepository.findAllByTournamentIdAndGroupIdOrderByScoreDesc(tournamentId, groupId);
    }

    public ResponseEntity<Integer> getRank(Long tournamentId, Long userId) {
        LeaderboardId id = new LeaderboardId(tournamentId, userId);

        //TODO : handle when leaderboard exists but user is not in tournament
        Leaderboard leaderboard = leaderboardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leaderboard", "id", userId));

        int rank = leaderboardRepository.countByTournamentIdAndGroupIdAndScoreGreaterThan(
                tournamentId, leaderboard.getGroupId(), leaderboard.getScore());
        return ResponseEntity.ok(rank + 1);
    }

    @Transactional
    @CacheEvict(value = "leaderboard", key = "#groupId + '-' + #tournamentId")
    public ResponseEntity enterTournament(long tournamentId, long userId){
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

        // Check if user is already in the tournament
        Leaderboard leaderboard = leaderboardRepository.findByTournamentIdAndUserId(tournamentId,userId);

        if(leaderboard != null){
            return new ResponseEntity<>("User is already in the tournament", HttpStatus.BAD_REQUEST);
        }

        // Check if reward is claimed from last tournament
        // (Order all tournaments of user by createdAt [except the tournament user tries to enter] retrieve first)
        Leaderboard lastLeaderBoard  = leaderboardRepository.findFirstByUserIdAndTournamentIdNotOrderByAudit_CreatedAtDesc(userId, tournamentId);

        if(lastLeaderBoard != null && lastLeaderBoard.getIsClaimed().equals(Boolean.FALSE)){
            return new ResponseEntity<>("Please claim reward for previous tournament before entering a new one", HttpStatus.BAD_REQUEST);
        }

        user.setCoin(user.getCoin() - 1000);
        userRepository.save(user);


        long groupId = LeaderboardHelpers.generateNewGroupId(user.getLevel(), tournamentId);


        leaderboard = new Leaderboard(tournamentId, groupId, userId);
        leaderboardRepository.save(leaderboard);


        List<Leaderboard> leaderboards =  leaderboardRepository.findAllByTournamentIdAndGroupIdOrderByScoreDesc(tournamentId, groupId);

        return new ResponseEntity<>(leaderboards, HttpStatus.OK);
    }


    public ResponseEntity claimReward(long tournamentId, long userId) {
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
