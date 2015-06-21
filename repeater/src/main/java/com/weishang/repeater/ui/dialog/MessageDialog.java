package com.weishang.repeater.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;

import com.weishang.repeater.R;
import com.weishang.repeater.listener.RtnTask;
import com.weishang.repeater.utils.RunnableUtils;

/**
 * Created by momo on 2015-03-18.
 */
public class MessageDialog extends DialogFragment {
	private static final String PARAMS1="title";
	private static final String PARAMS2="message";
	private String mTitle;
	private String message;
	private DialogInterface.OnClickListener mListener;

	public static MessageDialog newInstance(String title,String message) {
		MessageDialog frag = new MessageDialog();
		Bundle args = new Bundle();
		args.putString(PARAMS1,title);
		args.putString(PARAMS2,message);
		frag.setArguments(args);
		return frag;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle arguments = getArguments();
		if(null!=arguments){
			mTitle=arguments.getString(PARAMS1);
			message=arguments.getString(PARAMS2);
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//final ResultReceiver receiver = getArguments().getParcelable("receiver");
		android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(),R.style.Theme_AppCompat_Light_Dialog_Alert);
		builder.setTitle(mTitle);
		builder.setMessage(message)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					if(null!=mListener){
						mListener.onClick(dialog,id);
					}
					dialog.dismiss();
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
		return builder.create();
	}


	public void setPositliveListener(DialogInterface.OnClickListener listener) {
		this.mListener=listener;
	}

	@Override
	public int show(final FragmentTransaction transaction,final String tag) {
		return RunnableUtils.runWiteTryCatch(new RtnTask<Integer>() {
			@Override
			public Integer run() {
				return MessageDialog.super.show(transaction, tag);
			}
		});
	}

	@Override
	public void dismiss() {
		RunnableUtils.runWiteTryCatch(new Runnable() {
			@Override
			public void run() {
				MessageDialog.super.dismiss();
			}
		});

	}
}
