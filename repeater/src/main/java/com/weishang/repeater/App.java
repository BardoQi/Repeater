package com.weishang.repeater;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.weishang.repeater.preference.ConfigName;
import com.weishang.repeater.preference.PrefernceUtils;
import com.weishang.repeater.utils.Loger;
import com.weishang.repeater.utils.PackageUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


public class App extends Application implements Thread.UncaughtExceptionHandler {
    public static final String STACKTRACE = "strace";
    private static Context context = null;
    public static final List<FragmentActivity> activitys;
    public static float sWidth, sHeight;
    public static int Theme;// 当前应用主题

    static {
        activitys = new ArrayList<FragmentActivity>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
        context = getApplicationContext();
        initImageLoader(context);
        PrefernceUtils.init(context);
        sWidth = getResources().getDisplayMetrics().widthPixels;
        sHeight = getResources().getDisplayMetrics().heightPixels;
        Thread.setDefaultUncaughtExceptionHandler(this);

        boolean isInit = PrefernceUtils.getBoolean(ConfigName.INIT);
        if (!isInit) {
            PrefernceUtils.setBoolean(ConfigName.INIT, true);

            //设置设置复读模式 0
            PrefernceUtils.setInt(ConfigName.REPEAT_COUNT, 0);

            //添加三种默认颜色
            int[] colors={App.getResourcesColor(R.color.yellow),App.getResourcesColor(R.color.red),App.getResourcesColor(R.color.blue)};
            for(int i=0;i<colors.length;i++){
                PrefernceUtils.addStyle(colors[i]);
            }
        }

        //如果版本号不一致.或者为空,则代表重装,或升级安装
        String appVersoin = PackageUtil.getAppVersoin();
        String appVersion = PrefernceUtils.getString(ConfigName.APP_VERSION);
        if(TextUtils.isEmpty(appVersion)||appVersion.equals(appVersion)){
            PrefernceUtils.setString(ConfigName.APP_VERSION, appVersion);
            //TODO 升级安装其他事件.预留操作
        }


    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static String getResString(int resId, Object... args) {
        String result = null;
        if (null != args) {
            result = context.getString(resId, args);
        } else {
            result = context.getString(resId);
        }
        return result;
    }

    public static Resources getAppResources() {
        return context.getResources();
    }

    public static ContentResolver getResolver() {
        return context.getContentResolver();
    }

    public static Context getAppContext() {
        return context;
    }

    public static boolean isRun() {
        return null != activitys && !activitys.isEmpty();
    }

    public static int getResourcesColor(int id) {
        return context.getResources().getColor(id);
    }

    public static String[] getStringArray(int array) {
        return context.getResources().getStringArray(array);
    }

    public static int[] getIntegerArray(int id) {
        return context.getResources().getIntArray(id);
    }

    public static String getStr(int resId, Object... args) {
        String result = null;
        if (null != args) {
            result = context.getString(resId, args);
        } else {
            result = context.getString(resId);
        }
        return result;
    }

    public static boolean isDebug() {
        return PackageUtil.getBooleanMataData("IS_DUBUG");
    }

    public static void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void toast(int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void toast(int resId, Object... args) {
        Toast.makeText(context, getStr(resId, args), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        final StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        Loger.e(this,stackTrace.toString());
        for (Activity activity : activitys) {
            activity.finish();
        }
        // 重启机制
        // Intent intent = new Intent(this, SplashActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // intent.putExtra(STACKTRACE, stackTrace.toString());
        // startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
