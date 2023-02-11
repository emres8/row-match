package com.emres.helpers;

import com.emres.model.Leaderboard;
import com.emres.repository.LeaderboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class LeaderboardHelpers {


    private static LeaderboardRepository leaderboardRepository;


    @Autowired
    public LeaderboardHelpers(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }


    public static int calculateReward(int rank) {
        if (rank == 1) {
            return 10000;
        } else if (rank == 2) {
            return 5000;
        } else if (rank == 3) {
            return 3000;
        } else if (rank >= 4 && rank <= 10) {
            return 1000;
        } else {
            return 0;
        }
    }

    private static int getNumMembersInGroup(long tournamentId, long groupId){
        List<Leaderboard> groupLeaderboard = leaderboardRepository.findAllByTournamentIdAndGroupId(tournamentId, groupId);
        if(groupLeaderboard != null) {
            return groupLeaderboard.size();
        }
        return 0;
        }

    public static long generateNewGroupId(int level, long tournamentId) {
        //How many groups are allowed to have for same level intervals
        //i.e. for users between 20-100 there can be at most groupIntervalConstant groups
        long groupIntervalConstant = 1000000;


        // Binary search to find available group
        long groupBase = ((level - 1) / 100 + 1) * groupIntervalConstant;
        long left = groupBase, right = groupBase + groupIntervalConstant - 1;
        while (left < right) {
            long mid = (left + right) / 2;
            int numMembers = getNumMembersInGroup(tournamentId, mid);
            if (numMembers >= 20) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }
}
