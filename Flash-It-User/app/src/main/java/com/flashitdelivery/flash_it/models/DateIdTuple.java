package com.flashitdelivery.flash_it.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yon on 15/09/16.
 */
public class DateIdTuple {
    public Date date;
    public int id;
    public DateIdTuple(Date date, int id) {
        this.date = date;
        this.id = id;
    }
    public static List<Date> dateTupleListToDateList(List<DateIdTuple> list) {
        List<Date> dateList = new ArrayList<>();
        for (DateIdTuple dateT : list) {
            dateList.add(dateT.date);
        }
        return dateList;
    }
    public static int getIdFromList(List<DateIdTuple> list, Date date) {
        for (DateIdTuple dateT : list) {
            if (dateT.date.getYear() == date.getYear() &&
                    dateT.date.getMonth() == date.getMonth() &&
                    dateT.date.getDay() == date.getDay()) {
                return dateT.id;
            }
        }
        return -1;
    }
}

