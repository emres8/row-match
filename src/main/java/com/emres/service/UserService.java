package com.emres.service;

import com.emres.exception.ResourceNotFoundException;
import com.emres.model.Leaderboard;
import com.emres.model.User;
import com.emres.model.Tournament;
import com.emres.repository.LeaderboardRepository;
import com.emres.repository.TournamentRepository;
import com.emres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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


    public User.NewUserResponse createUser(User.NewUserRequest request){
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setCoin(5000);
        user.setLevel(1);
        userRepository.save(user);
        User.NewUserResponse response = new User.NewUserResponse(user.getId(),user.getLevel(),user.getCoin());
        return response;
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }


    public User updateLevel(Long userId){
        int coinPerLevel = 25;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setLevel(user.getLevel() + 1);
        user.setCoin((user.getCoin() + coinPerLevel));
        userRepository.save(user);

        // Increase score of user in active tournament
        Tournament active = tournamentRepository.getActiveTournament();
        Leaderboard leaderboard = leaderboardRepository.findByTournamentIdAndUserId(active.getId(), user.getId());
        leaderboard.setScore(leaderboard.getScore() + 1);
        leaderboardRepository.save(leaderboard);

        return user;
    }


}
