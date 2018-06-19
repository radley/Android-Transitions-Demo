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

package com.radleymarx.imagegallerydemo.ui.gallery;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.util.concurrent.atomic.AtomicBoolean;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.radleymarx.imagegallerydemo.MainActivity;
import com.radleymarx.imagegallerydemo.R;
import com.radleymarx.imagegallerydemo.ui.preview.PreviewFragment;

import static com.radleymarx.imagegallerydemo.data.ImageData.IMAGE_DRAWABLES;


/**
 * A fragment for displaying a grid of images.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageViewHolder> {
  
  /**
   * A listener that is attached to all ViewHolders to handle imageView loading events and clicks.
   */
  private interface ViewHolderListener {
    
    void onLoadCompleted(ImageView view, int adapterPosition);
    void onItemClicked(View view, int adapterPosition);
  }
  
  private final ViewHolderListener mViewHolderListener;
  
  /**
   * Constructs a new grid adapter for the given {@link Fragment}.
   */
  public GalleryAdapter(Fragment fragment) {
    mViewHolderListener = new ViewHolderListenerImpl(fragment);
  }
  
  @Override
  public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.grid_thumbnail, parent, false);
    return new ImageViewHolder(view, mViewHolderListener);
  }
  
  @Override
  public void onBindViewHolder(ImageViewHolder holder, int position) {
    holder.onBind(holder, position);
  }
  
  @Override
  public int getItemCount() {
    return IMAGE_DRAWABLES.length;
  }
  
  
  /**
   * Default {@link ViewHolderListener} implementation.
   */
  private static class ViewHolderListenerImpl implements ViewHolderListener {
    
    private Fragment fragment;
    private AtomicBoolean enterTransitionStarted;
    
    ViewHolderListenerImpl(Fragment fragment) {
      this.fragment = fragment;
      this.enterTransitionStarted = new AtomicBoolean();
    }
    
    @Override
    public void onLoadCompleted(ImageView view, int position) {
      
      // Call startPostponedEnterTransition only when the 'selected' imageView loading is completed.
      if (MainActivity.currentPosition != position) {
        return;
      }
      if (enterTransitionStarted.getAndSet(true)) {
        return;
      }
      fragment.startPostponedEnterTransition();
    }
    
 
    @Override
    public void onItemClicked(View view, int position) {
      // Update the position.
      MainActivity.currentPosition = position;
  
      ImageView transitioningView = view.findViewById(R.id.thumbnail);
      
      fragment.getFragmentManager()
          .beginTransaction()
          .setReorderingAllowed(true) // Optimize for shared element transition
          .addSharedElement(transitioningView, transitioningView.getTransitionName())
          .replace(R.id.fragment_container, new PreviewFragment(), PreviewFragment.class.getSimpleName())
          .addToBackStack(null)
          .commit();
    }
  }
  
  /**
   * ViewHolder for the grid's images.
   */
  static class ImageViewHolder extends RecyclerView.ViewHolder implements
      View.OnClickListener {
    
    final ImageView imageView;
    final ViewHolderListener viewHolderListener;
    final Matrix matrix = new Matrix();
    Float scale = 1f;
    
    ImageViewHolder(View itemView, ViewHolderListener viewHolderListener) {
      super(itemView);
      this.imageView = itemView.findViewById(R.id.thumbnail);
      this.viewHolderListener = viewHolderListener;
      itemView.findViewById(R.id.thumbnail).setOnClickListener(this);
    }
    
    void onBind(ImageViewHolder viewHolder, int position) {
      Resources res = viewHolder.itemView.getContext().getResources();
      Context context = viewHolder.itemView.getContext(); //<----- Add this line
      int adapterPosition = position;
      setImageView(res, adapterPosition);
      
      // Set the string value of the imageView resource as the unique transition name for the view.
      imageView.setTransitionName(String.valueOf(IMAGE_DRAWABLES[adapterPosition]));
    }
    
    void setImageView(Resources resources, final int adapterPosition) {
  
      Glide.with(imageView.getContext())
          .load(IMAGE_DRAWABLES[adapterPosition])
          .apply(RequestOptions.skipMemoryCacheOf(true)
              .centerCrop())
          .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable>
                target, boolean isFirstResource) {
              viewHolderListener.onLoadCompleted(imageView, adapterPosition);
              return false;
            }
      
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                target, DataSource dataSource, boolean isFirstResource) {
              viewHolderListener.onLoadCompleted(imageView, adapterPosition);
              return false;
            }
          })
          .into(imageView);
    }
    
    @Override
    public void onClick(View view) {
      // Let the listener start the ImagePagerFragment.
      viewHolderListener.onItemClicked(view, getAdapterPosition());
    }
  }
}