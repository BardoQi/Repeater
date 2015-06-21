package com.weishang.repeater.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.utils.ViewInject;

import java.lang.reflect.Method;


/**
 * 搜索控件
 */
public class SearchView extends LinearLayout implements TextWatcher {
    @ID(id = R.id.rl_container)
    private View mContainer;
    @ID(id = R.id.et_content_editor)
    private EditText mEditor;
    @ID(id = R.id.iv_clear_btn)
    private ImageView mDeleteBtn;
    @ID(id = R.id.tv_submit_btn)
    private TextView mSubmitBtn;
    private OnSearchListener mListener;

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SearchView(Context context) {
        this(context, null);
    }

    /**
     * �初始化控件��ؼ�
     */
    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.search_view_layout, this, true);
        ViewInject.init(this);
        mEditor.addTextChangedListener(this);
        mSubmitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onSubmit(mEditor.getText());
                }
            }
        });
        mDeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setText(null);
            }
        });
        mEditor.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (EditorInfo.IME_ACTION_SEARCH == actionId
                        && null != mListener) {
                    mListener.onSubmit(mEditor.getText());
                }
                return false;
            }
        });
        mEditor.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    onEditViewFocusChange();
                    post(mShowImeRunnable);
                }
            }
        });

    }

    private Runnable mShowImeRunnable = new Runnable() {
        public void run() {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                showSoftInputUnchecked(mEditor, imm, 0);
            }
        }
    };

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        onEditViewFocusChange();
    }

    private void onEditViewFocusChange() {
        post(new Runnable() {
            @Override
            public void run() {
                boolean focused = mEditor.hasFocus();
                mContainer.getBackground().setState(
                        focused ? FOCUSED_STATE_SET : EMPTY_STATE_SET);
                mContainer.getBackground().setState(
                        focused ? FOCUSED_STATE_SET : EMPTY_STATE_SET);
                invalidate();
            }
        });
    }

    /**
     * �设置输入法显示隐藏状态
     *
     * @param visible
     */
    private void setImeVisibility(final boolean visible) {
        if (visible) {
            post(mShowImeRunnable);
            setBackgroundResource(R.drawable.textfield_multiline_activated);
        } else {
            removeCallbacks(mShowImeRunnable);
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
    }

    private void showSoftInputUnchecked(View view,
                                        InputMethodManager imm, int flags) {
        try {
            Method method = imm.getClass().getMethod("showSoftInputUnchecked",
                    int.class, ResultReceiver.class);
            method.setAccessible(true);
            method.invoke(imm, flags, null);
        } catch (Exception e) {
            imm.showSoftInput(view, flags);
        }
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                KeyEvent.DispatcherState state = getKeyDispatcherState();
                if (state != null) {
                    state.startTracking(event, this);
                }
                return true;
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                KeyEvent.DispatcherState state = getKeyDispatcherState();
                if (state != null) {
                    state.handleUpEvent(event);
                }
                if (event.isTracking() && !event.isCanceled()) {
                    mEditor.clearFocus();
                    setImeVisibility(false);
                    return true;
                }
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    /**
     * 设置编辑提示文字
     */
    public void setEditHintText(String text) {
        mEditor.setHint(text);
    }

    /**
     * 设置编辑提示文字
     */
    public void setEditHintText(int resid) {
        mEditor.setHint(resid);
    }

    /**
     * 获得搜索字符
     *
     * @return
     */
    public String getSearchWord() {
        return mEditor.getText().toString();
    }

    /**
     * �是否展示提交按钮
     */
    public void setShowSubmit(boolean show) {
        synchronized (SearchView.class) {
            mSubmitBtn.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mSubmitBtn.setEnabled(!TextUtils.isEmpty(s));
        mDeleteBtn.setVisibility(!TextUtils.isEmpty(s) ? View.VISIBLE : View.GONE);
        if (null != mListener) {
            mListener.onQuery(s);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public void setOnSearchListener(OnSearchListener listener) {
        this.mListener = listener;
    }

    public void clearEdit() {
        mEditor.setText(null);
    }

    public interface OnSearchListener {
        void onQuery(CharSequence text);

        void onSubmit(CharSequence text);
    }

}
