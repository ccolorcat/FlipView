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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: cxx
 * Date: 2018-07-31
 * GitHub: https://github.com/ccolorcat
 */
public class FlipView extends FrameLayout {
    private static final int DEFAULT_INTERVAL = 2000;

    private ViewPager mFlipper;
    private FlipAdapter mAdapter;
    private int mSize = 0;
    private TextView mTitle;
    private TabLayout mIndicator;

    private int mCurrent = 0;
    private int mFlipInterval;

    private boolean mInfiniteLoop = false;
    private boolean mReverse = false;
    private boolean mAutoStart = false;
    private boolean mPauseOnTouch = false;

    private boolean mStarted = false;
    private boolean mVisible = false;
    private boolean mUserPresent = true;
    private boolean mUserTouched = false;

    private boolean mRunning = false;

    private List<OnItemSelectedListener> mSelectedListeners;

    public FlipView(@NonNull Context context) {
        this(context, null);
    }

    public FlipView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FlipView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlipView);
        mAutoStart = ta.getBoolean(R.styleable.FlipView_autoStart, false);
        mPauseOnTouch = ta.getBoolean(R.styleable.FlipView_pauseOnTouch, false);
        mFlipInterval = ta.getInteger(R.styleable.FlipView_flipInterval, DEFAULT_INTERVAL);
        int flipperLayout = ta.getResourceId(R.styleable.FlipView_flipperLayout, R.layout.flipper_flip_view);
        boolean titleEnabled = ta.getBoolean(R.styleable.FlipView_titleEnabled, false);
        int titleLayout = ta.getResourceId(R.styleable.FlipView_titleLayout, R.layout.title_flip_view);
        boolean indicatorEnabled = ta.getBoolean(R.styleable.FlipView_indicatorEnabled, false);
        int indicatorLayout = ta.getResourceId(R.styleable.FlipView_indicatorLayout, R.layout.indicator_flip_view);
        mInfiniteLoop = ta.getBoolean(R.styleable.FlipView_infiniteLoop, false);
        mReverse = ta.getBoolean(R.styleable.FlipView_reverse, false);
        ta.recycle();

        mFlipper = inflateAndAttach(this, flipperLayout, R.id.flipper);
        if (titleEnabled || (titleLayout > 0 && titleLayout != R.layout.title_flip_view)) {
            mTitle = inflateAndAttach(this, titleLayout, R.id.title);
        }
        if (indicatorEnabled || (indicatorLayout > 0 && indicatorLayout != R.layout.indicator_flip_view)) {
            mIndicator = inflateAndAttach(this, indicatorLayout, R.id.indicator);
            mIndicator.setupWithViewPager(mFlipper, true);
        }
        mFlipper.addOnPageChangeListener(mPageChangeListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        getContext().registerReceiver(mReceiver, filter);
        if (mAutoStart) {
            startFlipping();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
        getContext().unregisterReceiver(mReceiver);
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = (visibility == VISIBLE);
        updateRunning();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        boolean result = super.dispatchTouchEvent(ev);
        if (mPauseOnTouch && result) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mUserTouched = true;
                    updateRunning();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_UP:
                    mUserTouched = false;
                    updateRunning();
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    public void setReverse(boolean reverse) {
        mReverse = reverse;
    }

    public boolean isReverse() {
        return mReverse;
    }

    public void setOffscreenLimit(int limit) {
        mFlipper.setOffscreenPageLimit(limit);
    }

    public int getOffscreenLimit() {
        return mFlipper.getOffscreenPageLimit();
    }

    public void setAdapter(FlipAdapter adapter) {
        if (adapter == null) {
            throw new NullPointerException("adapter == null");
        }
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
        mAdapter = adapter;
        mAdapter.mInfiniteLoop = mInfiniteLoop;
        mAdapter.registerDataSetObserver(mObserver);
        mFlipper.setAdapter(mAdapter);
        updateSize();
    }

    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    public void addOnItemSelectedListener(OnItemSelectedListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
        if (mSelectedListeners == null) {
            mSelectedListeners = new ArrayList<>(4);
        }
        mSelectedListeners.add(listener);
    }

    public void removeOnItemSelectedListener(OnItemSelectedListener listener) {
        if (mSelectedListeners != null && listener != null) {
            mSelectedListeners.remove(listener);
        }
    }

    /**
     * @param interval flit interval in milliseconds
     */
    public void setFlipInterval(int interval) {
        mFlipInterval = interval;
    }

    public int getFlipInterval() {
        return mFlipInterval;
    }

    public void setAutoStart(boolean autoStart) {
        mAutoStart = autoStart;
    }

    public boolean isAutoStart() {
        return mAutoStart;
    }

    public void setPauseOnTouch(boolean enabled) {
        mPauseOnTouch = enabled;
        mUserTouched = false;
    }

    public boolean isPauseOnTouch() {
        return mPauseOnTouch;
    }

    public boolean isStarted() {
        return mStarted;
    }

    public void startFlipping() {
        mStarted = true;
        updateRunning();
    }

    public void stopFlipping() {
        mStarted = false;
        updateRunning();
    }

    public void showNext() {
        if (mRunning) {
            removeCallbacks(mFlipRunnable);
            postDelayed(mFlipRunnable, mFlipInterval);
        }
        updateItem(1);
    }

    public void showPrevious() {
        if (mRunning) {
            removeCallbacks(mFlipRunnable);
            postDelayed(mFlipRunnable, mFlipInterval);
        }
        updateItem(-1);
    }

    public void setCurrentItem(int position) {
        int fixed = (mInfiniteLoop && mSize > 1) ? position + 1 : position;
        mFlipper.setCurrentItem(fixed, false);
    }

    private void updateItem(int offset) {
        if (mSize > 0) {
            int item = mCurrent + offset;
            if (offset > 0) {
                if (item >= mSize) {
                    item = 0;
                }
            } else if (offset < 0) {
                if (item < 0) {
                    item = mSize - 1;
                }
            }
            mFlipper.setCurrentItem(item, true);
        }
    }

    private void updateRunning() {
        boolean running = mStarted && mVisible && mUserPresent && mSize > 0 && !mUserTouched;
        if (running != mRunning) {
            if (running) {
                postDelayed(mFlipRunnable, mFlipInterval);
            } else {
                removeCallbacks(mFlipRunnable);
            }
            mRunning = running;
        }
    }

    private void updateSize() {
        mSize = mAdapter.getCount();
        if (mSize > 1) {
            if (mInfiniteLoop) {
                hideIndicatorIcon(0);
                hideIndicatorIcon(mSize - 1);
                if (mCurrent == 0) {
                    mFlipper.setCurrentItem(1, false);
                }
            }
            updateRunning();
        } else {
            mRunning = false;
            removeCallbacks(mFlipRunnable);
            if (mSize == 1) {
                mFlipper.setCurrentItem(0, false);
            }
        }
    }

    private void hideIndicatorIcon(int position) {
        TabLayout.Tab tab = mIndicator.getTabAt(position);
        if (tab != null) {
            View customView = tab.getCustomView();
            if (customView == null) {
                customView = new View(getContext());
                tab.setCustomView(customView);
            }
            View parent = (View) customView.getParent();
            customView.setVisibility(View.GONE);
            parent.setVisibility(GONE);
        }
    }

    private final Runnable mFlipRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRunning) {
                if (mReverse) {
                    showPrevious();
                } else {
                    showNext();
                }
            }
        }
    };

    private final DataSetObserver mObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            updateSize();
        }

        @Override
        public void onInvalidated() {
            updateSize();
        }
    };

    private final ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mCurrent = position;
            if (mTitle != null) {
                mTitle.setText(mAdapter.getPageTitle(position));
            }
            if (mInfiniteLoop && mSize > 1 && (position == 0 || position == mSize - 1)) {
                return;
            }
            int fixedPosition = mAdapter.computeFixedPosition(position);
            if (mSelectedListeners != null) {
                for (int i = 0, size = mSelectedListeners.size(); i < size; ++i) {
                    mSelectedListeners.get(i).onItemSelected(fixedPosition);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mInfiniteLoop && mSize > 1 && state == ViewPager.SCROLL_STATE_IDLE) {
                if (mCurrent == mSize - 1) {
                    mFlipper.setCurrentItem(1, false);
                } else if (mCurrent == 0) {
                    mFlipper.setCurrentItem(mSize - 2, false);
                }
            }
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                mUserPresent = false;
                updateRunning();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                mUserPresent = true;
                updateRunning();
            }
        }
    };


    private static <V extends View> V inflateAndAttach(ViewGroup group, @LayoutRes int layoutResId, @IdRes int id) {
        Context context = group.getContext();
        View view = LayoutInflater.from(context).inflate(layoutResId, group, false);
        V v = view.findViewById(id);
        if (v == null) {
            throw new NullPointerException("Can't find view by " + context.getResources().getResourceName(id));
        }
        group.addView(view);
        return v;
    }


    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }
}
