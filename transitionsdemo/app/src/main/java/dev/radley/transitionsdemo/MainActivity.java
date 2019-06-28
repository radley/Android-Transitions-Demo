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

package dev.radley.transitionsdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import dev.radley.transitionsdemo.R;

import dev.radley.transitionsdemo.data.local.LocalPhoto;
import dev.radley.transitionsdemo.data.local.LocalPhotoDataProvider;
import dev.radley.transitionsdemo.databinding.GalleryImageBinding;
import dev.radley.transitionsdemo.transition.DetailSharedElementEnterCallback;
import dev.radley.transitionsdemo.transition.TransitionCallback;
import dev.radley.transitionsdemo.ui.gallery.OnItemSelectedListener;
import dev.radley.transitionsdemo.ui.gallery.PhotoAdapter;
import dev.radley.transitionsdemo.ui.gallery.PhotoViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    
    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private ArrayList<LocalPhoto> mPhotoList;
    private int mSharedElementTransition;
    private int mDetailViewEnterTransition;
    private int mDetailViewExitTransition;
    
    private final Transition.TransitionListener sharedExitListener =
        new TransitionCallback() {
            @Override
            public void onTransitionEnd(Transition transition) {
                setExitSharedElementCallback((SharedElementCallback) null);
            }
        };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // lock view to portrait on phones
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        
        applySystemUIStyles();
        supportPostponeEnterTransition();
        
        // Listener to reset shared element exit transition callbacks.
        getWindow().getSharedElementExitTransition().addListener(sharedExitListener);
        
        mRecyclerView = findViewById(R.id.image_grid);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getApplicationContext(), R.dimen.thumbnail_padding);
        mRecyclerView.addItemDecoration(itemDecoration);
        
        if (savedInstanceState != null) {
            mPhotoList = savedInstanceState.getParcelableArrayList(IntentUtil.PHOTO_LIST);
        }
        
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.transitions_array, R.layout.spinner_item);
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinner != null) {
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        }
        
        loadPhotos();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(IntentUtil.PHOTO_LIST, mPhotoList);
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onActivityReenter(int resultCode, Intent data) {
    
        supportPostponeEnterTransition();
        
        // Start the postponed transition when the recycler view is ready to be drawn.
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                supportStartPostponedEnterTransition();
                return true;
            }
        });
        
        // Did we select a new image in the other activity?
        if (data == null) {
            return;
        }
        
        final int selectedItem = data.getIntExtra(IntentUtil.SELECTED_ITEM_POSITION, 0);
        mRecyclerView.scrollToPosition(selectedItem);
        
        PhotoViewHolder holder = (PhotoViewHolder) mRecyclerView.findViewHolderForAdapterPosition(selectedItem);
        
        if (holder == null) {
            Log.w(TAG, "onActivityReenter: Holder is null, remapping cancelled.");
            return;
        }
        DetailSharedElementEnterCallback callback = new DetailSharedElementEnterCallback();
        callback.setBinding(holder.getBinding());
        setExitSharedElementCallback(callback);
    }
    
    @NonNull
    private static Intent getDetailActivityStartIntent(Activity host, ArrayList<LocalPhoto> photos, int position,
                                                       int sharedElementTransition, int enterTransition, int exitTransition) {
        
        final Intent intent = new Intent(host, DetailActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putParcelableArrayListExtra(IntentUtil.PHOTO, photos);
        intent.putExtra(IntentUtil.SELECTED_ITEM_POSITION, position);
        
        // Has user selected new transitions?
        if(sharedElementTransition > 0)
            intent.putExtra(IntentUtil.SHARED_ELEMENT_TRANSITION, sharedElementTransition);
        if(enterTransition > 0)
            intent.putExtra(IntentUtil.DETAIL_ENTER_TRANSITION, enterTransition);
        if(exitTransition > 0)
            intent.putExtra(IntentUtil.DETAIL_EXIT_TRANSITION, exitTransition);
        
        return intent;
    }
    
    private ActivityOptionsCompat getActivityOptions(GalleryImageBinding binding) {
        
        // image and unique transition identifier (name)
        List<android.support.v4.util.Pair<View, String>> sharedElements = new ArrayList<>();
        sharedElements.add(Pair.create((View) binding.photo, binding.photo.getTransitionName()));
        
        Pair[] result = new Pair[sharedElements.size()];
        sharedElements.toArray(result);
        
        return ActivityOptionsCompat.makeSceneTransitionAnimation(this, result);
    }
    
    // Extend to load from other sources
    protected void loadPhotos() {
        if (mPhotoList != null) {
            populateGrid();
            
        } else {
            
            // easy resources images for demo
            mPhotoList = LocalPhotoDataProvider.getPhotoList(getApplicationContext());
            
            // Add listeners here. When ready do:
            populateGrid();
        }
    }
    
    private void populateGrid() {
        
        final Activity activity = this;
        
        mRecyclerView.setAdapter(new PhotoAdapter(this, mPhotoList));
        mRecyclerView.addOnItemTouchListener(new OnItemSelectedListener(getApplicationContext()) {
            
            public void onItemSelected(RecyclerView.ViewHolder holder, int position) {
                
                if (!(holder instanceof PhotoViewHolder)) {
                    return;
                }
                
                GalleryImageBinding binding = ((PhotoViewHolder) holder).getBinding();
                
                final Intent intent = getDetailActivityStartIntent(activity, mPhotoList, position,
                    mSharedElementTransition, mDetailViewEnterTransition, mDetailViewExitTransition);
                final ActivityOptionsCompat activityOptions = getActivityOptions(binding);
                
                activity.startActivityForResult(intent, IntentUtil.REQUEST_CODE,
                    activityOptions.toBundle());
            }
        });
    }
    
    /**
     * Customize white status bar and nav bar
     */
    protected void applySystemUIStyles() {
        
        // setSystemUiVisibility flags must be set as group
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.statusPrimaryLight));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.statusPrimaryLight));
        }
    }
    
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        
        // Apply a combination of transitions based on user selection
        
        // TODO redesign DetailView layout to allow for custom transitions, e.g. explode
        // TODO move to extensible class
        switch (position) {
            case 1:
                mSharedElementTransition = R.transition.shared_transition;
                mDetailViewEnterTransition = R.transition.transition_detail_fade;
                mDetailViewExitTransition = R.transition.transition_detail_fade;
                setTransitions(R.transition.transition_explode_grid);
                break;
            case 2:
                mSharedElementTransition = R.transition.shared_transition;
                mDetailViewEnterTransition = R.transition.transition_detail_fade;
                mDetailViewExitTransition = R.transition.transition_detail_fade;
                setTransitions(R.transition.transition_explode_all, R.transition.transition_explode_all_reenter);
                break;
            default:
                mSharedElementTransition = R.transition.shared_transition;
                mDetailViewEnterTransition = R.transition.transition_detail_fade;
                mDetailViewExitTransition = R.transition.transition_detail_fade;
                setTransitions(R.transition.transition_auto);
        }
    }
    
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }
    
    private void setTransitions(int transition) {
        setTransitions(transition, transition);
    }
    
    private void setTransitions(int exit, int reenter) {
        
        TransitionInflater inflater = TransitionInflater.from(this);
        Transition exitTransition = inflater.inflateTransition(exit);
        getWindow().setExitTransition(exitTransition);
        
        Transition reenterTransition = inflater.inflateTransition(reenter);
        getWindow().setReenterTransition(reenterTransition);
    }
    
    /**
     * Add padding to gallery images
     */
    protected class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
        
        private int mItemOffset;
        
        ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }
        
        ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }
        
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }
}
