package com.flashitdelivery.flash_it.models.remote.body;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yon on 11/09/16.
 */
public class AvailabiltyPeriodBody {
    public int id;
    public String time_start;
    public String time_until;
    public AvailabiltyPeriodBody(String date, String dateFormatPattern) {
        DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
        try
        {
            Date timeStart = dateFormat.parse(date);
            Date timeEnd = dateFormat.parse(date);
            timeStart.setHours(9);
            timeEnd.setHours(22);
            this.time_start = dateFormat.format(timeStart);
            this.time_until = dateFormat.format(timeEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
