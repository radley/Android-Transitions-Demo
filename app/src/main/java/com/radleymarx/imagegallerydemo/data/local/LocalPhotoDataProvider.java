package com.radleymarx.imagegallerydemo.data.local;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.radleymarx.imagegallerydemo.R;

import java.util.ArrayList;
import java.util.List;

public class LocalPhotoDataProvider {
    
    @DrawableRes
    static final int[] IMAGE_DRAWABLES = {
        R.drawable.louge,
        R.drawable.mountainview,
        R.drawable.air,
        R.drawable.racecar,
        R.drawable.desks,
        R.drawable.lounge,
        R.drawable.hive,
        R.drawable.logo
    };
    
    @StringRes
    static final int[] IMAGE_NAMES = {
        R.string.louge,
        R.string.mountain_view,
        R.string.air,
        R.string.race_car,
        R.string.desks,
        R.string.lounge,
        R.string.hive,
        R.string.logo
    };
    
    public static ArrayList<LocalPhoto> getPhotoList(Context context) {
    
        ArrayList<LocalPhoto> photos = new ArrayList<>();
        for (int i=0; i< IMAGE_DRAWABLES.length ; i++ )
        {
            photos.add(new LocalPhoto(IMAGE_DRAWABLES[i], context.getResources().getString(IMAGE_NAMES[i])));
        }
        
        return photos;
    };
}
