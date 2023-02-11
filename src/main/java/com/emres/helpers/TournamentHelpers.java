package com.emres.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TournamentHelpers {

    public static String createDailyTournamentName(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return "Tournament-" + dateFormat.format(new Date());

    }

}
