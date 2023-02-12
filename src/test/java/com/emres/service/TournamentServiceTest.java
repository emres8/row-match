package com.emres.service;

import com.emres.model.Tournament;
import com.emres.repository.TournamentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@SpringBootTest
@AutoConfigureMockMvc
public class TournamentServiceTest {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TournamentRepository tournamentRepository;
    @Test
    public void testCreateTournament_Success() {
        // Arrange
        tournamentService.clearAll();
        Tournament request = new Tournament("Tournament 1");

        // Act
        ResponseEntity<Tournament> response = tournamentService.createTournament(request);
        Tournament createdTournament = response.getBody();

        // Assert
        Assertions.assertNotNull(createdTournament);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Tournament 1", createdTournament.getName());
        Assertions.assertEquals(Tournament.Status.ACTIVE, createdTournament.getStatus());
    }

    @Test
    public void testFinishTournament_Success() {
        // Arrange
        tournamentService.clearAll();

        tournamentService.startTournament("Tournament 3");

        // Act
        tournamentService.finishTournament();

        // Assert
        Tournament tournament = tournamentRepository.getTournamentByName("Tournament 3");
        Assertions.assertNotNull(tournament);
        Assertions.assertEquals("Tournament 3", tournament.getName());
        Assertions.assertEquals(Tournament.Status.FINISHED, tournament.getStatus());
    }
}
