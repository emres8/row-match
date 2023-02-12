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

import java.util.*;



@Service
public class LeaderboardService {

    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final LeaderboardRepository leaderboardRepository;

    private static Map<Integer, Stack<User>> levelStacks;

    private static final int MAX_STACK_SIZE = 5;
    private static final int MAX_LEVEL = 2000;
    private static final int LEVEL_INTERVAL = 100;

    @Autowired
    public LeaderboardService(UserRepository userRepository, TournamentRepository tournamentRepository, LeaderboardRepository leaderboardRepository) {
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
        this.leaderboardRepository = leaderboardRepository;
        this.levelStacks = initializeLevelStacks();
    }

    private Map<Integer, Stack<User>> initializeLevelStacks() {
        Map<Integer, Stack<User>> levelStacks = new HashMap<>();
        // Initialize stacks for each level interval
        int level = LEVEL_INTERVAL;
        while (level <= MAX_LEVEL) {
            levelStacks.put(level, new Stack<User>());
            level += LEVEL_INTERVAL;
        }

        return levelStacks;
    }


    @Cacheable(value = "leaderboards", key = "#groupId + '-' + #tournamentId")
    public List<Leaderboard> getLeaderboard(long tournamentId, long groupId) {
        try{
        return leaderboardRepository.findAllByTournamentIdAndGroupIdOrderByScoreDesc(tournamentId, groupId);
        }catch(Exception e){
            throw e;
        }
    }

    public ResponseEntity getRank(Long tournamentId, Long userId) {
        try {
            LeaderboardId id = new LeaderboardId(tournamentId, userId);

            Leaderboard leaderboard = leaderboardRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Leaderboard", "id", userId));

            int rank = leaderboardRepository.countByTournamentIdAndGroupIdAndScoreGreaterThan(
                    tournamentId, leaderboard.getGroupId(), leaderboard.getScore());
            return ResponseEntity.ok(rank + 1);
        }catch(Exception e){
            return new ResponseEntity<>(String.format("Unknown error occurred %s", e.getMessage()) , HttpStatus.NOT_FOUND);
        }
    }


    private record TournamentEntryResponse(
            List<User> leaderboard,
            String msg
    ) {
    }

    // Transactional handles concurrency issues
    @Transactional
    @CacheEvict(value = "leaderboard", key = "#groupId + '-' + #tournamentId")
    public ResponseEntity enterTournament(long tournamentId, long userId) {
        try {
            Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", tournamentId));
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

            int level = user.getLevel();
            int coin = user.getCoin();

            // Check if user is eligible to enter
            if (level < 20 || coin < 1000) {
                return new ResponseEntity<>("User is not eligible", HttpStatus.BAD_REQUEST);
            }
            if (level > MAX_LEVEL) {
                return new ResponseEntity<>("Invalid level", HttpStatus.FORBIDDEN);
            }
            // Check if the tournament is active
            if (tournament.getStatus().equals(Tournament.Status.FINISHED)) {
                return new ResponseEntity<>("Tournament is not active for entries", HttpStatus.BAD_REQUEST);
            }

            // Check if user is already in the tournament
            Leaderboard leaderboard = leaderboardRepository.findByTournamentIdAndUserId(tournamentId, userId);

            if (leaderboard != null) {
                return new ResponseEntity<>("User is already in the tournament", HttpStatus.BAD_REQUEST);
            }

            // Check if reward is claimed from last tournament
            // (Order all tournaments of user by createdAt [except the tournament user tries to enter] retrieve first)
            Leaderboard lastLeaderBoard = leaderboardRepository.findFirstByUserIdAndTournamentIdNotOrderByAudit_CreatedAtDesc(userId, tournamentId);

            if (lastLeaderBoard != null && lastLeaderBoard.getIsClaimed().equals(Boolean.FALSE)) {
                return new ResponseEntity<>("Please claim reward for previous tournament before entering a new one", HttpStatus.BAD_REQUEST);
            }


            int stackIndex = ((int) level / LEVEL_INTERVAL) * LEVEL_INTERVAL;


            // Push the user to the corresponding stack
            Stack<User> stack = levelStacks.get(stackIndex);
            stack.push(user);


            // If the stack size reaches the maximum size, create a Leaderboard entry
            if (stack.size() == MAX_STACK_SIZE) {
                long groupId = new Random().nextLong();

                List<Leaderboard> leaderboards = new ArrayList<>();
                while (!stack.isEmpty()) {
                    User temp = stack.pop();
                    leaderboards.add(new Leaderboard(tournamentId, groupId, temp.getId()));
                }
                leaderboardRepository.saveAll(leaderboards);
                return new ResponseEntity<>(leaderboards, HttpStatus.OK);
            }


            user.setCoin(user.getCoin() - 1000);
            userRepository.save(user);


            return new ResponseEntity<>(new TournamentEntryResponse(stack, "Waiting for other players to join..."), HttpStatus.OK);
        } catch (Error e) {
            return new ResponseEntity<>(String.format("Unknown error occurred %s", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // Transactional handles concurrency issues
    @Transactional
    public ResponseEntity claimReward(long tournamentId, long userId) {
        try {
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
            if (leaderboard.getIsClaimed()) {
                return new ResponseEntity<>("Reward is already claimed", HttpStatus.BAD_REQUEST);
            }

            int rank = leaderboardRepository.countByTournamentIdAndGroupIdAndScoreGreaterThan(
                    tournamentId, leaderboard.getGroupId(), leaderboard.getScore());

            int reward = LeaderboardHelpers.calculateReward(rank);

            leaderboard.setIsClaimed(true);
            leaderboardRepository.save(leaderboard);

            user.setCoin(user.getCoin() + reward);
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format("Unknown error occurred %s", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}