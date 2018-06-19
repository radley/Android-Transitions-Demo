/*
 * Copyright 2018 Radley Marx
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.radleymarx.imagegallerydemo.ui.preview;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.radleymarx.imagegallerydemo.R;

/**
 * A fragment for displaying an image.
 */
public class ImageFragment extends Fragment {
  
  private static final String KEY_IMAGE_RES = "com.radleymarx.imagegallerydemo.key.imageRes";
  
  protected Bitmap mBitmap;
  
  public static ImageFragment newInstance(@DrawableRes int drawableRes) {
    ImageFragment fragment = new ImageFragment();
    Bundle argument = new Bundle();
    argument.putInt(KEY_IMAGE_RES, drawableRes);
    fragment.setArguments(argument);
    return fragment;
  }
  
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    
    ImageView imageView = (ImageView) inflater.inflate(R.layout.fragment_image, container, false);
    
    Bundle arguments = getArguments();
    @DrawableRes int imageRes = arguments.getInt(KEY_IMAGE_RES);
    
    // Give image a unique transition id.
    imageView.setTransitionName(String.valueOf(imageRes));
  
  
    RequestOptions options = new RequestOptions()
        .skipMemoryCache(true)
        .fitCenter();

    Glide.with(this)
        .load(imageRes)
        .apply(options)
        .listener(new RequestListener<Drawable>() {
          @Override
          public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable>
              target, boolean isFirstResource) {
            getParentFragment().startPostponedEnterTransition();
            return false;
          }
        
          @Override
          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
              target, DataSource dataSource, boolean isFirstResource) {
            getParentFragment().startPostponedEnterTransition();
            return false;
          }
        })
        .into(imageView);

    return imageView;
  }


}
