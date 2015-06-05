package son.nt.here.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sonnt on 6/5/15.
 */
public class TsDateUtils {

    private static final String DATE_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";

    public static String convertLongToString(long time, String format) {
        String myDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        myDate = sdf.format(date);
        return myDate;
    }

    public static String getStringDate(long time) {
        return convertLongToString(time, DATE_FORMAT_1);
    }
}
