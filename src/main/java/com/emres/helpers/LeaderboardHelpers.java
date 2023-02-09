package com.emres.helpers;

public class LeaderboardHelpers {
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
}
