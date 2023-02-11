package com.emres.controller;

import com.emres.model.Tournament;
import com.emres.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/tournament")
public class TournamentController {

    private final TournamentService tournamentService;


    @Autowired
    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping()
    public List<Tournament> getTournament(){
        return tournamentService.getTournaments();
    }


    @GetMapping("{tournamentId}")
    public Tournament getTournamentById(@PathVariable(value = "tournamentId") Long tournamentId) {
        return tournamentService.getTournamentById(tournamentId);
    }

    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament request) {
        return tournamentService.createTournament(request);
    }
    
}

