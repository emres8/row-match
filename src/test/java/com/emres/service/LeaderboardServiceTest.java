package com.emres.service;

import com.emres.model.Leaderboard;
import com.emres.model.Tournament;
import com.emres.model.User;
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

import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class LeaderboardServiceTest {

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
    public void testEnterTournament_SingleUserEligible() {
        // Set up test data
        tournamentService.clearAll();
        userService.clearAll();
        leaderboardService.clearAll();

        User user =  (User) userService.createUser("John Doe", "johndoe@gmail.com").getBody();
        user.setLevel(500);
        user.setCoin(100000);
        userRepository.save(user);

        Tournament tournament = new Tournament("ACTIVE TEST Tournament");
        tournamentRepository.save(tournament);

        //Want to return stack and msg: Waiting for other players
        ResponseEntity response = leaderboardService.enterTournament(tournament.getId(), user.getId());

        LeaderboardService.TournamentEntryResponse entryResponse = (LeaderboardService.TournamentEntryResponse) response.getBody();
        User responseUser = (User) entryResponse.leaderboard().get(0);
        // Verify the results
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        Assertions.assertEquals(user.getId(), responseUser.getId());
        Assertions.assertEquals(user.getName(), responseUser.getName());
        Assertions.assertEquals(user.getEmail(), responseUser.getEmail());
        // Coin should be -1000
        Assertions.assertEquals(user.getCoin() - 1000, responseUser.getCoin());
        Assertions.assertEquals("Waiting for other players to join...", entryResponse.msg());

    }


    @Test
    public void testEnterTournament_20UsersEligible() {
        // This one differs from single user as by design choice if there are less than 20 users they are stored in a stack until stack is completed to 20 users.
        // This tests checks if users are saved in leaderboard

        // Set up test data
        tournamentService.clearAll();
        userService.clearAll();
        leaderboardService.clearAll();

        Tournament tournament = new Tournament("ACTIVE TEST Tournament");
        tournamentRepository.save(tournament);

        for (int i = 0; i < 19; i++) {
            User user =  (User) userService.createUser("John Doe " + i, "johndoe" + i + "@gmail.com").getBody();
            user.setLevel(500);
            user.setCoin(100000);
            userRepository.save(user);
            leaderboardService.enterTournament(tournament.getId(), user.getId());
        }
        // This will complete stack and trigger new Group in tournament
        User user = (User) userService.createUser("John Doe", "johndoe@gmail.com").getBody();
        user.setLevel(500);
        user.setCoin(100000);
        userRepository.save(user);

        ResponseEntity response = leaderboardService.enterTournament(tournament.getId(), user.getId());

        LeaderboardService.TournamentEntryResponse entryResponse = (LeaderboardService.TournamentEntryResponse) response.getBody();
        List<Leaderboard> responseLeaderboardList = (List<Leaderboard>) entryResponse.leaderboard();
        // Verify the results
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        long group_id = responseLeaderboardList.get(0).getGroupId();
        Assertions.assertEquals(20, responseLeaderboardList.size());
        //Check all users are in same tournament group with correct default values
        for (int i = 0; i < responseLeaderboardList.size(); i++) {
            Assertions.assertEquals(group_id, responseLeaderboardList.get(i).getGroupId());
            Assertions.assertEquals(0, responseLeaderboardList.get(i).getScore());
            Assertions.assertEquals(false, responseLeaderboardList.get(i).getIsClaimed());
            Assertions.assertEquals(tournament.getId(), responseLeaderboardList.get(i).getTournamentId());
        }

        Assertions.assertEquals("Entered the tournament, group leaderboard is shown.", entryResponse.msg());

    }

    @Test
    public void testEnterTournament_NotEligible() {
        // Set up test data
        userService.clearAll();
        tournamentService.clearAll();
        leaderboardService.clearAll();

        Tournament tournament = tournamentService.createTournament(new Tournament("Tournament 1")).getBody();
        User user =  (User) userService.createUser("John Doe", "johndoe@gmail.com").getBody();

        // Not enough coin
        user.setCoin(500);
        long tournamentId = tournament.getId();
        long userId = user.getId();

        // Call enterTournament method
        ResponseEntity<Object> response = leaderboardService.enterTournament(tournamentId, userId);

        // Verify the results
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("User is not eligible", response.getBody());
    }

    @Test
    public void enterTournament_TournamentNotActive() {
        // Set up test data
        tournamentService.clearAll();
        userService.clearAll();

        User user =  (User) userService.createUser("John Doe", "johndoe@gmail.com").getBody();
        user.setLevel(500);
        user.setCoin(100000);
        userRepository.save(user);
        Tournament tournament = new Tournament("FINISHED Tournament", Tournament.Status.FINISHED);
        tournamentRepository.save(tournament);

        ResponseEntity response = leaderboardService.enterTournament(tournament.getId(), user.getId());

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Tournament is not active for entries", response.getBody());
    }

    @Test
    public void enterTournament_userAlreadyInTournament_returnsBadRequest() {
        // Set up test data
        tournamentService.clearAll();
        userService.clearAll();
        leaderboardService.clearAll();

        User user = (User) userService.createUser("John Doe", "johndoe@gmail.com").getBody();
        user.setLevel(500);
        user.setCoin(100000);
        userRepository.save(user);

        Tournament tournament = new Tournament("ACTIVE TEST Tournament");
        tournamentRepository.save(tournament);

        // Manually register to tournament
        Leaderboard leaderboard = new Leaderboard(tournament.getId(), 1L, user.getId());
        leaderboardRepository.save(leaderboard);


        ResponseEntity response = leaderboardService.enterTournament(tournament.getId(), user.getId());

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("User is already in the tournament", response.getBody());
    }

    @Test
    public void testGetRank_Success() {
        // Set up test data
        tournamentService.clearAll();
        userService.clearAll();
        leaderboardService.clearAll();

        long tournamentId = 1L;
        long groupId = 1L;
        long userId = 1L;

        // Manually register to tournament group multiple users.
        // Not the best way to test score ordering :/
        Leaderboard leaderboard = new Leaderboard(tournamentId, groupId, userId, 3,false);
        leaderboardRepository.save(leaderboard);
        leaderboardRepository.save(new Leaderboard(tournamentId, groupId, 2L, 2,false));
        leaderboardRepository.save(new Leaderboard(tournamentId, groupId, 6L, 6,false));
        leaderboardRepository.save(new Leaderboard(tournamentId, groupId, 4L, 4,false));

        // When
        ResponseEntity response = leaderboardService.getRank(tournamentId, userId);

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        // Rather than manual enter should create array order by score in test function
        Assertions.assertEquals(3, response.getBody());
    }

    @Test
    public void testClaimReward_Success() {
        // Set up test data
        tournamentService.clearAll();
        userService.clearAll();
        leaderboardService.clearAll();

        User user =  (User) userService.createUser("John Doe", "johndoe@gmail.com").getBody();

        int defaultCoin = user.getCoin();

        userRepository.save(user);

        // Finished so can claim reward
        Tournament tournament = new Tournament("ACTIVE TEST Tournament", Tournament.Status.FINISHED);
        tournamentRepository.save(tournament);

        // Manually register to tournament group
        Leaderboard leaderboard = new Leaderboard(tournament.getId(), 1L, user.getId(), 3,false);
        leaderboardRepository.save(leaderboard);

        // When
        ResponseEntity response = leaderboardService.claimReward(tournament.getId(), user.getId());

        Optional<User> updatedUser = userRepository.findById(user.getId());
        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        // Single player test. Reward should be first place reward
        Assertions.assertEquals(10000, updatedUser.get().getCoin() -  defaultCoin);
    }

    @Test
    public void testClaimReward_rewardAlreadyClaimed_returnsBadRequest() {
        // Set up test data
        tournamentService.clearAll();
        userService.clearAll();
        leaderboardService.clearAll();

        User user =  (User) userService.createUser("John Doe", "johndoe@gmail.com").getBody();
        userRepository.save(user);

        // Finished so can claim reward
        Tournament tournament = new Tournament("ACTIVE TEST Tournament", Tournament.Status.FINISHED);
        tournamentRepository.save(tournament);

        // Manually register to tournament group
        Leaderboard leaderboard = new Leaderboard(tournament.getId(), 1L, user.getId(), 3,true);
        leaderboardRepository.save(leaderboard);

        // When
        ResponseEntity response = leaderboardService.claimReward(tournament.getId(), user.getId());

        // Then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Reward is already claimed", response.getBody());


    }




    }
