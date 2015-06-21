package com.weishang.repeater.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.animation.ValueAnimator;
import com.squareup.otto.Subscribe;
import com.weishang.repeater.MoreActivity;
import com.weishang.repeater.R;
import com.weishang.repeater.adapter.MyRecyclerAdapter;
import com.weishang.repeater.adapter.RemarkAdapter;
import com.weishang.repeater.anim.recycler.SlideInRightAnimator;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.annotation.Toolbar;
import com.weishang.repeater.bean.Remark;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.event.AddRemarkEvent;
import com.weishang.repeater.provider.BusProvider;
import com.weishang.repeater.utils.ViewInject;

import java.util.ArrayList;

/**
 * Created by momo on 2015/4/25.
 * 备忘信息列表
 */
@Toolbar(title = R.string.all_remark)
public class RemarkListFragment extends Fragment implements View.OnClickListener {
    @ID(id=R.id.toolbar)
    private android.support.v7.widget.Toolbar mToolBar;
    @ID(id = R.id.rv_recycler)
    private RecyclerView mRecyclerView;
    @ID(id = R.id.iv_add_remark, click = true)
    private View mAddRemark;
    private RemarkAdapter mAdapter;
    private int[] mColors;
    private int[] mFontColor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_remark_list,container,false);
        ViewInject.init(this,view);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BusProvider.getInstance().register(this);
        ViewInject.initToolBar(this, mToolBar);
        mColors = new int[]{0xFFFFFAE6, 0xFFF7EAD0, 0xFFE7D3AA, 0xFFDECFB2, 0xFFA5A395, 0xFFE7E6BA, 0xFFCEEABA, 0xFF3E4F43, 0xFF293B2C, 0xFF293343};
        mFontColor = new int[]{0xFF312F2C, 0xFF393325, 0xFF393340, 0xFF393365, 0xFF2D2C29, 0xFF424433, 0xFF46573F, 0xFF909F8D, 0xFF90A487, 0xFFADB6B2};
        ArrayList<Remark> remarks = MyDb.getDatas(DbTable.REMARK_URI, new Remark(), DbTable.REMARK_SELECTION, null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        SlideInRightAnimator itemAnimator = new SlideInRightAnimator();
        mRecyclerView.setItemAnimator(itemAnimator);
        mRecyclerView.setVerticalScrollBarEnabled(true);
        itemAnimator.setAddDuration(300);
        itemAnimator.setRemoveDuration(200);
        mRecyclerView.setAdapter(mAdapter = new RemarkAdapter(getActivity(), remarks, mRecyclerView));
        mAdapter.setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
            @Override
            public void itemClick(View v, int position) {
                Bundle extras = new Bundle();
                extras.putParcelable("item", mAdapter.getItem(position));
                MoreActivity.toActivityforResult(RemarkListFragment.this, getActivity(), AddRemarkFragment.class, extras);
            }
        });
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private ValueAnimator mValueAnimator;
            private int mFirstVisibleItem;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager= (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//                if(firstVisibleItemPosition!=mFirstVisibleItem){
//                    final Remark remark = mAdapter.getItem(mFirstVisibleItem);
//                    final Remark newRemark = mAdapter.getItem(firstVisibleItemPosition);
//                    mFirstVisibleItem=firstVisibleItemPosition;
//                    if(null!=mValueAnimator){
//                        mValueAnimator.cancel();
//                        mValueAnimator=null;
//                    }
//                    mValueAnimator = ValueAnimator.ofFloat(1f);
//                    mValueAnimator.setDuration(300);
//                    mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                            int newColor = AnimationUtils.evaluate(valueAnimator.getAnimatedFraction(), mColors[remark.bg], mColors[newRemark.bg]);
//                            mToolBar.setBackgroundColor(newColor);
//                        }
//                    });
//                    mValueAnimator.start();
//                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.empty_container:
            case R.id.iv_add_remark:
                MoreActivity.toActivity(getActivity(), AddRemarkFragment.class, null);
                break;
            case R.id.more_item:
                //月/周/天筛选

                break;
        }
    }

    @Override
    public void onDestroyView() {
        BusProvider.getInstance().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (AddRemarkFragment.UPDATE_ITEM == resultCode && null != mAdapter && null != data) {
            Remark remark = data.getParcelableExtra("item");
            mAdapter.updateItem(remark);
        }
    }

    @Subscribe
    public void onAddRemarkEvent(AddRemarkEvent event) {
        if (null == event) return;
        Remark remark = event.getRemark();
        if (null != remark) {
            if (mAdapter.isEmpty()) {
            }
            mAdapter.insert(remark);
        }
    }
}
