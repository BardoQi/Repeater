package com.weishang.repeater.utils;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.annotation.MethodClick;
import com.weishang.repeater.annotation.Toolbar;
import com.weishang.repeater.annotation.ViewClick;
import com.weishang.repeater.listener.ViewImageClickListener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 控件寻找id与设置点击事件帮助类
 *
 * @author 扑倒末末
 */
public class ViewInject {

    /**
     * 初始化属性
     *
     * @param
     */
    public static void init(Object object) {
        init(object, null);
    }

    /**
     * 初始化fragment
     */
    public static void init(Object object, View view) {
        init(object, view, false);
    }

    /**
     * 初始化Fragment/Dialog/View
     *
     * @param object
     * @param view
     * @param initParent 是否初始化父类-----使用于ViewHolder 对象继承
     */
    public static void init(Object object, View view, boolean initParent) {
        initView(object, view, initParent);
        initClick(object, view);
        initMethodClick(object, view);
    }


    public static void initToolBar(Object object){
        initToolBar(object,null,null);
    }

    public static void initToolBar(Object object, android.support.v7.widget.Toolbar toolbar) {
        initToolBar(object, toolbar, null);
    }

    /**
     * 初始化toolbar
     *
     * @param object
     * @param toolbarView
     */
    public static void initToolBar(Object object, android.support.v7.widget.Toolbar toolbarView, String title) {
        Toolbar toolbar = object.getClass().getAnnotation(Toolbar.class);
        if (null == toolbar || null == toolbarView) return;
        ActionBar actionBar = null;
        AppCompatActivity appCompatActivity = null;
        if (!toolbar.hidden() && object instanceof AppCompatActivity) {
            appCompatActivity = ((AppCompatActivity) object);
            appCompatActivity.setSupportActionBar(toolbarView);
            actionBar = appCompatActivity.getSupportActionBar();
            toolbarView= (android.support.v7.widget.Toolbar) appCompatActivity.findViewById(R.id.toolbar);
        } else if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            fragment.setHasOptionsMenu(true);//启用actionbar
            FragmentActivity activity = fragment.getActivity();
            if (!toolbar.hidden() && activity instanceof AppCompatActivity) {
                appCompatActivity = ((AppCompatActivity) activity);
                appCompatActivity.setSupportActionBar(toolbarView);
                actionBar = appCompatActivity.getSupportActionBar();
                View view = fragment.getView();
                if(null!=view){
                    toolbarView= (android.support.v7.widget.Toolbar) view.findViewById(R.id.toolbar);
                }
            }
        }
        if(null==toolbarView) return;
        //设置标题
        int titleRes = toolbar.title();
        //优先设置传入标题
        if (!TextUtils.isEmpty(title)) {
            actionBar.setTitle(title);
        } else if (-1 != titleRes) {
            actionBar.setTitle(titleRes);
        }
        //显示返回
        if (toolbar.displayHome()) {
            final Activity finalActivity = appCompatActivity;
            toolbarView.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            toolbarView.setNavigationOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //检测此处是否存在内存泄露
                    if (null != finalActivity) {
                        finalActivity.finish();
                    }
                }
            });
        }
        //设置图标
        int icon = toolbar.icon();
        if (-1 != icon) {
            toolbarView.setLogo(icon);
        }
    }

    /**
     * 初始化方法点击事件
     *
     * @param object
     * @param view
     */
    private static void initMethodClick(final Object object, View view) {
        if (null != object) {
            final Method[] methods = object.getClass().getDeclaredMethods();
            int length = methods.length;
            for (int i = 0; i < length; i++) {
                methods[i].setAccessible(true);
                MethodClick methodClick = methods[i].getAnnotation(MethodClick.class);
                if (null != methodClick) {
                    int[] ids = methodClick.ids();
                    if (null != ids) {
                        View findView = null;
                        for (int j = 0; j < ids.length; j++) {
                            findView = getFindView(object, view, ids[j]);
                            if (null != findView) {
                                // 设置方法点击
                                final int finalI = i;
                                OnClickListener listener = new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try {
                                            methods[finalI].invoke(object, view);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                findView.setOnClickListener(methodClick.imageScale() ? new ViewImageClickListener(listener) : listener);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化控件点击事件
     *
     * @param object
     */
    private static void initClick(Object object, View view) {
        if (null != object && object instanceof OnClickListener) {
            ViewClick viewClick = object.getClass().getAnnotation(ViewClick.class);
            if (null != viewClick) {
                if (null != viewClick.ids() && 0 < viewClick.ids().length && -1 != viewClick.ids()[0]) {
                    int[] ids = viewClick.ids();
                    for (int i = 0; i < ids.length; i++) {
                        setOnclickListenerById(object, view, ids[i], viewClick.isImageView());
                    }
                }
                // 子控件点击
                if (null != viewClick.childClick() && 0 < viewClick.childClick().length && -1 != viewClick.childClick()[0]) {
                    int[] ids = viewClick.childClick();
                    for (int i = 0; i < ids.length; i++) {
                        View findView = getFindView(object, view, ids[i]);
                        if (null != findView && findView instanceof ViewGroup) {
                            ViewGroup findGroupView = (ViewGroup) findView;
                            int childCount = findGroupView.getChildCount();
                            for (int j = 0; j < childCount; j++) {
                                setOnClickListener(object, findGroupView.getChildAt(j), viewClick.isImageView());
                            }
                        }
                    }
                }
            }
        }
    }

    private static void setOnclickListenerById(Object object, View view, int id, boolean isViewImage) {
        View findView = getFindView(object, view, id);
        if (null != findView) {
            setOnClickListener(object, findView, isViewImage);
        }
    }

    private static View getFindView(Object object, View view, int id) {
        View findView = null;
        if (object instanceof Activity) {
            findView = ((Activity) object).findViewById(id);
        } else if (object instanceof Dialog) {
            findView = ((Dialog) object).findViewById(id);
        } else if (null != view) {
            findView = view.findViewById(id);
        }
        return findView;
    }

    /**
     * 查找控件id
     *
     * @param object
     * @param view
     * @param initParent
     */
    private static void initView(Object object, View view, boolean initParent) {
        initObject(object, object.getClass(), view);
        if (initParent) {
            initObject(object, object.getClass().getSuperclass(), view);
        }
    }

    private static void initObject(Object object, Class<?> clazz, View view) {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            ID id = field.getAnnotation(ID.class);
            if (null != id) {
                // 设置控件id
                if (0 != id.id()) {
                    try {
                        View findView = null;
                        if (object instanceof Activity) {
                            // activity
                            findView = ((Activity) object).findViewById(id.id());
                        } else if (object instanceof Dialog) {
                            // 对话框---
                            findView = ((Dialog) object).findViewById(id.id());
                        } else if (object instanceof View) {
                            findView = ((View) object).findViewById(id.id());
                        } else if (null != view) {
                            // view---用于fragment
                            findView = view.findViewById(id.id());
                        }
                        if (null != findView) {
                            field.set(object, findView);
                            // 设置当前控件点击
                            if (id.click()) {
                                setOnClickListener(object, findView, true);
                            }
                            // 设置子控件点击事件
                            if (id.childClick() && findView instanceof ViewGroup) {
                                for (int i = 0; i < ((ViewGroup) findView).getChildCount(); i++) {
                                    setOnClickListener(object, ((ViewGroup) findView).getChildAt(i), true);
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 设置控件onClick事件到当前acivity----activity必须实现onclick接口！
     *
     * @param object
     * @param view
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    private static void setOnClickListener(Object object, View view, boolean isViewImage) {
        try {
            Method method = view.getClass().getMethod("setOnClickListener", OnClickListener.class);
            if (null != method && object instanceof OnClickListener) {
                method.invoke(view, isViewImage ? ((OnClickListener) object) : (OnClickListener) object);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
