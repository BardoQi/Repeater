package com.weishang.repeater;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.annotation.ViewClick;
import com.weishang.repeater.preference.ConfigName;
import com.weishang.repeater.preference.PrefernceUtils;
import com.weishang.repeater.service.PlayService;
import com.weishang.repeater.ui.MusicRecordFragment;
import com.weishang.repeater.ui.RecordListFragment;
import com.weishang.repeater.ui.RemarkListFragment;
import com.weishang.repeater.ui.SearchFragment;
import com.weishang.repeater.ui.StatisticsFragment;
import com.weishang.repeater.utils.NClick;
import com.weishang.repeater.utils.ViewInject;

/**
 * 复读器主界面
 *
 * @author momo
 * @date 2015/3/8
 */
@ViewClick(ids = {R.id.tv_user_record, R.id.tv_user_remark, R.id.tv_user_statistics, R.id.tv_user_setting, R.id.tv_play_statistics})
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    @ID(id=R.id.tv_happy_alert)
    private TextView mAlert;
    private NClick nClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInject.init(this);
        startService(new Intent(PlayService.PLAY_ACTION));

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                String happyAlert = PrefernceUtils.getString(ConfigName.HAPPY_ALTER);
                if(TextUtils.isEmpty(happyAlert)){
                    happyAlert=App.getStr(R.string.happy_every_day);
                }
                mAlert.setText(happyAlert);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        nClick=new NClick(2,2000) {
            @Override
            protected void toDo(Object[] objects) {
                finish();
            }

            @Override
            public void noToDo() {
                App.toast(R.string.click_to_exit);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            MoreActivity.toActivity(this, SearchFragment.class, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(PlayService.PLAY_ACTION);
        intent.putExtra("stop", true);
        stopService(intent);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        boolean isExit = PrefernceUtils.getBoolean(ConfigName.EXIT_ITEM);
        if(isExit){
            nClick.nClick();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_user_setting:
                startActivity(new Intent(this, SettingActivity.class));
                mDrawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    }
                }, 200);
                break;
            case R.id.tv_user_statistics:
                MoreActivity.toActivity(this, MusicRecordFragment.class,null);
                break;
            case R.id.tv_user_remark:
                MoreActivity.toActivity(this, RemarkListFragment.class,null);
                break;
            case R.id.tv_user_record:
                MoreActivity.toActivity(this, RecordListFragment.class,null);
                break;
            case R.id.tv_play_statistics:
                MoreActivity.toActivity(this, StatisticsFragment.class,null);
                break;
        }
    }
}
