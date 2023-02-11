package com.emres.repository;


import com.emres.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;




@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Tournament getTournamentById(long tournamentId);

    @Query("SELECT t FROM Tournament t WHERE t.status = 'ACTIVE'")
    Tournament getActiveTournament();
    
}



