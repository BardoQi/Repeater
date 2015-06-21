package com.weishang.repeater.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.preference.ConfigName;
import com.weishang.repeater.preference.PrefernceUtils;
import com.weishang.repeater.utils.InputMethodUtils;
import com.weishang.repeater.utils.ViewInject;

/**
 * Created by momo on 2015-03-18.
 * 带编缉信息的消息对象框
 */
public class EditMessageDialog extends DialogFragment {
    private static final String PARAMS1 = "title";
    private static final String PARAMS2 = "message";
    @ID(id = R.id.tv_text_count)
    private TextView mTextCount;
    @ID(id = R.id.et_list_name)
    private EditText mEditor;
    private String mTitle;
    private String message;
    private OnSubmitListener mListener;

    public EditMessageDialog() {
    }

    public static EditMessageDialog newInstance(String title, String message) {
        EditMessageDialog frag = new EditMessageDialog();
        Bundle args = new Bundle();
        args.putString(PARAMS1, title);
        args.putString(PARAMS2, message);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (null != arguments) {
            mTitle = arguments.getString(PARAMS1);
            message = arguments.getString(PARAMS2);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //final ResultReceiver receiver = getArguments().getParcelable("receiver");
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle(mTitle);
        View view = View.inflate(getActivity(), R.layout.dialog_create_list, null);
        builder.setView(view);
        ViewInject.init(this, view);
        builder.setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Editable text = mEditor.getText();
                        if (TextUtils.isEmpty(text)) {
                            App.toast(R.string.empty_info);
                        } else if (null != mListener) {
                            mListener.onSubmit(text);
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mTextCount.setText(App.getStr(R.string.list_text_count, charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        String happyAlert = PrefernceUtils.getString(ConfigName.HAPPY_ALTER);
        if (TextUtils.isEmpty(happyAlert)) {
            happyAlert = App.getStr(R.string.happy_every_day);
        }
        mEditor.setText(happyAlert);
        mEditor.setSelection(happyAlert.length());
        mEditor.requestFocus();
        mEditor.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodUtils.showSoftInput(getActivity());
            }
        }, 100);

    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }


    public void setOnSubmitListener(OnSubmitListener listener) {
        this.mListener = listener;
    }

    public interface OnSubmitListener {
        void onSubmit(Editable editable);
    }

}
