package com.radleymarx.imagegallerydemo.ui.detail;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.radleymarx.imagegallerydemo.DetailActivity;
import com.radleymarx.imagegallerydemo.R;
import com.radleymarx.imagegallerydemo.data.local.LocalPhoto;
import com.radleymarx.imagegallerydemo.databinding.DetailImageBinding;
import com.radleymarx.imagegallerydemo.transition.DetailSharedElementEnterCallback;

import java.util.List;



public class DetailViewPagerAdapter extends PagerAdapter {

    private final List<LocalPhoto> mPhotoList;
    private final LayoutInflater mLayoutInflater;
    private final Activity mActivity;
    private DetailSharedElementEnterCallback mSharedElementCallback;

    public DetailViewPagerAdapter(@NonNull Activity activity, @NonNull List<LocalPhoto> photos,
                                  @NonNull DetailSharedElementEnterCallback callback) {
        mLayoutInflater = LayoutInflater.from(activity);
        mPhotoList = photos;
        mActivity = activity;
        mSharedElementCallback = callback;
    }

    @Override
    public int getCount() {
        return mPhotoList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        DetailImageBinding binding =
                DataBindingUtil.inflate(mLayoutInflater, R.layout.detail_image, container, false);
        binding.setData(mPhotoList.get(position));
        onViewBound(binding);
        binding.executePendingBindings();
        container.addView(binding.getRoot());
    

        PhotoView imageView = (PhotoView) binding.getRoot().findViewById(R.id.photo);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DetailActivity) mActivity).toggleUI();
                //Toast.makeText(mActivity, "Ping", Toast.LENGTH_SHORT).show();
            }
        });
        
        return binding;
    }


    private void onViewBound(DetailImageBinding binding) {

        RequestOptions options = new RequestOptions()
            .placeholder(R.color.placeholder)
            .skipMemoryCache(true) // true value required for smooth transition
            .fitCenter();
        
        Glide.with(mActivity)
                .load(binding.getData().id)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable>
                        target, boolean isFirstResource) {

                        mActivity.startPostponedEnterTransition();
                        return false;
                    }
            
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                        target, DataSource dataSource, boolean isFirstResource) {

                        mActivity.startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(binding.photo);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (object instanceof DetailImageBinding) {
            mSharedElementCallback.setBinding((DetailImageBinding) object);
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
