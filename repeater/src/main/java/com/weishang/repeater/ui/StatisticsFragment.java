package com.weishang.repeater.ui;


import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.HorizontalBarChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BaseEasingMethod;
import com.db.chart.view.animation.easing.bounce.BounceEaseOut;
import com.db.chart.view.animation.easing.cubic.CubicEaseOut;
import com.db.chart.view.animation.easing.elastic.ElasticEaseOut;
import com.db.chart.view.animation.easing.quint.QuintEaseOut;
import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.annotation.Toolbar;
import com.weishang.repeater.utils.ViewInject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author momo
 * @date 2015/3/8
 *
 */
@Toolbar(title = R.string.use_collect)
public class StatisticsFragment extends Fragment {
    @ID(id=R.id.toolbar)
    private android.support.v7.widget.Toolbar mToolBar;
    @ID(id = R.id.barchart)
    private BarChartView mBarChart;
    @ID(id = R.id.horbarchart)
    private HorizontalBarChartView mHorBarChart;

    private Handler mHandler;
    private final TimeInterpolator enterInterpolator = new DecelerateInterpolator(1.5f);
    private final TimeInterpolator exitInterpolator = new AccelerateInterpolator();
    private final static int BAR_MAX = 10;
    private final static int BAR_MIN = 0;
    private final static String[] barLabels = {"YAK", "ANT", "GNU", "OWL", "APE", "JAY", "COD"};
    private final static float[][] barValues = {{6.5f, 7.5f, 3.5f, 3.5f, 10f, 4.5f, 5.5f},
            {9.5f, 3.5f, 5.5f, 4.5f, 8.5f, 6.5f, 5.5f}};
    private Paint mBarGridPaint;
    private TextView mBarTooltip;



    /**
     * HorizontalBar
     */
    private final static int HOR_BAR_MAX = 8;
    private final static int HOR_BAR_MIN = 0;
    private final static String[] horBarLabels = {"YAK", "ANT", "GNU", "OWL", "APE", "JAY", "COD"};
    private final static float[][] horBarValues = {{6f, 7f, 2f, 4f, 3f, 2f, 5f},
            {7f, 4f, 3f, 1f, 6f, 2f, 4f}};
    private Paint mHorBarGridPaint;
    private TextView mHorBarTooltip;

    /**
     * Order
     */
    private static ImageButton mOrderBtn;
    private final static int[] beginOrder = {0, 1, 2, 3, 4, 5, 6};
    private final static int[] middleOrder = {3, 2, 4, 1, 5, 0, 6};
    private final static int[] endOrder = {6, 5, 4, 3, 2, 1, 0};
    private static float mCurrOverlapFactor;
    private static int[] mCurrOverlapOrder;
    private static float mOldOverlapFactor;
    private static int[] mOldOverlapOrder;

    private final static float[][] lineValues = {{-5f, 6f, 2f, 9f, 0f, 1f, 5f},
            {-9f, -2f, -4f, -3f, -7f, -5f, -3f}};


    /**
     * Ease
     */
    private static ImageButton mEaseBtn;
    private static BaseEasingMethod mCurrEasing;
    private static BaseEasingMethod mOldEasing;


    /**
     * Enter
     */
    private static ImageButton mEnterBtn;
    private static float mCurrStartX;
    private static float mCurrStartY;
    private static float mOldStartX;
    private static float mOldStartY;


    /**
     * Alpha
     */
    private static ImageButton mAlphaBtn;
    private static int mCurrAlpha;
    private static int mOldAlpha;

    private final Runnable mEnterEndAction = new Runnable() {
        @Override
        public void run() {
//            mPlayBtn.setEnabled(true);
        }
    };

    private final Runnable mExitEndAction = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    mOldOverlapFactor = mCurrOverlapFactor;
                    mOldOverlapOrder = mCurrOverlapOrder;
                    mOldEasing = mCurrEasing;
                    mOldStartX = mCurrStartX;
                    mOldStartY = mCurrStartY;
                    mOldAlpha = mCurrAlpha;
                    updateBarChart();
                    updateHorBarChart();
                }
            }, 500);
        }
    };

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    public StatisticsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        ViewInject.init(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewInject.initToolBar(this,mToolBar);
        mCurrOverlapFactor = 1;
        mCurrEasing = new QuintEaseOut();
        mCurrStartX = -1;
        mCurrStartY = 0;
        mCurrAlpha = -1;

        mOldOverlapFactor = 1;
        mOldEasing = new QuintEaseOut();
        mOldStartX = -1;
        mOldStartY = 0;
        mOldAlpha = -1;

        mHandler = new Handler();

        initBarChart();
        initHorBarChart();

        updateBarChart();
        updateHorBarChart();

    }

        /*------------------------------------*
     *              BARCHART              *
	 *------------------------------------*/

    private void initBarChart() {
        mBarChart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                if (mBarTooltip == null)
                    showBarTooltip(setIndex, entryIndex, rect);
                else
                    dismissBarTooltip(setIndex, entryIndex, rect);
            }
        });
        mBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBarTooltip != null)
                    dismissBarTooltip(-1, -1, null);
            }
        });

        mBarGridPaint = new Paint();
        mBarGridPaint.setColor(App.getResourcesColor(R.color.yellow));
        mBarGridPaint.setStyle(Paint.Style.STROKE);
        mBarGridPaint.setAntiAlias(true);
        mBarGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
    }

    private void updateBarChart() {

        mBarChart.reset();

        BarSet barSet = new BarSet();
        Bar bar;
        for (int i = 0; i < barLabels.length; i++) {
            bar = new Bar(barLabels[i], barValues[0][i]);
            if (i == 4)
                bar.setColor(this.getResources().getColor(R.color.bar_highest));
            else
                bar.setColor(this.getResources().getColor(R.color.bar_fill1));
            barSet.addBar(bar);
        }
        mBarChart.addData(barSet);

        barSet = new BarSet();
        barSet.addBars(barLabels, barValues[1]);
        barSet.setColor(this.getResources().getColor(R.color.bar_fill2));
        mBarChart.addData(barSet);

        mBarChart.setSetSpacing(Tools.fromDpToPx(3));
        mBarChart.setBarSpacing(Tools.fromDpToPx(14));

        mBarChart.setBorderSpacing(0)
                .setAxisBorderValues(BAR_MIN, BAR_MAX, 2)
                .setGrid(BarChartView.GridType.FULL, mBarGridPaint)
                .setYAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYLabels(YController.LabelPosition.NONE)
                .show(getAnimation(true).setEndAction(mEnterEndAction));
    }

    /*------------------------------------*
     *         HORIZONTALBARCHART         *
	 *------------------------------------*/

    private void initHorBarChart() {
        mHorBarChart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                if (mHorBarTooltip == null)
                    showHorBarTooltip(setIndex, entryIndex, rect);
                else
                    dismissHorBarTooltip(setIndex, entryIndex, rect);
            }
        });
        mHorBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHorBarTooltip != null)
                    dismissHorBarTooltip(-1, -1, null);
            }
        });

        mHorBarGridPaint = new Paint();
        mHorBarGridPaint.setColor(this.getResources().getColor(R.color.yellow));
        mHorBarGridPaint.setStyle(Paint.Style.STROKE);
        mHorBarGridPaint.setAntiAlias(true);
        mHorBarGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
    }

    private void updateHorBarChart() {

        mHorBarChart.reset();

        BarSet barSet = new BarSet();
        Bar bar;
        for (int i = 0; i < horBarLabels.length; i++) {
            bar = new Bar(horBarLabels[i], horBarValues[0][i]);
            bar.setColor(this.getResources().getColor(R.color.horbar_fill));
            barSet.addBar(bar);
        }
        mHorBarChart.addData(barSet);
        mHorBarChart.setBarSpacing(Tools.fromDpToPx(3));

        mHorBarChart.setBorderSpacing(0)
                .setAxisBorderValues(HOR_BAR_MIN, HOR_BAR_MAX, 2)
                .setGrid(HorizontalBarChartView.GridType.VERTICAL, mHorBarGridPaint)
                .setXAxis(false)
                .setYAxis(false)
                .setXLabels(XController.LabelPosition.NONE)
                .show(getAnimation(true).setEndAction(mEnterEndAction));
    }

    @SuppressLint("NewApi")
    private void showBarTooltip(int setIndex, int entryIndex, Rect rect) {

        mBarTooltip = (TextView) View.inflate(getActivity(), R.layout.bar_tooltip, null);
        mBarTooltip.setText(Integer.toString((int) barValues[setIndex][entryIndex]));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(rect.width(), rect.height());
        layoutParams.leftMargin = rect.left;
        layoutParams.topMargin = rect.top;
        mBarTooltip.setLayoutParams(layoutParams);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mBarTooltip.setAlpha(0);
            mBarTooltip.setScaleY(0);
            mBarTooltip.animate()
                    .setDuration(200)
                    .alpha(1)
                    .scaleY(1)
                    .setInterpolator(enterInterpolator);
        }
        mBarChart.showTooltip(mBarTooltip);
    }

    @SuppressLint("NewApi")
    private void dismissBarTooltip(final int setIndex, final int entryIndex, final Rect rect) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBarTooltip.animate()
                    .setDuration(100)
                    .scaleY(0)
                    .alpha(0)
                    .setInterpolator(exitInterpolator).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mBarChart.removeView(mBarTooltip);
                    mBarTooltip = null;
                    if (entryIndex != -1)
                        showBarTooltip(setIndex, entryIndex, rect);
                }
            });
        } else {
            mBarChart.dismissTooltip(mBarTooltip);
            mBarTooltip = null;
            if (entryIndex != -1)
                showBarTooltip(setIndex, entryIndex, rect);
        }
    }

    @SuppressLint("NewApi")
    private void showHorBarTooltip(int setIndex, int entryIndex, Rect rect) {

        mHorBarTooltip = (TextView) View.inflate(getActivity(), R.layout.horbar_tooltip, null);
        mHorBarTooltip.setText(Integer.toString((int) horBarValues[setIndex][entryIndex]));
        mHorBarTooltip.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) Tools.fromDpToPx(15), (int) Tools.fromDpToPx(15));
        layoutParams.leftMargin = rect.right;
        layoutParams.topMargin = rect.top - (int) (Tools.fromDpToPx(15) / 2 - (rect.bottom - rect.top) / 2);
        mHorBarTooltip.setLayoutParams(layoutParams);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mHorBarTooltip.setAlpha(0);
            mHorBarTooltip.animate()
                    .setDuration(200)
                    .alpha(1)
                    .translationX(10)
                    .setInterpolator(enterInterpolator);
        }

        mHorBarChart.showTooltip(mHorBarTooltip);
    }

    @SuppressLint("NewApi")
    private void dismissHorBarTooltip(final int setIndex, final int entryIndex, final Rect rect) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mHorBarTooltip.animate()
                    .setDuration(100)
                    .alpha(0)
                    .translationX(-10)
                    .setInterpolator(exitInterpolator).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mHorBarChart.removeView(mHorBarTooltip);
                    mHorBarTooltip = null;
                    if (entryIndex != -1)
                        showHorBarTooltip(setIndex, entryIndex, rect);
                }
            });
        } else {
            mHorBarChart.dismissTooltip(mHorBarTooltip);
            mHorBarTooltip = null;
            if (entryIndex != -1)
                showHorBarTooltip(setIndex, entryIndex, rect);
        }
    }

    /*------------------------------------*
     *               GETTERS              *
	 *------------------------------------*/
    private Animation getAnimation(boolean newAnim) {
        if (newAnim)
            return new Animation()
                    .setAlpha(mCurrAlpha)
                    .setEasing(mCurrEasing)
                    .setOverlap(mCurrOverlapFactor, mCurrOverlapOrder)
                    .setStartPoint(mCurrStartX, mCurrStartY);
        else
            return new Animation()
                    .setAlpha(mOldAlpha)
                    .setEasing(mOldEasing)
                    .setOverlap(mOldOverlapFactor, mOldOverlapOrder)
                    .setStartPoint(mOldStartX, mOldStartY);
    }


    private void updateValues(BarChartView chartView){
        chartView.updateValues(0, barValues[1]);
        chartView.updateValues(1, barValues[0]);
        chartView.notifyDataUpdate();
    }

    private void updateValues(HorizontalBarChartView chartView){

        chartView.updateValues(0, horBarValues[1]);
        chartView.notifyDataUpdate();
    }
    /*------------------------------------*
	 *               SETTERS              *
	 *------------------------------------*/

    private void setOverlap(int index) {

        switch (index) {
            case 0:
                mCurrOverlapFactor = 1;
                mCurrOverlapOrder = beginOrder;
                break;
            case 1:
                mCurrOverlapFactor = .5f;
                mCurrOverlapOrder = beginOrder;
                break;
            case 2:
                mCurrOverlapFactor = .5f;
                mCurrOverlapOrder = endOrder;
                break;
            case 3:
                mCurrOverlapFactor = .5f;
                mCurrOverlapOrder = middleOrder;
                break;
            default:
                break;
        }
    }

    private void setEasing(int index) {

        switch (index) {
            case 0:
                mCurrEasing = new CubicEaseOut();
                break;
            case 1:
                mCurrEasing = new QuintEaseOut();
                break;
            case 2:
                mCurrEasing = new BounceEaseOut();
                break;
            case 3:
                mCurrEasing = new ElasticEaseOut();
            default:
                break;
        }
    }

    private void setEnterPosition(int index) {

        switch (index) {
            case 0:
                mCurrStartX = -1f;
                mCurrStartY = 0f;
                break;
            case 1:
                mCurrStartX = 0f;
                mCurrStartY = 0f;
                break;
            case 2:
                mCurrStartX = 0f;
                mCurrStartY = -1f;
                break;
            case 3:
                mCurrStartX = 0f;
                mCurrStartY = 1f;
                break;
            case 4:
                mCurrStartX = -1f;
                mCurrStartY = 1f;
                break;
            case 5:
                mCurrStartX = 1f;
                mCurrStartY = 1f;
                break;
            case 6:
                mCurrStartX = 1f;
                mCurrStartY = -1f;
                break;
            case 7:
                mCurrStartX = 1f;
                mCurrStartY = 0f;
                break;
            case 8:
                mCurrStartX = .5f;
                mCurrStartY = .5f;
                break;
            default:
                break;
        }
    }


    @SuppressLint("NewApi")
    private void setAlpha(int index) {

        switch (index) {
            case 0:
                mCurrAlpha = -1;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    mAlphaBtn.setImageAlpha(255);
                else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                    mAlphaBtn.setAlpha(1f);
                break;
            case 1:
                mCurrAlpha = 2;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    mAlphaBtn.setImageAlpha(115);
                else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                    mAlphaBtn.setAlpha(.6f);
                break;
            case 2:
                mCurrAlpha = 1;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    mAlphaBtn.setImageAlpha(55);
                else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                    mAlphaBtn.setAlpha(.3f);
                break;
            default:
                break;
        }
    }



}
