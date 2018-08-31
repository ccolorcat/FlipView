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

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Author: cxx
 * Date: 2018-08-31
 * GitHub: https://github.com/ccolorcat
 */
public abstract class SimpleFlipAdapter<T> extends FlipAdapter<FlipHolder> {
    private List<T> mData;
    @LayoutRes
    private int mItemLayout;

    protected SimpleFlipAdapter(@NonNull List<T> data, @LayoutRes int itemLayoutResId) {
        mData = data;
        mItemLayout = itemLayoutResId;
    }

    @Override
    protected int getItemCount() {
        return mData.size();
    }

    @Override
    protected FlipHolder onCreateHolder(@NonNull ViewGroup container, int viewType) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(mItemLayout, container, false);
        return new FlipHolder(itemView);
    }

    @Override
    protected void onBindView(@NonNull FlipHolder holder, int position) {
        onBindView(holder, mData.get(position));
    }

    protected abstract void onBindView(@NonNull FlipHolder holder, T data);
}
