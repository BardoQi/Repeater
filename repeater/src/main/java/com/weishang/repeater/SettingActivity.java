package com.weishang.repeater;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;

import com.kenumir.materialsettings.MaterialSettingsActivity;
import com.kenumir.materialsettings.items.CheckboxItem;
import com.kenumir.materialsettings.items.DividerItem;
import com.kenumir.materialsettings.items.HeaderItem;
import com.kenumir.materialsettings.items.SwitcherItem;
import com.kenumir.materialsettings.items.TextItem;
import com.kenumir.materialsettings.storage.PreferencesStorageInterface;
import com.kenumir.materialsettings.storage.StorageInterface;
import com.weishang.repeater.annotation.Toolbar;
import com.weishang.repeater.preference.ConfigName;
import com.weishang.repeater.preference.PrefernceUtils;
import com.weishang.repeater.ui.dialog.EditMessageDialog;
import com.weishang.repeater.ui.dialog.MessageDialog;
import com.weishang.repeater.utils.PackageUtil;

/**
 * Created by cz on 15/5/19.
 * 用户设置界面
 */
@Toolbar(title = R.string.setting)
public class SettingActivity extends MaterialSettingsActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBarBackground(App.getResourcesColor(R.color.title_bg));
        addItem(new HeaderItem(getFragment()).setTitle(App.getStr(R.string.common_setting)));
        addItem(new DividerItem(getFragment()));
        String alter = PrefernceUtils.getString(ConfigName.HAPPY_ALTER);
        if(TextUtils.isEmpty(alter)){
            alter=App.getStr(R.string.alert);
        }
        addItem(new TextItem(getFragment(), "alert").setTitle(App.getStr(R.string.alert)).setSubtitle(alter).setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(final TextItem textItem) {
                EditMessageDialog messageDialog = EditMessageDialog.newInstance(App.getStr(R.string.set_alert), null);
                messageDialog.setOnSubmitListener(new EditMessageDialog.OnSubmitListener() {
                    @Override
                    public void onSubmit(Editable editable) {
                        PrefernceUtils.setString(ConfigName.HAPPY_ALTER, editable.toString());
                        textItem.updateSubTitle(editable.toString());
                    }
                });
                messageDialog.show(getSupportFragmentManager(), null);
            }
        }));
        addItem(new DividerItem(getFragment()));
        addItem(new TextItem(getFragment(), "clear_cache").setTitle(App.getStr(R.string.clear_cache)).setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem v) {
                MessageDialog messageDialog = MessageDialog.newInstance(App.getStr(R.string.clear_cache),null);
                messageDialog.setPositliveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //清空缓存
                    }
                });
                messageDialog.show(getSupportFragmentManager(),null);
            }
        }));
        addItem(new DividerItem(getFragment()));
        addItem(new TextItem(getFragment(), "update_app").setTitle(App.getStr(R.string.update_app)).setSubtitle(App.getStr(R.string.app_version, PackageUtil.getAppVersoin())).setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem v) {
                //检测版本升级
            }
        }));
        addItem(new DividerItem(getFragment()));
        addItem(new SwitcherItem(getFragment(), "exit").setTitle(App.getStr(R.string.double_click_exit)).setDefaultValue(PrefernceUtils.getBoolean(ConfigName.EXIT_ITEM)).setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(CheckboxItem item, boolean isChecked) {
                PrefernceUtils.setBoolean(ConfigName.EXIT_ITEM, isChecked);
                item.save();
            }
        }));
    }

    @Override
    public StorageInterface initStorageInterface() {
        return new PreferencesStorageInterface(this);
    }



}
