package com.flashitdelivery.flash_it_partner.util;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;


import java.text.SimpleDateFormat;

/**
 * Created by Lindan on 2016-08-07.
 */
public class XAxisMonthFormatter implements AxisValueFormatter
{
    private SimpleDateFormat mFormat;

    public XAxisMonthFormatter()
    {
        mFormat = new SimpleDateFormat("MMM");

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis)
    {
        if (value == 1)
        {
            return "Jan";
        }
        else if (value == 2)
        {
            return "Feb";
        }
        else if (value == 3)
        {
            return "Mar";
        }
        else if (value == 4)
        {
            return "Apr";
        }
        else if (value == 5)
        {
            return "May";
        }
        else if (value == 6)
        {
            return "Jun";
        }
        else if (value == 7)
        {
            return "Jul";
        }
        else if (value == 8)
        {
            return "Aug";
        }
        else if (value == 9)
        {
            return "Sep";
        }
        else if (value == 10)
        {
            return "Oct";
        }
        else if (value == 11)
        {
            return "Nov";
        }
        else if (value == 12)
        {
            return "Dec";
        }
        else
        {
            return "";
        }
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
