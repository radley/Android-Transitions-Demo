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

package com.radleymarx.imagegallerydemo.ui.gallery;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.radleymarx.imagegallerydemo.R;
import com.radleymarx.imagegallerydemo.data.local.LocalPhoto;
import com.radleymarx.imagegallerydemo.databinding.GalleryImageBinding;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

    private final ArrayList<LocalPhoto> mPhotoList;
    private final LayoutInflater mLayoutInflater;

    public PhotoAdapter(@NonNull Context context, @NonNull ArrayList<LocalPhoto> photos) {
        this.mPhotoList = photos;
        mLayoutInflater = LayoutInflater.from(context);
    }
    
    @Override
    public PhotoViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        return new PhotoViewHolder((GalleryImageBinding) DataBindingUtil.inflate(mLayoutInflater,
                R.layout.gallery_image, parent, false));
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        GalleryImageBinding binding = holder.getBinding();
        LocalPhoto data = mPhotoList.get(position);
        binding.setData(data);
        binding.executePendingBindings();
    
        // .centerCrop()
        RequestOptions options = new RequestOptions()
            .skipMemoryCacheOf(true) // true value required for smooth transition
            .centerCrop()
            .placeholder(R.color.placeholder);
        
        Glide.with(mLayoutInflater.getContext())
                .load(holder.getBinding().getData().id)
                .apply(options)
                .into(holder.getBinding().photo);
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    @Override
    public long getItemId(int position) {
        return mPhotoList.get(position).id;
    }
}
