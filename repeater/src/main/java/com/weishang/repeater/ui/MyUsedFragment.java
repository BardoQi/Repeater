package com.weishang.repeater.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.weishang.repeater.App;
import com.weishang.repeater.PlayActivity;
import com.weishang.repeater.R;
import com.weishang.repeater.adapter.MusicAdapter;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.annotation.Toolbar;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.db.DbTable;

/**
 * 我的收藏
 * 
 * @author momo
 * @Date 2015/2/26
 */
@Toolbar(title = R.string.my_used)
public class MyUsedFragment extends ProgressFragment {
	@ID(id = R.id.list)
	private ListView mList;

	@Override
	public int getLayout() {
		return R.layout.fragment_list;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Cursor cursor = App.getResolver().query(DbTable.FILE_URI, DbTable.FILE_SELECTION, "use=?", new String[] { String.valueOf(1) }, "ut DESC");
		if (null != cursor && 0 < cursor.getCount()) {
			mList.setAdapter(new MusicAdapter(getActivity(), cursor));
		} else {
			setEmptyInfo(R.string.no_play);
			setEmptyShown(true);
		}
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object adapter = parent.getAdapter();
				if (adapter instanceof CursorAdapter) {
					Cursor cursor = ((CursorAdapter) adapter).getCursor();
					if (null != cursor) {
						Intent intent = new Intent(getActivity(), PlayActivity.class);
						intent.putExtra(PlayActivity.PLAY_MUSIC,new Music(cursor.getInt(1),
                                cursor.getLong(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getString(5),
                                cursor.getLong(6),
                                cursor.getString(7),
                                cursor.getLong(8),
                                cursor.getInt(9),
                                cursor.getInt(10),
                                cursor.getString(11),
                                cursor.getString(12),
                                cursor.getString(13)));
						startActivity(intent);
					}
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (android.R.id.home == item.getItemId()) {
			getActivity().finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
