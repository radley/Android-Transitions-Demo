package com.radleymarx.imagegallerydemo.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public abstract class BasePhoto implements Parcelable {
    
    public int id;
    public String title;
    
    public BasePhoto() {
    }
    
    protected BasePhoto(Parcel in) {
        id = in.readInt();
        title = in.readString();
    }

    
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
    }
}
