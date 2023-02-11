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


    public List<Tournament> getTournaments(){
        return tournamentRepository.findAll();
    }


    public Tournament getTournamentById(Long tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", tournamentId));
    }

    public ResponseEntity<Tournament> createTournament(Tournament request) {
        Tournament tournament = new Tournament(request.getName(), request.getStatus());
        Tournament createdTournament = tournamentRepository.save(tournament);
        return ResponseEntity.ok(createdTournament);
    }

    public void startTournament(String name){
        Tournament tournament = new Tournament(name, Tournament.Status.ACTIVE);
        Tournament createdTournament = tournamentRepository.save(tournament);
        if (createdTournament != null) {
            System.out.println("Tournament " + createdTournament.getName() + " is started.");
        }
    }

    public void finishTournament(){
        // Only one tournament is active at each time. Retrieve and set it FINISHED
        Tournament activeTournament = tournamentRepository.getActiveTournament();

        if (activeTournament != null){
            activeTournament.setStatus(Tournament.Status.FINISHED);
            tournamentRepository.save(activeTournament);
            System.out.println("Tournament " + activeTournament.getName() + " is finished.");
        }

    }

}
