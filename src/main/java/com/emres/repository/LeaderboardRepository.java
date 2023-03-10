package com.emres.repository;


import com.emres.model.Leaderboard;
import com.emres.model.LeaderboardId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, LeaderboardId> {


    Leaderboard findFirstByUserIdAndTournamentIdNotOrderByAudit_CreatedAtDesc(Long userId, Long tournamentId);

    Leaderboard findByTournamentIdAndUserId(long tournamentId, Long userId);

    Integer countByTournamentIdAndGroupIdAndScoreGreaterThan(long tournamentId, long groupId, Integer score);

    List<Leaderboard> findAllByTournamentIdAndGroupIdOrderByScoreDesc(Long tournamentId, Long groupId);

    List<Leaderboard> findAllByTournamentIdAndGroupId(Long tournamentId, Long groupId);
}