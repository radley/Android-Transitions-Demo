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

package com.radleymarx.imagegallerydemo.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.TextView;


import com.radleymarx.imagegallerydemo.MainActivity;
import com.radleymarx.imagegallerydemo.R;
import com.radleymarx.imagegallerydemo.adapter.GridAdapter;

import java.util.List;
import java.util.Map;

/**
 * A fragment for displaying a grid of images.
 */
public class GridFragment extends Fragment {
  
  protected RecyclerView mRecyclerView;
  protected View mView;
  
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    
    final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
    LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
    
    mView = (View) localInflater.inflate(R.layout.fragment_grid, container, false);
    
    Toolbar toolbar = (Toolbar) mView.findViewById(R.id.toolbar);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    
    // Remove default label and use custom textView
    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
    title.setText(getResources().getString(R.string.app_name));
    
    mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
    
    GridFragment.ItemOffsetDecoration itemDecoration = new GridFragment.ItemOffsetDecoration(getContext(), R.dimen.thumbnail_padding);
    mRecyclerView.addItemDecoration(itemDecoration);
    
    mRecyclerView.setAdapter(new GridAdapter(this));
    
    applySystemStyles();
    prepareTransitions();
    postponeEnterTransition();
    
    return mView;
  }
  
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    scrollToPosition();
  }
  
  /**
   * Scrolls the recycler view to show the last viewed item in the grid. This is important when
   * navigating back from the grid.
   */
  protected void scrollToPosition() {
    mRecyclerView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
      @Override
      public void onLayoutChange(View v,
                                 int left,
                                 int top,
                                 int right,
                                 int bottom,
                                 int oldLeft,
                                 int oldTop,
                                 int oldRight,
                                 int oldBottom) {
        mRecyclerView.removeOnLayoutChangeListener(this);
        
        final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        View viewAtPosition = layoutManager.findViewByPosition(MainActivity.currentPosition);
        
        // Scroll to position if the view for the current position is null (not currently part of
        // layout manager children), or it's not completely visible.
        if (viewAtPosition == null || layoutManager
            .isViewPartiallyVisible(viewAtPosition, false, true)) {
          mRecyclerView.post(() -> layoutManager.scrollToPosition(MainActivity.currentPosition));
        }
      }
    });
  }
  
  /**
   * Prepares the shared element transition to the pager fragment, as well as the other transitions
   * that affect the flow.
   */
  protected void prepareTransitions() {
    
    setExitTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.grid_exit_transition));
    
    // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
    setExitSharedElementCallback(
        new SharedElementCallback() {
          @Override
          public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            
            // Locate the ViewHolder for the clicked position.
            RecyclerView.ViewHolder selectedViewHolder = mRecyclerView
                .findViewHolderForAdapterPosition(MainActivity.currentPosition);
            
            if (selectedViewHolder == null || selectedViewHolder.itemView == null) {
              return;
            }
            
            // Map the first shared element name to the child ImageView.
            sharedElements.put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.thumbnail));
          }
        });
  }
  
  
  /**
   * customize status bar and nav bar
   */
  protected void applySystemStyles() {
    
    // setSystemUiVisibility flags must be set at the same time
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
      getActivity().getWindow().setStatusBarColor(Color.WHITE);
      getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryLight));
      
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      getActivity().getWindow().setStatusBarColor(Color.WHITE);
    }
  }
  
  
  /**
   * Add padding to gallery images
   */
  public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
    
    private int mItemOffset;
    
    public ItemOffsetDecoration(int itemOffset) {
      
      mItemOffset = itemOffset;
    }
    
    public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
      
      this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }
    
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
      
      super.getItemOffsets(outRect, view, parent, state);
      outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
    }
    
  }
}
