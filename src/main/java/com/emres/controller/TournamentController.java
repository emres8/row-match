package com.emres.controller;

import com.emres.exception.ResourceNotFoundException;
import com.emres.model.Tournament;
import com.emres.repository.TournamentRepository;
import com.emres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/tournament")
public class TournamentController {

    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;

    @Autowired
    public TournamentController(UserRepository userRepository, TournamentRepository tournamentRepository) {
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
    }

    @GetMapping()
    public List<Tournament> getTournament(){
        return tournamentRepository.findAll();
    }



    @GetMapping("{tournamentId}")
    public Tournament getUserById(@PathVariable(value = "tournamentId") Long tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", tournamentId));
    }

    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament request) {
        Tournament tournament = new Tournament(request.getName(), request.getStatus());
        Tournament createdTournament = tournamentRepository.save(tournament);
        return ResponseEntity.ok(createdTournament);
    }
    
}

