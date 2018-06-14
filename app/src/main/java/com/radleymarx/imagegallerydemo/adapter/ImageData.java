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


package com.radleymarx.imagegallerydemo.adapter;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.radleymarx.imagegallerydemo.R;

/**
 * Holds the image resource references used by the grid and the pager fragments.
 */
abstract class ImageData {
  
  @DrawableRes
  static final int[] IMAGE_DRAWABLES = {
      R.drawable.louge,
      R.drawable.mountainview,
      R.drawable.air,
      R.drawable.racecar,
      R.drawable.desks,
      R.drawable.lounge,
      R.drawable.hive,
      R.drawable.logo
  };
  
  @StringRes
  static final int[] IMAGE_NAMES = {
      R.string.louge,
      R.string.mountain_view,
      R.string.air,
      R.string.race_car,
      R.string.desks,
      R.string.lounge,
      R.string.hive,
      R.string.logo
  };
  
}
