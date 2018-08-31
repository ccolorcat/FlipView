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

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Author: cxx
 * Date: 2018-08-31
 * GitHub: https://github.com/ccolorcat
 */
public class FlipHolder {
    public final View itemView;
    int viewType;
    int position;

    public FlipHolder(@NonNull View itemView) {
        this.itemView = itemView;
    }

    public int getViewType() {
        return viewType;
    }

    public int getPosition() {
        return position;
    }

    public final <V extends View> V get(@IdRes int id) {
        return itemView.findViewById(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '{' +
                "itemView=" + itemView +
                ", viewType=" + viewType +
                ", position=" + position +
                '}';
    }
}
