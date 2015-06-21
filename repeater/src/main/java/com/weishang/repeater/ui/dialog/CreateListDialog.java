package com.weishang.repeater.ui.dialog;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.utils.InputMethodUtils;
import com.weishang.repeater.utils.ViewInject;

/**
 * Created by momo on 2015/3/10.
 * 创建列表对象框
 */
public class CreateListDialog extends DialogFragment {
    public static final String PARAMS = "position";
    @ID(id = R.id.tv_text_count)
    private TextView mTextCount;
    @ID(id = R.id.et_list_name)
    private EditText mEditor;
    private OnEditListener mListener;
    private int mPosition;

    public static CreateListDialog newInstance(int position) {
        CreateListDialog dialog = new CreateListDialog();
        //去掉标题
        Bundle args = new Bundle();
        args.putInt(PARAMS, position);
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            mPosition = getArguments().getInt(PARAMS);
        }
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.dialog_create_list, container, false);
//        Window window = getDialog().getWindow();
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        ViewInject.init(this, view);
//        return view;
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.Theme_AppCompat_Light_Dialog_Alert);
        View view = View.inflate(getActivity(), R.layout.dialog_create_list, null);
        builder.setView(view);
        ViewInject.init(this, view);
        builder.setTitle(R.string.create_list);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //这里getText绝对不会为空,因为前面有setEnable点击,一个好的设计
                String listName = mEditor.getText().toString();
                ContentResolver resolver = App.getResolver();
                Cursor cursor = resolver.query(DbTable.LIST_URI, null, "name=?", new String[]{listName}, null);
                //生成列表,并通知桌面列表更新
                if (null != cursor && cursor.moveToFirst()) {
                    //TODO 合并列表操作
                    App.toast(R.string.list_already_exists);
                } else if (null != mListener) {
                    mListener.onEditComplete(listName);
                }
                dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    @Override
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
        final String listName = App.getStr(R.string.simple_list_name, mPosition + 1);
        mEditor.setText(listName);
        mEditor.setSelection(listName.length());
        mEditor.requestFocus();
        mEditor.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodUtils.showSoftInput(getActivity());
            }
        }, 100);

    }

    public void setOnEditListener(OnEditListener listener) {
        this.mListener = listener;
    }

    public int show(FragmentTransaction transaction) {
        return super.show(transaction, getClass().getSimpleName());
    }

    public interface OnEditListener {
        void onEditComplete(String text);
    }
}
