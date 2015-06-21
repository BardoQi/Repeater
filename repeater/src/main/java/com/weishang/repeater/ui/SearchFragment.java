package com.weishang.repeater.ui;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.adapter.SearchAdapter;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.service.PlayService;
import com.weishang.repeater.utils.ViewInject;

/**
 * A simple {@link Fragment} subclass.
 */
@com.weishang.repeater.annotation.Toolbar(title = R.string.music_search)
public class SearchFragment extends Fragment {
    @ID(id = R.id.lv_list)
    private ListView mListView;

    @ID(id=R.id.toolbar)
    private Toolbar mToolBar;
    private SearchAdapter mAdapter;

    public static Fragment newInstance() {
        return new SearchFragment();
    }

    public SearchFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ViewInject.init(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewInject.initToolBar(this, mToolBar);
        searchMusic("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(item);
        searchView.setIconified(false);
        searchView.setQueryHint(App.getStr(R.string.search_music_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchMusic(s);
                return false;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = mAdapter.getItem(position);
                Intent playService = new Intent(PlayService.PLAY_ACTION);
                music.listId=MyDb.SINGLE_ITEM;
                playService.putExtra(PlayService.PLAY_MUSIC, music);
                playService.putExtra(PlayService.PLAY_LIST_ID, MyDb.SINGLE_ITEM);
                getActivity().startService(playService);
            }
        });
    }

    private void searchMusic(String s){
        ContentResolver resolver = App.getResolver();
        Cursor newCursor=resolver.query(DbTable.FILE_URI,DbTable.FILE_SELECTION,"name like  ?  or author like ? and list_id=?",new String[]{"%" + s + "%", "%" + s + "%",String.valueOf(MyDb.DEFUALT_LIST)},"name DESC");
        if(null==mAdapter){
            mAdapter=new SearchAdapter(getActivity(),newCursor);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.setSearchWord(s);
            mAdapter.swapCursor(newCursor);
        }
    }
}
