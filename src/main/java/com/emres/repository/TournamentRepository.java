package com.emres.repository;


import com.emres.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Tournament getTournamentById(long tournamentId);

    
}



