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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import static com.radleymarx.imagegallerydemo.data.ImageData.IMAGE_DRAWABLES;
import static com.radleymarx.imagegallerydemo.data.ImageData.IMAGE_NAMES;


public class PreviewAdapter extends FragmentStatePagerAdapter {
  
  public PreviewAdapter(Fragment fragment) {
    // Note: Initialize with the child fragment manager.
    super(fragment.getChildFragmentManager());
  }
  
  @Override
  public int getCount() {
    return IMAGE_DRAWABLES.length;
  }
  
  @Override
  public Fragment getItem(int position) {
    return ImageFragment.newInstance(IMAGE_DRAWABLES[position]);
  }
  
  
  public int getTitle(int position)
  {
    return IMAGE_NAMES[position];
  }
}
