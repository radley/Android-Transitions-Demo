package com.radleymarx.imagegallerydemo.data.local;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.radleymarx.imagegallerydemo.data.BasePhoto;


public class LocalPhoto extends BasePhoto {
    
    public LocalPhoto(int id, String title) {
        super();
        this.id = id;
        this.title = title;
    }
    
    protected LocalPhoto(Parcel in) {
        super(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<LocalPhoto> CREATOR = new Creator<LocalPhoto>() {
        @Override
        public LocalPhoto createFromParcel(Parcel in) {
            return new LocalPhoto(in);
        }
        
        @Override
        public LocalPhoto[] newArray(int size) {
            return new LocalPhoto[size];
        }
    };
}
