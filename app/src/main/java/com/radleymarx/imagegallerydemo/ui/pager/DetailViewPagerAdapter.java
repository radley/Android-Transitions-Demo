package com.radleymarx.imagegallerydemo.ui.pager;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.radleymarx.imagegallerydemo.R;
import com.radleymarx.imagegallerydemo.data.model.Photo;
import com.radleymarx.imagegallerydemo.databinding.DetailImageBinding;
import com.radleymarx.imagegallerydemo.ui.DetailSharedElementEnterCallback;

import java.util.List;

/**
 * Adapter for paging detail views.
 */

public class DetailViewPagerAdapter extends PagerAdapter {

    private final List<Photo> allPhotos;
    private final LayoutInflater layoutInflater;
    private final int photoWidth;
    private final Activity mActivity;
    private DetailSharedElementEnterCallback sharedElementCallback;

    public DetailViewPagerAdapter(@NonNull Activity activity, @NonNull List<Photo> photos,
                                  @NonNull DetailSharedElementEnterCallback callback) {
        layoutInflater = LayoutInflater.from(activity);
        allPhotos = photos;
        photoWidth = activity.getResources().getDisplayMetrics().widthPixels;
        mActivity = activity;
        sharedElementCallback = callback;
    }

    @Override
    public int getCount() {
        return allPhotos.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        DetailImageBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.detail_image, container, false);
        binding.setData(allPhotos.get(position));
        onViewBound(binding);
        binding.executePendingBindings();
        container.addView(binding.getRoot());
        return binding;
    }

    private void onViewBound(DetailImageBinding binding) {
    
        
        // .centerCrop()
        RequestOptions options = new RequestOptions()
            .placeholder(R.color.placeholder)
            .skipMemoryCache(false)
            .fitCenter();
        
        Glide.with(mActivity)
                .load(binding.getData().getPhotoUrl(photoWidth))
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable>
                        target, boolean isFirstResource) {
                        // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                        // startPostponedEnterTransition() should also be called on it to get the transition
                        // going in case of a failure.
                        mActivity.startPostponedEnterTransition();
                        return false;
                    }
            
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                        target, DataSource dataSource, boolean isFirstResource) {
                        // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                        // startPostponedEnterTransition() should also be called on it to get the transition
                        // going when the image is ready.
                        mActivity.startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(binding.photo);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (object instanceof DetailImageBinding) {
            sharedElementCallback.setBinding((DetailImageBinding) object);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object instanceof DetailImageBinding
                && view.equals(((DetailImageBinding) object).getRoot());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((DetailImageBinding) object).getRoot());
    }
}
