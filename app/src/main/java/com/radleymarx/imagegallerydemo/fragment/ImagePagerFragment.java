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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.radleymarx.imagegallerydemo.MainActivity;
import com.radleymarx.imagegallerydemo.R;
import com.radleymarx.imagegallerydemo.adapter.ImagePagerAdapter;

import java.util.List;
import java.util.Map;

/**
 * A fragment for displaying a pager of images.
 */
public class ImagePagerFragment extends Fragment {
  
  protected View mView;
  protected ViewPager mViewPager;
  protected Toast mToast;
  protected ImagePagerAdapter mImagePagerAdapter;
  protected Toolbar mToolbar;
  protected TextView mTitle;
  
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    
    final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.DetailTheme);
    LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
    
    mView = (View) localInflater.inflate(R.layout.fragment_pager, container, false);
    
    mToolbar = (Toolbar) mView.findViewById(R.id.toolbar);
    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

    
    mViewPager = (ViewPager) mView.findViewById(R.id.view_pager);
    mImagePagerAdapter = new ImagePagerAdapter(this);
    mViewPager.setAdapter(mImagePagerAdapter);
    
    // Set the current position and add a listener that will update the selection coordinator when
    // paging the images.
    mViewPager.setCurrentItem(MainActivity.currentPosition);
    mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      
      @Override
      public void onPageSelected(int position) {
        MainActivity.currentPosition = position;
        if(position > 0)
          mTitle.setText(getResources().getString(mImagePagerAdapter.getTitle(MainActivity.currentPosition)));
      }
    });
  
  
    // Use centered textView in Toolbar. Called after mImagePagerAdapter is instantiated.
    mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
    mTitle.setText(getResources().getString(mImagePagerAdapter.getTitle(MainActivity.currentPosition)));
  
    applySystemStyles();
    prepareLowerMenu();
  
    prepareSharedElementTransition();
    
    // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
    if (savedInstanceState == null) {
      postponeEnterTransition();
    }
    return mView;
  }
  
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    
  }
  
  
  /**
   * Prepares the shared element transition from and back to the grid fragment.
   */
  protected void prepareSharedElementTransition() {
    
    Transition transition = TransitionInflater.from(getContext())
        .inflateTransition(R.transition.image_shared_element_transition);
    
    setSharedElementEnterTransition(transition);
    
    // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
    setEnterSharedElementCallback(
        new SharedElementCallback() {
          @Override
          public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            
            // Locate the image view at the primary fragment (the ImageFragment that is currently
            // visible). To locate the fragment, call instantiateItem with the selection position.
            // At this stage, the method will simply return the fragment at the position and will
            // not create a new one.
            Fragment currentFragment = (Fragment) mViewPager.getAdapter()
                .instantiateItem(mViewPager, MainActivity.currentPosition);
            
            View view = currentFragment.getView();
            if (view == null) {
              return;
            }
            
            // Map the first shared element name to the child ImageView.
            sharedElements.put(names.get(0), view.findViewById(R.id.image));
          }
        });
  }
  
  protected void prepareLowerMenu() {
    
    prepareMenuButton(R.id.plus_one_btn, R.string.plus_one);
    prepareMenuButton(R.id.comment_btn, R.string.comment);
    prepareMenuButton(R.id.add_btn, R.string.add);
    prepareMenuButton(R.id.share_btn, R.string.share);
  }
  
  protected void prepareMenuButton(int buttonView, int name) {
    
    AppCompatImageButton button = (AppCompatImageButton) mView.findViewById(buttonView);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showToast(getResources().getString(R.string.menu_select_message, getResources().getString(name)));
      }
    });
  }
  
  /**
   * customize status bar and nav bar
   */
  protected void applySystemStyles() {
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      
      getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
      getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark));
      getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark));
      
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      
      getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
      getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark));
    }
  }
  
  
  /**
   * Prevent Toasts from overlapping if clicked too quickly.
   */
  protected void showToast(String message) {
    
    if (mToast != null) {
      mToast.cancel();
    }
    
    mToast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
    
    // offset toast position to be higher than menu
    mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 240);
    mToast.show();
  }
  
  
  
  
}
