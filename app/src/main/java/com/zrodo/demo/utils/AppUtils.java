package com.zrodo.demo.utils;

import java.io.DataOutputStream;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Vibrator;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class AppUtils {

    //======================Toast===========================================
    public static void showToast(ContextWrapper ctx, int text) {
        showToast(ctx, ctx.getResources().getString(text));
    }

    public static void showToast(ContextWrapper ctx, int text, Object... formatArgs) {
        showToast(ctx, ctx.getResources().getString(text, formatArgs));
    }

    public static void showToast(ContextWrapper ctx, String text) {
        if (text == null)
            text = "null";
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }


    public static void showToast(Context ctx, String text) {
        if (text == null)
            text = "null";
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();

    }
    //======================Screen===========================================

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 震动模式
     *
     * @param staytime:震动持续时间
     */
    public static void openVibrate(Context ctx, int staytime) {
        Vibrator mVibrator = (Vibrator) ctx.getSystemService(Service.VIBRATOR_SERVICE);
        mVibrator.vibrate(staytime);//振动提醒已到设定位置附近
    }

    //======================Other===========================================

    /**
     * 使控件获得focus
     */
    public static void getFocus(View v) {
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.requestFocusFromTouch();
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftKeyboard(Activity act) {
        InputMethodManager manager = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View focus = act.getCurrentFocus();
        manager.hideSoftInputFromWindow(
                focus == null ? null : focus.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 关闭该控件的软键盘
     */
    public static void closeKeyBoard(Context ctx, EditText editText) {
        try {
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置系统栏可见性
     */
    public static void setSystemBarVisible(final Activity context, boolean visible) {
        int flag = context.getWindow().getDecorView().getSystemUiVisibility();
        int fullScreen = 0x8;
        if (visible) {
            if ((flag & fullScreen) != 0) {
                context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        } else {
            if ((flag & fullScreen) == 0) {
                context.getWindow().getDecorView().setSystemUiVisibility(flag | fullScreen);
            }
        }
    }

    /**
     * 判断状态栏是否显示
     */
    public static boolean isSystemBarVisible(final Activity context) {
        int flag = context.getWindow().getDecorView().getSystemUiVisibility();
        return (flag & 0x8) == 0;
    }

    /**
     * 获得安卓仪器设备号
     *
     * @return
     */
    public static String getServiceId(Context ctx) {
        String id = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
        return id;
    }

    /**
     * 获取程序版本号
     *
     * @param context
     * @return
     */
    public static int getLocalVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        int versionCode = 10000;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionCode;
    }

    // 获取当前版本号
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取当前版本号
    public static String getPhoneNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//			 String deviceid = tm.getDeviceId();	//获取智能设备唯一编号  
        String te1 = tm.getLine1Number();        //获取本机号码  		：移动没有
//			 String imei = tm.getSimSerialNumber();	//获得SIM卡的序号  	:电信没有
//			 String imsi = tm.getSubscriberId();	//得到用户Id 
        return (te1 != null ? te1 : "NA");
    }

    // 获取手机服务商信息
    public static String getProvidersName(Context context) {
        String ProvidersName = "NA";
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String IMSI = tm.getSubscriberId();

            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ProvidersName;
    }

    /**
     * 设置系统时间
     *
     * @param【时间格式 yyyyMMdd.HHmmss】
     */
    public static boolean setSystemTime(String time) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT\n");
            os.writeBytes("/system/bin/date -s " + time + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //=================================================test==============================================================

    /**
     * 测试下设备的基本信息
     * screenWidth=800,screenHeight=552,density=1.0,densityDPI=160,xdpi=213.0,ydpi=213.0
     */
    public static String testDevice(Context ctx, Activity act) {
//		//fun 1
//		Display display = act.getWindowManager().getDefaultDisplay(); 
//		int width = display.getWidth();
//		int height = display.getHeight();
//		String fun1 = "fun1:width="+width+",height="+height+"\n";
//		ComUtils.print("fun1:width="+width+",height="+height);

        //fun 2
        DisplayMetrics dm = new DisplayMetrics();
        dm = ctx.getResources().getDisplayMetrics();
        float density = dm.density;        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        int densityDPI = dm.densityDpi;     // 屏幕密度（每寸像素：120/160/240/320）
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        String fun2 = "fun2:screenWidth=" + screenWidth + ",screenHeight=" + screenHeight +
                ",density=" + density + ",densityDPI=" + densityDPI +
                ",xdpi=" + xdpi + ",ydpi=" + ydpi + "\n";
        ComUtils.print("fun2:screenWidth=" + screenWidth + ",screenHeight=" + screenHeight +
                ",density=" + density + ",densityDPI=" + densityDPI +
                ",xdpi=" + xdpi + ",ydpi=" + ydpi);

//		//fun 3
//		dm = new DisplayMetrics();  
//		act.getWindowManager().getDefaultDisplay().getMetrics(dm);  
//		density  = dm.density;      	// 屏幕密度（像素比例：0.75/1.0/1.5/2.0）  
//		densityDPI = dm.densityDpi;     // 屏幕密度（每寸像素：120/160/240/320）  
//		xdpi = dm.xdpi;           
//		ydpi = dm.ydpi;  
//		int screenWidthDip = dm.widthPixels;        // 屏幕宽（dip，如：320dip）  
//		int screenHeightDip = dm.heightPixels;      // 屏幕宽（dip，如：533dip）  
//		String fun3 = "fun3:screenWidthDip="+screenWidthDip+",screenHeightDip="+screenHeightDip+
//				",density="+density+",densityDPI="+densityDPI+
//				",xdpi="+xdpi+",ydpi="+ydpi+"\n";
//		ComUtils.print("fun3:screenWidthDip="+screenWidthDip+",screenHeightDip="+screenHeightDip+
//				",density="+density+",densityDPI="+densityDPI+
//				",xdpi="+xdpi+",ydpi="+ydpi);

//		return fun1+fun2+fun3;
        return fun2;
    }

}
