package com.emres.repository;


import com.emres.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Tournament getTournamentById(long tournamentId);

    
}



