package com.weishang.repeater.anim.recycler;

/**
 * Copyright (C) 2015 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;

public class SlideInRightAnimator extends BaseItemAnimator {

    @Override
    protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .alpha(0f)
                .setDuration(getRemoveDuration())
                .setListener(new DefaultRemoveVpaListener(holder))
                .start();
        mRemoveAnimations.add(holder);
    }

    @Override
    protected void preAnimateAdd(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView, holder.itemView.getRootView().getWidth());
    }

    @Override
    protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationX(0)
                .setDuration(getAddDuration())
                .setListener(new DefaultAddVpaListener(holder))
                .start();
        mAddAnimations.add(holder);
    }
}
