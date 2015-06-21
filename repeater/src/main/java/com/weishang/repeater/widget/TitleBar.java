package com.weishang.repeater.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weishang.repeater.R;
import com.weishang.repeater.listener.ViewImageClickListener;
import com.weishang.repeater.utils.UnitUtils;

/**
 * 自定义标题
 *
 * @author momo
 * @Date 2014/8/8
 */
public class TitleBar extends RelativeLayout {
    private static final int UN_RES = -1;
    private static final int TEXT_DEFAULT_SIZE = 18;
    private static final int TEXT_PADDING = 10;// 文字内边距
    private static final int IMAGE_MENU_WIDTH = 50;// 条目宽

    private DivideLinearLayout menuContainer;// 功能容器
    private TextView titleView;// 标题控件
    private TextView pageTitleView;// 中间标题
    private boolean disPlayHome;// 显示返回箭头
    private ProgressBar indeterminate;
    private int divierColor;// 底部分隔颜色
    private float divierHeight;// 分隔线高
    private Paint paint;

    public TitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttribute(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context) {
        this(context, null, 0);
    }

    private void initAttribute(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        paint = new Paint();
        setId(R.id.titlebar_container);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        addTitleBarView(titleView = getTextMenuView(R.id.titlebar_home, UN_RES), ALIGN_PARENT_LEFT);
        addTitleBarView(pageTitleView = getTextMenuView(R.id.titlebar_page, UN_RES), CENTER_HORIZONTAL);
        pageTitleView.setBackgroundColor(Color.TRANSPARENT);
        addTitleBarView(menuContainer = new DivideLinearLayout(context), ALIGN_PARENT_RIGHT);
        initIndeterminate();
        setBackgroundColor(typedArray.getColor(R.styleable.TitleBar_titlebar_bg, getResources().getColor(R.color.title_bg)));
        setDisplayHome(typedArray.getBoolean(R.styleable.TitleBar_titlebar_display_home, true));// 显示返回箭头
        setTitle(typedArray.getResourceId(R.styleable.TitleBar_titlebar_title, UN_RES));// 设置标题
        setIcon(typedArray.getResourceId(R.styleable.TitleBar_titlebar_icon, UN_RES));
        setTextTitleColor(typedArray.getColor(R.styleable.TitleBar_titlebar_text_color, getResources().getColor(R.color.text_color)));// 设置文字颜色
        setTextTitleColor(typedArray.getColorStateList(R.styleable.TitleBar_titlebar_text_color));// 设置文字状态选择器
        setPageTitle(typedArray.getResourceId(R.styleable.TitleBar_titlebar_page_title, UN_RES));
        setPageTitleColor(typedArray.getColor(R.styleable.TitleBar_titlebar_page_text_color, getResources().getColor(R.color.text_color)));// 设置标题颜色选择器
        setPageTitleColor(typedArray.getColorStateList(R.styleable.TitleBar_titlebar_page_text_color));// 设置标题颜色
        setPageTitleVisible(typedArray.getBoolean(R.styleable.TitleBar_titlebar_page_title_visible, false));
        showItemDivier(typedArray.getBoolean(R.styleable.TitleBar_titlebar_show_divier, false));// 显示分隔线
        setDivierColor(typedArray.getResourceId(R.styleable.TitleBar_titlebar_line_color, R.color.yellow));
        setDivierHeight(typedArray.getDimension(R.styleable.TitleBar_titlebar_line_height, UnitUtils.dip2px(context, 2f)));
        typedArray.recycle();
    }

    /**
     * 是否显示返回箭头
     *
     * @param displayHome
     */
    public void setDisplayHome(boolean displayHome) {
        if (disPlayHome = displayHome) {
            int padding = UnitUtils.dip2px(getContext(), TEXT_PADDING);
            int paddingLeft = UnitUtils.dip2px(getContext(), 3);
            // 有箭头图片,所以左边距为小一点。
            titleView.setPadding(paddingLeft, 0, padding, 0);
            titleView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.abc_ic_ab_back_mtrl_am_alpha, 0, 0, 0);
        } else {
            titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    /**
     * 设置返回事件
     *
     * @param listener
     */
    public void setBackListener(OnClickListener listener) {
        if (disPlayHome && null != listener) {
            findViewById(R.id.titlebar_home).setOnClickListener(new ViewImageClickListener(listener));
        }
    }

    /**
     * 添加左控件
     *
     * @param view
     */
    private void addTitleBarView(View view, int rule) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.addRule(rule);
        addView(view, params);
    }

    /**
     * 设置标题文字
     *
     * @param resid 标题res
     */
    public void setTitle(int resid) {
        if (UN_RES != resid) {
            titleView.setText(resid);
        }
    }

    /**
     * 设置标题文字
     */
    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        }
    }

    /**
     * 设置头标
     *
     * @param resid
     */
    public void setIcon(int resid) {
        if (UN_RES != resid) {
            titleView.setPadding(0, 0, TextUtils.isEmpty(titleView.getText()) ? 0 : UnitUtils.dip2px(getContext(), TEXT_PADDING), 0);
            titleView.setCompoundDrawablesWithIntrinsicBounds(resid, 0, 0, 0);
        }
    }

    /**
     * 设置title文字颜色
     *
     * @param color
     */
    public void setTextTitleColor(int color) {
        View homeView = findViewById(R.id.titlebar_home);
        if (null != homeView && homeView == titleView) {
            titleView.setTextColor(color);
        }
    }

    /**
     * 设置title文字颜色选择器
     *
     * @param stateList
     */
    public void setTextTitleColor(ColorStateList stateList) {
        View homeView = findViewById(R.id.titlebar_home);
        if (null != homeView && homeView == titleView && null != stateList) {
            titleView.setTextColor(stateList);
        }
    }

    /**
     * 设置页标题
     *
     * @param resid
     */
    public void setPageTitle(int resid) {
        if (null != pageTitleView && UN_RES != resid) {
            pageTitleView.setText(resid);
        }
    }

    /**
     * 设置页标题
     *
     * @param pageTitle
     */
    public void setPageTitle(String pageTitle) {
        if (null != pageTitleView && !TextUtils.isEmpty(pageTitle)) {
            pageTitleView.setText(pageTitle);
        }
    }

    /**
     * 设置页标题
     *
     * @param drawables
     */
    public void setPageCompoundDrawables(Drawable... drawables) {
        if (null != pageTitleView && null != drawables) {
            Drawable[] compoundDrawables = new Drawable[4];
            System.arraycopy(drawables, 0, compoundDrawables, 0, drawables.length);
            pageTitleView.setCompoundDrawablePadding(UnitUtils.dip2px(getContext(), 5));
            pageTitleView.setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        }
    }

    /**
     * 设置页标题颜色
     *
     * @param color
     */
    public void setPageTitleColor(int color) {
        if (null != pageTitleView) {
            pageTitleView.setTextColor(color);
        }
    }

    /**
     * 设置页标题颜色选择器
     *
     * @param colors
     */
    public void setPageTitleColor(ColorStateList colors) {
        if (null != pageTitleView && null != colors) {
            pageTitleView.setTextColor(colors);
        }
    }

    /**
     * 设置页显示隐藏状态
     */
    public void setPageTitleVisible(boolean visibility) {
        if (null != pageTitleView) {
            pageTitleView.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 显示条目之间分隔线
     *
     * @param showDivier
     */
    public void showItemDivier(boolean showDivier) {
        menuContainer.showItemDivide(showDivier);
    }

    /**
     * 是否显示加载旋转框
     *
     * @param showIndeterminate
     */
    public void showIndeterminate(boolean showIndeterminate) {
        if (null != indeterminate) {
            indeterminate.setVisibility(showIndeterminate ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置分隔线高
     *
     * @param height
     */
    public void setDivierHeight(float height) {
        this.divierHeight = height;
        invalidate();
    }

    /**
     * 设置底部分隔线颜色
     *
     * @param res
     */
    public void setDivierColor(int res) {
        this.divierColor = getResources().getColor(res);
        invalidate();
    }

    private void initIndeterminate() {
        Context context = getContext();
        indeterminate = new ProgressBar(context, null, android.R.attr.progressBarStyle);
        int padding = UnitUtils.dip2px(context, 10);
        indeterminate.setPadding(padding, padding, padding, padding);
        indeterminate.setVisibility(View.GONE);
        addContentView(indeterminate);
    }


    /**
     * 添加文字菜单项
     *
     * @param id
     * @param resId
     */
    public void addTextMenu(int id, int resId) {
        addTextMenu(id, resId, null);
    }

    public void addTextMenu(int id, int resId, OnClickListener listener) {
        TextView textMenuView = getTextMenuView(id, resId);
        if (null != listener) {
            textMenuView.setOnClickListener(new ViewImageClickListener(listener));
        }
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        menuContainer.addView(textMenuView, params);
    }

    /**
     * 设定menu显示隐藏状态
     *
     * @param id
     * @param visible
     */
    public void setMenuItemVisible(int id, boolean visible) {
        if (null != menuContainer) {
            View findView = menuContainer.findViewById(id);
            if (null != findView) {
                findView.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * 添加图片菜单项
     *
     * @param id
     * @param resId
     */
    public void addImageMenu(int id, int resId, int textid) {
        addImageMenu(id, resId, textid, null);
    }

    public View addImageMenu(int id, int resId, int textid, OnClickListener listener) {
        ImageView imageMenuView = getImageMenuView(id, resId, textid);
        if (null != listener) {
            imageMenuView.setOnClickListener(new ViewImageClickListener(listener));
        }
        LayoutParams params = new LayoutParams(UnitUtils.dip2px(getContext(), IMAGE_MENU_WIDTH), LayoutParams.MATCH_PARENT);
        menuContainer.addView(imageMenuView, params);
        return imageMenuView;
    }

    /**
     * 添加其他控件体
     *
     * @param view
     */
    public void addContentView(View view) {
        if (null != view) {
            LayoutParams params = new LayoutParams(UnitUtils.dip2px(getContext(), IMAGE_MENU_WIDTH), LayoutParams.MATCH_PARENT);
            menuContainer.addView(view, params);
        }
    }

    /**
     * 添加一个图片菜单
     *
     * @param id
     * @param resId
     * @return
     */
    private ImageView getImageMenuView(int id, int resId, final int textid) {
        ImageViewFlat imageMenu = new ImageViewFlat(getContext());
        imageMenu.setId(id);
        imageMenu.setImageResource(resId);
        imageMenu.setScaleType(ScaleType.FIT_CENTER);
        int padding = UnitUtils.dip2px(getContext(), 10);
        imageMenu.setPadding(padding, padding, padding, padding);
        if (-1 != textid) {
            imageMenu.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // ToastUtils.showAtLocatl(menuContainer, textid);
                    return true;
                }
            });
        }
        return imageMenu;
    }

    /**
     * 添加文字菜单
     *
     * @param id
     * @param resid
     * @return
     */
    private TextView getTextMenuView(int id, final int resid) {
        int padding = UnitUtils.dip2px(getContext(), TEXT_PADDING);
        return getTextMenuView(id, resid, padding, padding);
    }

    /**
     * 添加文字菜单
     *
     * @param id
     * @param resid
     * @param paddingLeft  控件左边距
     * @param paddingRight 控件右边距
     * @return
     */
    private TextView getTextMenuView(int id, final int resid, int paddingLeft, int paddingRight) {
        TextViewFlat textMenu = new TextViewFlat(getContext());
        textMenu.setId(id);
        textMenu.setGravity(Gravity.CENTER);
        textMenu.setClickMode(TextViewFlat.CLICK);
        textMenu.setPadding(paddingLeft, 0, paddingRight, 0);
        textMenu.setGravity(Gravity.CENTER_VERTICAL);
        textMenu.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_DEFAULT_SIZE);
        textMenu.setTextColor(getResources().getColor(R.color.text_color));
        if (R.id.titlebar_page != id) {
            textMenu.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // ToastUtils.showAtLocatl(menuContainer, resid);
                    return true;
                }
            });
        }
        if (UN_RES != resid) {
            textMenu.setText(resid);
        }
        return textMenu;
    }

    public TextView getTitleView() {
        return titleView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.reset();
        paint.setColor(divierColor);
        paint.setStrokeWidth(divierHeight);
        canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paint);
    }
}
