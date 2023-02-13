package com.emres.service;


import com.emres.model.User;
import com.emres.model.Tournament;
import com.emres.model.Leaderboard;

import com.emres.repository.LeaderboardRepository;
import com.emres.repository.TournamentRepository;
import com.emres.repository.UserRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {


    @Autowired
    private LeaderboardService leaderboardService;
    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Autowired
    private TournamentRepository tournamentRepository;


    @Test
    public void testCreateUser_Success() {
        // Set up test data
        tournamentService.clearAll();
        userService.clearAll();
        leaderboardService.clearAll();

        User expectedUser = new User();
        expectedUser.setName("John Doe");
        expectedUser.setEmail("johndoe@gmail.com");
        expectedUser.setCoin(5000);
        expectedUser.setLevel(1);

        ResponseEntity<User> response = userService.createUser("John Doe", "johndoe@gmail.com");
        User actualUser = response.getBody();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(actualUser);
        Assertions.assertEquals(expectedUser.getName(), actualUser.getName());
        Assertions.assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        Assertions.assertEquals(expectedUser.getCoin(), actualUser.getCoin());
        Assertions.assertEquals(expectedUser.getLevel(), actualUser.getLevel());
    }

    @Test
    public void testUpdateLevel_Success() {
        // Set up test data
        tournamentService.clearAll();
        userService.clearAll();
        leaderboardService.clearAll();

        User user =  (User) userService.createUser("John Doe", "johndoe@gmail.com").getBody();
        Long userId = user.getId();

        // Start tournament
        tournamentService.startTournament("Test Tournament");

        Tournament tournament = tournamentRepository.getTournamentByName("Test Tournament");

        // Manually register to tournament
        Leaderboard leaderboard = new Leaderboard(tournament.getId(), 1L, user.getId());
        leaderboardRepository.save(leaderboard);

        // Update user level
        ResponseEntity response = userService.updateLevel(userId);
        User updatedUser = (User) response.getBody();

        // Assert user level is increased
        Assertions.assertEquals(user.getLevel() + 1, updatedUser.getLevel());

        // Assert user coin is increased
        Assertions.assertEquals(user.getCoin() + 25, updatedUser.getCoin());

        // Assert user score in tournament leaderboard is increased
        Tournament activeTournament = tournamentRepository.getActiveTournament();
        Leaderboard updatedleaderboard = leaderboardRepository.findByTournamentIdAndUserId(activeTournament.getId(), userId);
        Assertions.assertNotNull(updatedleaderboard);
        Assertions.assertEquals(1, updatedleaderboard.getScore());
    }

    @Test
    public void testUpdateLevel_NoActiveTournament() {
        // Set up test data
        tournamentService.clearAll();
        userService.clearAll();
        leaderboardService.clearAll();

        User user =  (User) userService.createUser("John Doe", "johndoe@gmail.com").getBody();
        Long userId = user.getId();

        // Update user level without starting tournament
        ResponseEntity response = userService.updateLevel(userId);
        User updatedUser = (User) response.getBody();
        // Assert user level is increased
        Assertions.assertEquals(2, updatedUser.getLevel());

        // Assert user coin is increased
        Assertions.assertEquals(5025, updatedUser.getCoin());

    }

    @Test
    public void testUpdateLevel_UserNotFound() {
        // Set up test data
        tournamentService.clearAll();
        userService.clearAll();
        leaderboardService.clearAll();

        // Try to update level for non-existing user
        ResponseEntity response = userService.updateLevel(12345L);


        // Assert response is NOT_FOUND
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
