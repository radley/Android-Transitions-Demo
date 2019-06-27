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

package dev.radley.transitionsdemo.transition;

import android.app.SharedElementCallback;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import dev.radley.transitionsdemo.databinding.DetailImageBinding;
import dev.radley.transitionsdemo.databinding.GalleryImageBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailSharedElementEnterCallback extends SharedElementCallback {
    
    private DetailImageBinding mCurrentDetailBinding;
    private GalleryImageBinding mCurrentPhotoBinding;
    
    private final List<View> mSharedElementViews;

    public DetailSharedElementEnterCallback() {
        mSharedElementViews = new ArrayList<>();
    }
    
    @Override
    public void onSharedElementStart(List<String> sharedElementNames,
                                     List<View> sharedElements,
                                     List<View> sharedElementSnapshots) {
    }
    
    @Override
    public void onSharedElementEnd(List<String> sharedElementNames,
                                   List<View> sharedElements,
                                   List<View> sharedElementSnapshots) {
    
        for (View sharedElementView : mSharedElementViews) {
            forceSharedElementLayout(sharedElementView);
        }
    }
    
    @Override
    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
        removeObsoleteElements(names, sharedElements, mapObsoleteElements(names));
        mapSharedElement(names, sharedElements, getPhoto());
    }
    
    public void setBinding(@NonNull DetailImageBinding binding) {
        mCurrentDetailBinding = binding;
        mCurrentPhotoBinding = null;
    }
    
    public void setBinding(@NonNull GalleryImageBinding binding) {
        mCurrentPhotoBinding = binding;
        mCurrentDetailBinding = null;
    }
    
    private ImageView getPhoto() {
        if (mCurrentPhotoBinding != null) {
            return mCurrentPhotoBinding.photo;
        } else if (mCurrentDetailBinding != null) {
            return mCurrentDetailBinding.photo;
        } else {
            throw new NullPointerException("Must set a binding before transitioning.");
        }
    }
    
    /**
     * Maps all views that don't start with "android" namespace.
     *
     * @param names All shared element names.
     * @return The obsolete shared element names.
     */
    @NonNull
    private List<String> mapObsoleteElements(List<String> names) {
        List<String> elementsToRemove = new ArrayList<>(names.size());
        for (String name : names) {
            if (name.startsWith("android")) continue;
            elementsToRemove.add(name);
        }
        return elementsToRemove;
    }

    /**
     * Removes obsolete elements from names and shared elements.
     *
     * @param names Shared element names.
     * @param sharedElements Shared elements.
     * @param elementsToRemove The elements that should be removed.
     */
    private void removeObsoleteElements(List<String> names,
                                        Map<String, View> sharedElements,
                                        List<String> elementsToRemove) {
        if (elementsToRemove.size() > 0) {
            names.removeAll(elementsToRemove);
            for (String elementToRemove : elementsToRemove) {
                sharedElements.remove(elementToRemove);
            }
        }
    }

    /**
     * Puts a shared element to transitions and names.
     *
     * @param names The names for this transition.
     * @param sharedElements The elements for this transition.
     * @param view The view to add.
     */
    private void mapSharedElement(List<String> names, Map<String, View> sharedElements, View view) {
        String transitionName = view.getTransitionName();
        names.add(transitionName);
        sharedElements.put(transitionName, view);
    }
    
    private void forceSharedElementLayout(View view) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(),
            View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(view.getHeight(),
            View.MeasureSpec.EXACTLY);
        view.measure(widthSpec, heightSpec);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }


}