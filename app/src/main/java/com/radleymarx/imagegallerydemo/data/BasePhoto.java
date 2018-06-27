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
