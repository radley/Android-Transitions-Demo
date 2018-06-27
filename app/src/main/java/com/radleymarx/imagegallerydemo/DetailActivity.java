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

package com.radleymarx.imagegallerydemo;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.radleymarx.imagegallerydemo.data.local.LocalPhoto;
import com.radleymarx.imagegallerydemo.transition.DetailSharedElementEnterCallback;
import com.radleymarx.imagegallerydemo.ui.detail.DetailViewPagerAdapter;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    
    private static final String STATE_INITIAL_ITEM = "initial";
    private int mInitialItem;
    protected ViewPager mViewPager;
    protected int mSharedElementTransitionId;
    protected int mEnterTransitionId;
    protected int mExitTransitionId;
    protected ActionBar mActionbar;
    protected Toast mToast;
    protected List<LocalPhoto> mPhotoList;
    protected View mBottomMenu;
    protected float mScreenBottomY;
    protected float mBottomMenuY;
    
    private final View.OnClickListener navigationOnClickListener =
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        };
    
    private DetailSharedElementEnterCallback sharedElementCallback;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_detail);
        
        mActionbar = getSupportActionBar();
        mActionbar.setDisplayHomeAsUpEnabled(true);
        
        Intent intent = getIntent();
        
        
        sharedElementCallback = new DetailSharedElementEnterCallback();
        setEnterSharedElementCallback(sharedElementCallback);
        mInitialItem = intent.getIntExtra(IntentUtil.SELECTED_ITEM_POSITION, 0);
        mSharedElementTransitionId = intent.getIntExtra(IntentUtil.SHARED_ELEMENT_TRANSITION, R.transition.shared_transition);
        mEnterTransitionId = intent.getIntExtra(IntentUtil.DETAIL_ENTER_TRANSITION, R.transition.transition_detail_fade);
        mExitTransitionId = intent.getIntExtra(IntentUtil.DETAIL_EXIT_TRANSITION, R.transition.transition_detail_fade);
        
        mPhotoList = intent.<LocalPhoto>getParcelableArrayListExtra(IntentUtil.PHOTO);
        mActionbar.setTitle(((LocalPhoto)mPhotoList.get(mInitialItem)).title);
        
        mBottomMenu = findViewById(R.id.bottom_menu);
        setUpViewPager();
        prepareBottomMenu();
        
        if (savedInstanceState == null) {
            supportPostponeEnterTransition();
        }
        
        setupTransition();
        
        super.onCreate(savedInstanceState);
    }
    
    protected void setupTransition() {
    
        TransitionInflater inflater = TransitionInflater.from(this);

        Transition sharedElementEnterTransition = inflater.inflateTransition(mSharedElementTransitionId);
        getWindow().setSharedElementEnterTransition(sharedElementEnterTransition);


        Transition enterTransition = inflater.inflateTransition(mEnterTransitionId);
        getWindow().setEnterTransition(enterTransition);
    

        Transition exitTransition = inflater.inflateTransition(mExitTransitionId);
        getWindow().setExitTransition(exitTransition);

    }
    
    protected void setUpViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new DetailViewPagerAdapter(this, mPhotoList, sharedElementCallback));
        mViewPager.setCurrentItem(mInitialItem);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            
            @Override
            public void onPageSelected(int position) {
                if(position > 0)
                    mActionbar.setTitle(((LocalPhoto)mPhotoList.get(position)).title);
            }
        });
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_INITIAL_ITEM, mInitialItem);
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mInitialItem = savedInstanceState.getInt(STATE_INITIAL_ITEM, 0);
        super.onRestoreInstanceState(savedInstanceState);
    }
    
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
    
    @Override
    public void onBackPressed() {
        setActivityResult();
        super.onBackPressed();
    }
    
    @Override
    public void finishAfterTransition() {
        setActivityResult();
        super.finishAfterTransition();
    }
    
    private void setActivityResult() {
        if (mInitialItem == mViewPager.getCurrentItem()) {
            setResult(RESULT_OK);
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(IntentUtil.SELECTED_ITEM_POSITION, mViewPager.getCurrentItem());
        setResult(RESULT_OK, intent);
    }
    
    protected boolean mShowUI = true;
    
    public void toggleUI() {
        
        mShowUI = !mShowUI;
        if(mShowUI) showMenuUIs();
        else hideMenuUI();
    }
    
    private void hideMenuUI() {
        
        ObjectAnimator bottomMenuAnimator = ObjectAnimator.ofFloat(mBottomMenu, "y", mBottomMenuY, mScreenBottomY);
        bottomMenuAnimator.setDuration(350);
        bottomMenuAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        bottomMenuAnimator.start();
        
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    
    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showMenuUIs() {
        
        ObjectAnimator bottomMenuAnimator = ObjectAnimator.ofFloat(mBottomMenu, "y", mScreenBottomY, mBottomMenuY);
        bottomMenuAnimator.setDuration(240);
        bottomMenuAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        bottomMenuAnimator.start();
        
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        
    }
    
    protected void prepareBottomMenu() {
        
        addToastToButton(R.id.plus_one_btn, R.string.plus_one);
        addToastToButton(R.id.comment_btn, R.string.comment);
        addToastToButton(R.id.add_btn, R.string.add);
        addToastToButton(R.id.share_btn, R.string.share);
        
        // Manually determining the Y-positions for showing / hiding the bottom menu
        // due to bug with showing / hiding the nav bar
        ViewTreeObserver vto = mBottomMenu.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBottomMenu.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                
                // bottom of screen
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                mScreenBottomY = displayMetrics.heightPixels + mBottomMenu.getHeight();
                
                // menu's Y absolute Y position
                int[] location = new int[2];
                mBottomMenu.getLocationOnScreen(location);
                mBottomMenuY = location[1];
            }
        });
    }
    
    protected void addToastToButton(int buttonView, int id) {
        
        final int stringId = id;
        
        AppCompatImageButton button = (AppCompatImageButton) findViewById(buttonView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(getResources().getString(R.string.menu_select_message, getResources().getString(stringId)));
            }
        });
    }
    
    /**
     * Prevent Toasts from overlapping if more than one are clicked quickly.
     */
    protected void showToast(String message) {
        
        if (mToast != null) {
            mToast.cancel();
        }
        
        mToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        
        // offset toast position to be higher than menu
        mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 240);
        mToast.show();
    }
    
    
}
