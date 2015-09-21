package com.friendlylimo.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

/**
 * Created by Muzamil Hussain on 9/2/2015.
 */
public class TypeFacesManager {

    private Context context;

    private Typeface typeFace;

    public TypeFacesManager(Context context){
        this.context = context;
    }

    public Typeface setTypeFace(AppConstants.TypeFace appTypeFace){

        switch (appTypeFace){
            case RalewayReg:
                typeFace = Typeface.createFromAsset(context.getAssets(),
                        "Raleway-Regular.ttf");
                break;
            case RalewayBold:
                typeFace = Typeface.createFromAsset(context.getAssets(),
                        "Raleway-Bold.ttf");
                break;

        }
        return typeFace;
    }



}
