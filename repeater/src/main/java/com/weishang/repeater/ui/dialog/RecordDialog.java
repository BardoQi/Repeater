package com.weishang.repeater.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.RepeatInfo;
import com.weishang.repeater.blur.BlurDialogFragment;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.utils.DateUtils;
import com.weishang.repeater.utils.RecorderUtils;
import com.weishang.repeater.utils.ViewInject;


/**
 * 录音对话框.blur效果
 */
public class RecordDialog extends BlurDialogFragment {

    @ID(id = R.id.ll_record_layout)
    private View mRecordLayout;
    @ID(id = R.id.tv_record_time)
    private TextView mRecordTime;
    @ID(id = R.id.iv_record_btn)
    private View mRecordBtn;
    private RepeatInfo mRepeatInfo;
    private long mStartRecordTime;
    private Runnable mPostAction;
    private CountDownTimer mTimer;


    public static RecordDialog newInstance(RepeatInfo repeatInfo) {
        RecordDialog fragment = new RecordDialog();
        Bundle args = new Bundle();
        args.putParcelable("item",repeatInfo);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null!=getArguments()){
            mRepeatInfo=getArguments().getParcelable("item");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyDialog);
        View view = getActivity().getLayoutInflater().inflate(R.layout.record_dialog, null);
        ViewInject.init(this, view);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecordLayout.setVisibility(View.VISIBLE);
        ViewCompat.setAlpha(mRecordLayout, 0f);
        ViewCompat.setScaleX(mRecordBtn, 0f);
        ViewCompat.setScaleY(mRecordBtn, 0f);
        ViewCompat.setTranslationY(mRecordTime, -mRecordTime.getHeight());
        ViewCompat.animate(mRecordLayout).alpha(0.8f).setDuration(300).setInterpolator(new LinearInterpolator()).setListener(new ViewPropertyAnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(View view) {
                ViewCompat.animate(mRecordBtn).scaleX(1f).scaleY(1f).setDuration(300).setListener(null);
                ViewCompat.animate(mRecordTime).translationY(0).setDuration(300);
                //开始录音
                mRepeatInfo.record = mRepeatInfo.id + "_" + mRepeatInfo.position;
                mStartRecordTime = System.currentTimeMillis();
                RecorderUtils.startRecorder(mRepeatInfo.record, new Runnable() {
                    @Override
                    public void run() {
                        mTimer=new CountDownTimer(Integer.MAX_VALUE,1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                mRecordTime.setText(DateUtils.getProgressTime(Integer.MAX_VALUE - millisUntilFinished));
                            }

                            @Override
                            public void onFinish() {
                            }
                        };
                        mTimer.start();
                    }
                });

            }
        });



        //点击结束录音
        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                RecorderUtils.stopRecorder();
                if(null!=mTimer){
                    mTimer.cancel();
                    mTimer=null;
                }
                mRepeatInfo
                        .recordLength = System.currentTimeMillis() - mStartRecordTime;
                //更新数据库
                MyDb.repleceData(mRepeatInfo, DbTable.REPEAT_URI, "id=? and position=?", String.valueOf(mRepeatInfo.id), String.valueOf(mRepeatInfo.position));
                ViewCompat.animate(mRecordLayout).alpha(0f).setDuration(300).setInterpolator(new LinearInterpolator()).setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        ViewCompat.animate(mRecordTime).translationY(-mRecordTime.getHeight()).setDuration(300);
                        ViewCompat.animate(mRecordBtn).scaleX(0f).scaleY(0f).setDuration(300).setListener(new ViewPropertyAnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(View view) {
                                mRecordLayout.setVisibility(View.GONE);
                                if(null!=mPostAction){
                                    mPostAction.run();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public void setPostAction(Runnable action){
        this.mPostAction=action;
    }
}
