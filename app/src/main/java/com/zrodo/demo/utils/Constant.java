package com.zrodo.demo.utils;

import android.os.Environment;

/**
 * 定义的静态变量
 *
 * @author wujn
 */
public class Constant {
    public static String FOR_DCS = "DCS";//多参数
    public static String FOR_NC = "NC";//农残

    public static String MatchRegexStr_TelNum = "((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-" +
            "(\\d{7,8})|(\\d{4}|\\d{3})-" +
            "(\\d{7,8})-" +
            "(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-" +
            "(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)";

    //江苏平台上传默认值
    public static String js_upload_method = "post";//上传方式
    public static String js_add_firm = "T004";
    public static int js_add_from = 2;
    public static String js_add_sign = "智锐达生化速测仪";
    public static String js_detect_basis = "GB/T 5009.199-2003";//检测依据 value=1
    public static String js_detect_method = "酶抑制率法";//检测方式 value=1

    /**
     * 底层驱动code
     */
    public static String OPEN = "o";    //打开光源
    public static String CLOSE = "c";    //关闭光源
    public static String RETURN = "r";  //滤光片复位到1

    /**
     * 各种路径
     */
    public static String FONT_KAITI_PATH = "fonts/SIMKAI.TTF";    //楷体文件路径
    public static String HANYU_SIMPLE_PATH = "/assets/unicode_to_simple_pinyin.txt";    //模糊查询与中文字排序
    //	public static String HANYU_SIMPLE_PATH = "/assets/unicode_to_hanyu_pinyin.txt";
    public static String U_FILE_PATH = "mnt/usb_storage";        //u盘文件路径
    public static String U_FILE_NAME = "Data.xls";                //u盘文件名
    public static String DOWN_APK_PATH = Environment.getExternalStorageDirectory() + "/DuoCanShu/Apk/";    //下载apk的路径
    public static String VIDEO_PATH = Environment.getExternalStorageDirectory() + "/DuoCanShu/Video/";    //视频目录
    public static String VIDEO_COVER_PATH = VIDEO_PATH + "cover/";                                        //视频封面目录
    public static String VIDEO_SOURCE_PATH = VIDEO_PATH + "source/";                                    //视频源目录


    /**
     * 私有文件的sp的key定义
     */
    //当前使用的对照检测结果
    public static String SP_ADZ = "Adz";
    public static String SP_ADZ_VAL = "Adz.val";
    public static String SP_ADZ_ID = "Adz.id";

    //是否进行过出厂校准补差
    public static String SP_DIFF = "diff";
    public static String SP_DIFF_ISMATH = "diff.ismath";//0,1


    //登陆人信息
    public static String SP_USER = "user";                        //用户
    public static String SP_USER_LEVEL = "user.level";            //用户等级
    public static String SP_USER_ID = "user.userid";            //用户id
    public static String SP_USER_TRUENAME = "user.truename";    //用户真名
    public static String SP_USER_USERNAME = "user.username";    //用户名
    public static String SP_USER_PASSWORD = "user.password";    //用户密码
    public static String SP_USER_POINTNAME = "user.pointname";    //用户站点名
    public static String SP_USER_POINTID = "user.pointid";        //用户站点id
    public static String SP_USER_TYPENAME = "user.typename";    //用户使用点type "超市"，"菜市场"
    public static String SP_USER_TYPEID = "user.typeid";        //用户使用点id	"1"
    public static String SP_USER_ROLEID = "user.roleid";        //用户角色:2->管理员

    //百度location位置信息sp
    public static String SP_LOC = "bd_location";
    public static String SP_LOC_LATI = "bd_location.latitude";    //经度
    public static String SP_LOC_LONGI = "bd_location.longitude";//纬度
    public static String SP_LOC_ADDR = "bd_location.address";    //地址

    //被检测中记住的值
    public static String SP_REMEBER = "remeber";
    public static String SP_REMEBER_COMPANY = "remeber.company";    //被检单位->","
    public static String SP_REMEBER_OBJ = "remeber.obj";            //检测对象->","

    //极光推送
    public static String SP_JPUSH = "jpush";
    public static String SP_JPUSH_REGID = "regid";

    //江苏用户登录
    public static String SP_JS_LOGIN = "js_login";
    public static String SP_JS_LOGIN_NAME = "js_login.name";
    //记住的
    public static String SP_JS_REMEBER = "js_remeber";
    public static String SP_JS_REMEBER_WORKER = "js_remeber.worker";//采样人员，逗号分隔
    public static String SP_JS_REMEBER_SAMPLENO = "js_remeber.sampleno";//样品编号，记录最新


    /**
     * 检测项目初始化的load info
     */
    //不同检测项目的浓度单位
    public static String itemUnits[] = {
            "%", "mg/kg", "mg/kg", "mg/kg", "A",
            "A", "mg/kg", "mg/kg", "mg/kg", "mg/kg",
            "mg/kg", "mg/kg", "mg/kg", "mg/kg", "%",
            "%", "mg/kg", "mg/kg", "mg/kg", "mg/kg",
            "mg/kg", "mg/kg", "mg/kg", "mg/kg", "mg/kg",
            "mg/kg", "mg/kg", "mg/kg", "mg/kg", "mg/kg",
            "mg/kg", "mg/kg", "mg/kg", "mg/kg", "mg/kg",
            "mg/kg", " mg/kg", "mg/kg", "mg/kg", "mg/kg",
            "mg/kg", "%", "mg/kg", "mg/kg", "mg/kg",
            "mg/kg"
    };
    //不同检测项目名字：默认
    public static String itemName[] = {
            "农药残留", "双氧水", "硼砂", "噻唑烷酮", "猪肉PDO",
            "肌肉PDO", "硝酸盐", "挥发性盐基氮", "砷", "柠檬黄",
            "硫氰酸钠", "吊白块", "甲醛", "褪黑素", "果糖",
            "葡萄糖", "日落黄", "胭脂红", "苋菜红", "山梨酸",
            "组胺", "二氧化硫", "亚硝酸盐", "溴酸钾", "丙二醛",
            "过氧化苯甲酰", "罗丹明B", "皮革水解物", "美术绿", "酱油中毛发水解物",
            "火碱", "拉非", "酚酞", "余氯", "食盐碘",
            "重金属铬", "茶多酚", "甲醇", "亚铁氰化钾", "重金属镉",
            "木耳掺假", "蛋白质", "硫酸铝钾", "油脂表面活性剂", "亮蓝",
            "糖精"
    };
    //不同的检测项目对应滤光片的真实波长：非国家最优标准
    //不同公司的溶液体系不同所需波长也不同，根据他们修改（百士康）
    public static int waveLen[] = {
            420, 420, 420, 420, 420,
            420, 550, 420, 420, 420,
            450, 420, 420, 520, 520,
            520, 520, 520, 520, 520,
            520, 600, 550, 550, 550,
            550, 550, 550, 550, 550,
            550, 550, 550, 550, 550,
            550, 550, 550, 600, 600,
            600, 600, 630, 630, 630,
            630
    };
    //不同检测项目对应的滤光片
    public static String channelNum[] = {
            "0", "0", "0", "0", "0",
            "0", "3", "0", "0", "0",
            "1", "0", "0", "2", "2",
            "2", "2", "2", "2", "2",
            "2", "4", "3", "3", "3",
            "3", "3", "3", "3", "3",
            "3", "3", "3", "3", "3",
            "3", "3", "3", "4", "4",
            "4", "4", "5", "5", "5",
            "5"
    };
    //	//不同检测项目名字：淮安投标
//	public static String itemName[] = { 
//		"农药残留","双氧水","硼砂","噻唑烷酮","孔雀石绿",
//		"铅","硝酸盐","挥发性盐基氮","砷","柠檬黄",	
//		"硫氰酸钠","吊白块","甲醛","褪黑素","果糖",
//		"葡萄糖","日落黄","胭脂红","苋菜红","山梨酸",
//		"组胺","二氧化硫","亚硝酸盐","溴酸钾","丙二醛",
//		"过氧化苯甲酰","罗丹明B","皮革水解物","美术绿","酱油中毛发水解物",
//		"火碱","拉非","酚酞","余氯","食盐碘",
//		"重金属铬","茶多酚","甲醇","亚铁氰化钾","重金属镉",
//		"木耳掺假","蛋白质","硫酸铝钾","油脂表面活性剂","亮蓝",
//		"糖精"
//    };
    //不同检测项目名字：江苏投标
//	public static String itemName[] = { 
//		"农药残留","吊白块","硼砂","甲醛","苏丹红",
//		"罗丹明B","罂粟壳","苯甲酸","山梨酸","脱氢乙酸",
//		"双乙酸钠","二氧化硫","亚硝酸盐","糖精钠","甜蜜素",
//		"安赛蜜","诱惑红","柠檬黄","苋菜红","胭脂红",
//		"日落黄","赤藓红","亮蓝","新红","硫酸铝钾",
//		"硫酸铝铵","酸价","过氧化值"
//    };
    //不同的检测项目对应不同的id
    public static int itemId[] = {
            1, 2, 3, 4, 5,
            6, 7, 8, 9, 10,
            11, 12, 13, 14, 15,
            16, 17, 18, 19, 20,
            21, 22, 23, 24, 25,
            26, 27, 28, 29, 30,
            31, 32, 33, 34, 35,
            36, 37, 38, 39, 40,
            41, 42, 43, 44, 45,
            46
    };
    //不同检测项目的是否开通
    public static String itemOpen[] = {
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1"
    };
    //不同的检测项目对应不同的本底值
    public static float bottomVal[] = {
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0
    };
    //不同的检测项目对应不同的参考最小值
    public static float minVal[] = {
            0f, 0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f, 0f,
            0f
    };
    //不同的检测项目对应不同的参考最大值:假的
    public static float maxVal[] = {
            50.00f, 2.00f, 2.00f, 2.00f, 2.00f,
            2.00f, 2.00f, 2.00f, 2.00f, 2.00f,
            2.00f, 2.00f, 2.00f, 2.00f, 2.00f,
            2.00f, 2.00f, 2.00f, 2.00f, 2.00f,
            2.00f, 2.00f, 2.00f, 2.00f, 2.00f,
            2.00f, 2.00f, 2.00f, 2.00f, 2.00f,
            2.00f, 2.00f, 2.00f, 2.00f, 2.00f,
            2.00f, 2.00f, 2.00f, 2.00f, 2.00f,
            2.00f, 2.00f, 2.00f, 2.00f, 2.00f,
            2.00f
    };
    //不同的检测项目的检测模式
    public static String mode[] = {
            "0", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1", "1", "1", "1", "1",
            "1"
    };


}
