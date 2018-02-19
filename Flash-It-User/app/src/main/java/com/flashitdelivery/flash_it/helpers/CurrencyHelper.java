package com.flashitdelivery.flash_it.helpers;

import java.text.NumberFormat;

/**
 * Created by Lindan on 2016-09-02.
 */
public class CurrencyHelper
{
    private CurrencyHelper() {}



    public static String returnAmt(double value)
    {

        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String formattedValue = nf.format(value);
        return formattedValue;
    }

}
