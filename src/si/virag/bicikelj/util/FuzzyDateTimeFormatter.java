package si.virag.bicikelj.util;

import android.content.Context;
import android.content.res.Resources;

import java.util.Calendar;

import si.virag.bicikelj.R;

public class FuzzyDateTimeFormatter {

    private final static int SECONDS = 1;
    private final static int MINUTES = 60 * SECONDS;
    private final static int HOURS = 60 * MINUTES;
    private final static int DAYS = 24 * HOURS;
    private final static int WEEKS = 7 * DAYS;
    private final static int MONTHS = 4 * WEEKS;
    private final static int YEARS = 12 * MONTHS;

    public static String getTimeAgo(Context context, Calendar time) {
        long nowSeconds = System.currentTimeMillis();
        int timeDifference = (int) (nowSeconds - time.getTimeInMillis()) / 1000;

        Resources res = context.getResources();

        if (timeDifference < MINUTES) {
            return res.getQuantityString(R.plurals.fuzzydatetime__seconds_ago, timeDifference, timeDifference);
        } else if (timeDifference < HOURS) {
            return res.getQuantityString(R.plurals.fuzzydatetime__minutes_ago, timeDifference / MINUTES, timeDifference / MINUTES);
        } else if (timeDifference < DAYS) {
            return res.getQuantityString(R.plurals.fuzzydatetime__hours_ago, timeDifference / HOURS, timeDifference / HOURS);
        } else if (timeDifference < WEEKS) {
            return res.getQuantityString(R.plurals.fuzzydatetime__days_ago, timeDifference / DAYS, timeDifference / DAYS);
        } else if (timeDifference < MONTHS) {
            return res.getQuantityString(R.plurals.fuzzydatetime__weeks_ago, timeDifference / WEEKS, timeDifference / WEEKS);
        } else if (timeDifference < YEARS) {
            return res.getQuantityString(R.plurals.fuzzydatetime__months_ago, timeDifference / MONTHS, timeDifference / MONTHS);
        } else {
            return res.getQuantityString(R.plurals.fuzzydatetime__years_ago, timeDifference / YEARS, timeDifference / YEARS);
        }

    }

}
