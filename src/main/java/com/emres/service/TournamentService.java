package com.emres.service;

import com.emres.exception.ResourceNotFoundException;
import com.emres.model.Tournament;
import com.emres.repository.TournamentRepository;
import com.emres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class TournamentService {

    UserRepository userRepository;
    TournamentRepository tournamentRepository;


    @Autowired
    public TournamentService(UserRepository userRepository, TournamentRepository tournamentRepository) {
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
    }


    public List<Tournament> getTournament(){
        return tournamentRepository.findAll();
    }


    public Tournament getUserById(Long tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", tournamentId));
    }

    public ResponseEntity<Tournament> createTournament(Tournament request) {
        Tournament tournament = new Tournament(request.getName(), request.getStatus());
        Tournament createdTournament = tournamentRepository.save(tournament);
        return ResponseEntity.ok(createdTournament);
    }
}
