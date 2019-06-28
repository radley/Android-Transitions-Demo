/*
 * Copyright (C) 2018 Radley Marx
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.radleymarx.imagegallerydemo.data.local;


import android.os.Parcel;

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
