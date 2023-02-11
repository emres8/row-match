package com.emres.repository;


import com.emres.model.Leaderboard;
import com.emres.model.LeaderboardId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, LeaderboardId> {


    Leaderboard findFirstByUserIdOrderByAudit_CreatedAtDesc(Long userId);

    Leaderboard findByTournamentIdAndUserId(long tournamentId, Long userId);

    Integer countByTournamentIdAndGroupIdAndScoreGreaterThan(long tournamentId, long groupId, Integer score);

    List<Leaderboard> findAllByTournamentIdAndGroupIdOrderByScoreDesc(Long tournamentId, Long groupId);
}