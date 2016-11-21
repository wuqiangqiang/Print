package com.zrodo.demo.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * 核心算法工具
 */
public class CoreUtils {
    /**
     * 仪器软件界面：
     * 金标仪第一代横屏
     * 金标仪第二代反横屏
     * 金标仪手持式竖屏
     */
    public static int ScreenForward = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

    /**
     * 拍照CT图片后需要旋转的方向（和本身相机旋转没关系，是需要图片旋转）：
     * 金标仪第一代默认0：照片横着的二维码往右（默认往右）
     * 金标仪第二代反横屏270：照片竖着的二维码往右(默认往下)
     * 金标仪手持式竖屏：0：往右
     */
    public static int TakePhotoOrientation = 0;

    public static String DefaultCTLoc = "426,817,1100,954";    //默认检测图片检测区域坐标值
    public static String LuaKey = "xmjcLua";                //lua解密文件的key

    /**
     * 相机管理的framingrect
     * 台式金标仪：(0, 0, 2048,1536)
     * 手持式工程机：(0, 0, 540, 960)
     * 手持式样机：(0, 0, 720, 1280)
     */
    public static Rect frameRect = new Rect(0, 0, 720, 1280);

    public static boolean isHandleSetPrevSize = false;

    /**
     * 我们的相机是不是正的，拍出的照片是：左结果区域右二维码
     * 一般来说相机预览图像是向左倾斜的图片，所以要旋转
     * 相机参数旋转度数90，-90，180，-180
     * 默认旋转扫描二维码旋转90度，变倒过来
     * <p/>
     * 二维码方向：
     * 金标仪第一代90往下
     * 金标仪第二代0往下
     * 金标仪手持式90往下
     */
    public static int DisplayOrientation = 90;

    /**
     * 扫描二维码的时候是否需要放大预览：
     * 金标仪第一代:相机拍的照片是横着，方向正确
     * 金标仪第二代：相机拍的照片是竖着的，方向错误
     * 金标仪手持式：相机拍的照片是横着，方向正确
     */
    public static boolean NeedZoom = true;

//	/**
//	 * 扫描二维码的时候是否需要最佳预览状态：
//	 * 金标仪第一代:true
//	 * 金标仪第二代：unknow
//	 * 金标仪手持式：unknow
//	 * */
//	public static boolean NeedBestPreview = true;

    public static Point HandlePoint = new Point();//对于手动设置相机预览参数

    /**
     * 台式金标仪是需要focus的
     * 手持式的话不需要focus
     */
    public static boolean NeedFocus = true;


    /**
     * 对二维码扫描后的结果进行recode
     */
    public static String recode(String str) {
        String formart = "";
        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder().canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
                Log.i("1234      ISO8859-1", formart + "=----====-------=");
            } else {
                formart = str;
                Log.i("1234      stringExtra", str + "---------------------");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return formart;
    }


//	/**
//	 * 获得T值，C值，T/C值，B值
//	 * */
//	public static float[] getTCB(String ctPoint,String orgImgPath){
//		//设置so的rect
//		String array[] = ctPoint.split("\\,");
//		NativeFunctionInterface.getInstance().native_SetRect(Integer.valueOf(array[0]), Integer.valueOf(array[1]), 
//										 					 Integer.valueOf(array[2]), Integer.valueOf(array[3]));
//		//获得tcb
//		return NativeFunctionInterface.getInstance().native_Measure(orgImgPath);
//	}
//	
//	/**
//	 * 解析检测图片step5.5:lua检测
//	 * DecodeQr会调用此Lua函数：在解析检测图片时候用到的核心算法
//	 * @param Tval
//	 * @param Cval
//	 * @return
//	 */
//	public static float LuaTest(float Tval, float Cval ,float Bval , String paramid) {
//		LuaState Lua = LuaStateFactory.newLuaState();
//		Lua.openLibs();// 加载lua
//		
//		File fLua = new File(Constant.LUA_UNZIP_DIR + paramid + ".lua");
//		if(!fLua.exists()){
//			ComUtils.print("没有【" + paramid + "】源算法文件。");
//			return -9998;//error code
//		}
//		
//		//解密：把源文件解密到param.lua下
//		try {
//			FileUtils.decrypt(Constant.LUA_UNZIP_DIR + paramid + ".lua", Constant.LUA_DCY_PATH, LuaKey.length());
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		// 读入lua脚本
//		int error = Lua.LdoFile(Constant.LUA_DCY_PATH);
//		if (error != 0) {
//			ComUtils.print("读写算法解密文件失败。");
//			return -9999;
//		}
//		// 找到函数
//		Lua.getField(LuaState.LUA_GLOBALSINDEX, "getDensity");
//		// 参数1
//		Lua.pushNumber(Tval);
//		// 参数2
//		Lua.pushNumber(Cval);
//		// 参数3
//		Lua.pushNumber(Bval);
//		// 调用共3个参数1个返回值
//		Lua.call(3, 1);
//		// 保存返回值到result中
//		Lua.setField(LuaState.LUA_GLOBALSINDEX, "Density");
//		// 读入result
//		LuaObject lobj = Lua.getLuaObject("Density");
//		// 返回结果
//		float Density = (float) lobj.getNumber();
//		Lua.close();
//		
//		return Density;
//	}
}
