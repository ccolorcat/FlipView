/*
 * Copyright 2018 cxx
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.colorcat.flipview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: cxx
 * Date: 2018-08-31
 * GitHub: https://github.com/ccolorcat
 */
@SuppressWarnings("deprecation")
public abstract class FlipAdapter<VH extends FlipHolder> extends PagerAdapter {
    boolean mInfiniteLoop = false;

    @NonNull
    @Override
    public final Object instantiateItem(@NonNull ViewGroup container, int position) {
        final int fixedPosition = computeFixedPosition(position);
        final int viewType = getItemViewType(fixedPosition);
        VH holder = onCreateHolder(container, viewType);
        holder.viewType = viewType;
        holder.position = fixedPosition;
        onBindView(holder, fixedPosition);
        container.addView(holder.itemView);
        return holder.itemView;
    }

    @Override
    public final void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public final boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public final int getCount() {
        final int itemCount = getItemCount();
        return (mInfiniteLoop && itemCount > 1) ? itemCount + 2 : itemCount;
    }

    @Nullable
    @Override
    public final CharSequence getPageTitle(int position) {
        return getItemPageTitle(computeFixedPosition(position));
    }

    @Override
    public final float getPageWidth(int position) {
        return getItemPageWidth(computeFixedPosition(position));
    }

    protected CharSequence getItemPageTitle(int position) {
        return null;
    }

    protected float getItemPageWidth(int position) {
        return 1.f;
    }

    protected int getItemViewType(int position) {
        return 0;
    }

    protected abstract int getItemCount();

    protected abstract VH onCreateHolder(@NonNull ViewGroup container, int viewType);

    protected abstract void onBindView(@NonNull VH holder, int position);

    int computeFixedPosition(int position) {
        if (!mInfiniteLoop) {
            return position;
        }
        final int count = getCount();
        if (count <= 1) {
            return position;
        }
        if (position == 0) {
            return getItemCount() - 1;
        }
        if (position == count - 1) {
            return 0;
        }
        return position - 1;
    }
}
