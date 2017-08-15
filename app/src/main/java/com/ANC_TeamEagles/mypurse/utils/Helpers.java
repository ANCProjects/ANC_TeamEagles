package com.ANC_TeamEagles.mypurse.utils;

import java.text.DecimalFormat;

/**
 * Created by nezspencer on 8/15/17.
 */

public class Helpers {
    public static String formatAmount(double amount){
        return new DecimalFormat("#,###,###,###")
                .format(amount);
    }
}
