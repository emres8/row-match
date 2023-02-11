package com.emres.tasks;

import com.emres.helpers.TournamentHelpers;
import com.emres.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TournamentScheduler {

    private final TournamentService tournamentService;



    @Autowired
    public TournamentScheduler(TournamentService tournamentService){
        this.tournamentService = tournamentService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void startNewTournament() {
        String tournament_name = TournamentHelpers.createDailyTournamentName();
        tournamentService.startTournament(tournament_name);
    }

    @Scheduled(cron = "0 0 20 * * *")
    public void finishTournament() {
        tournamentService.finishTournament();
    }
}
