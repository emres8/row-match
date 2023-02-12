package com.emres.service;

import com.emres.exception.ResourceNotFoundException;
import com.emres.model.Leaderboard;
import com.emres.model.User;
import com.emres.model.Tournament;
import com.emres.repository.LeaderboardRepository;
import com.emres.repository.TournamentRepository;
import com.emres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final TournamentRepository tournamentRepository;

    private final LeaderboardRepository leaderboardRepository;

    @Autowired
    public UserService(UserRepository userRepository, TournamentRepository tournamentRepository, LeaderboardRepository leaderboardRepository){
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }


    public ResponseEntity createUser(String name, String email){
        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setCoin(5000);
            user.setLevel(1);
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format("Unknown error occurred %s", e.getMessage()), HttpStatus.NOT_FOUND);
    }
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }


    public ResponseEntity updateLevel(Long userId) {
        try {

            int coinPerLevel = 25;
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

            user.setLevel(user.getLevel() + 1);
            user.setCoin((user.getCoin() + coinPerLevel));
            userRepository.save(user);

            // Increase score of user in active tournament
            Tournament active = tournamentRepository.getActiveTournament();

            if(active == null){
                //There is no active tournament
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
            Leaderboard leaderboard = leaderboardRepository.findByTournamentIdAndUserId(active.getId(), user.getId());
            if (leaderboard != null) {
                leaderboard.setScore(leaderboard.getScore() + 1);
                leaderboardRepository.save(leaderboard);
            }

            return new ResponseEntity(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format("Unknown error occurred %s", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    public void clearAll(){
        userRepository.deleteAll();
    }


}
