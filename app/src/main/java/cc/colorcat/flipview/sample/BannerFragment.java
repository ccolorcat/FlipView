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

package cc.colorcat.flipview.sample;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cc.colorcat.adapter.SimpleVpAdapter;
import cc.colorcat.adapter.VpHolder;
import cc.colorcat.flipview.FlipView;
import cc.colorcat.vangogh.VanGogh;

/**
 * Author: cxx
 * Date: 2018-07-27
 * GitHub: https://github.com/ccolorcat
 */
public class BannerFragment extends Fragment {
    private static final String TAG = FlipView.class.getSimpleName();

    private List<Integer> mData = new ArrayList<>();
    @DrawableRes
    private int[] mImages = {
            R.drawable.one_piece_0,
            R.drawable.one_piece_1,
            R.drawable.one_piece_2,
            R.drawable.one_piece_3,
            R.drawable.one_piece_4,
            R.drawable.one_piece_5,
            R.drawable.one_piece_6
    };
    private FlipView mFlipper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_flipper, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFlipper = view.findViewById(R.id.banner_flipper);
        mFlipper.setAdapter(new SimpleVpAdapter<Integer>(mData, R.layout.item_banner) {
            @Override
            protected void bindView(@NonNull VpHolder holder, Integer data) {
                Log.i(TAG, "bindView, data = " + getResources().getResourceEntryName(data));
                VanGogh.with(holder.getRoot().getContext()).load(data).into((ImageView) holder.get(R.id.iv_banner));
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return "one piece " + position;
            }
        });
        mFlipper.addOnItemSelectedListener(new FlipView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                Log.v(TAG, "onItemSelected, position = " + position);
            }
        });
        batchClick(view, R.id.btn_increase, R.id.btn_reduce, R.id.banner_flipper);
    }

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_increase: {
                    for (int res : mImages) {
                        mData.add(res);
                    }
                    mFlipper.getAdapter().notifyDataSetChanged();
                    break;
                }
                case R.id.btn_reduce: {
                    for (int i = mData.size() >> 1; i > 0; --i) {
                        mData.remove(i);
                    }
                    Log.e(TAG, "data removed, data = " + dataToString());
                    mFlipper.getAdapter().notifyDataSetChanged();
                    break;
                }
                case R.id.banner_flipper: {
                    Log.v(TAG, "bannerFlipper clicked");
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void batchClick(View view, @IdRes int... ids) {
        for (int id : ids) {
            view.findViewById(id).setOnClickListener(mClick);
        }
    }

    private String dataToString() {
        Resources resources = getResources();
        final int size = mData.size();
        List<String> data = new ArrayList<>(mData.size());
        for (int i = 0; i < size; ++i) {
            data.add(resources.getResourceEntryName(mData.get(i)));
        }
        return data.toString();
    }
}
