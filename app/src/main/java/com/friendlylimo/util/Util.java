package com.friendlylimo.util;

import android.content.Context;

import com.network.InternetDetector;

/**
 * Created by Muzamil Hussain on 9/2/2015.
 */
public class Util {

    // methods
    public static final boolean detectInternet(Context context) {
        InternetDetector detectInt = new InternetDetector(context);
        if (detectInt.detectInternet())
            return true;
        return false;
    }
}
