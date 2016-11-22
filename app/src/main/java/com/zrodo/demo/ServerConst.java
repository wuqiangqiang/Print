package com.zrodo.demo;

import android.util.Base64;

public class ServerConst {

	/**
	 * 网络数据传输timeout时间
	 * */
	public static int TIMEOUT_SHORT = 6*1000;	//6s
	public static int TIMEOUT_LONG = 20*1000;	//20s
	
	/**
	 * 服务器配置定义
	 * */
	//本地：192.168.1.104:8080	    正式：115.29.106.147:8080    正式： www.zrodo.com:8080
	public static String	SERVER_IP	= "www.zrodo.com";
	public static String	SERVER_PORT	= "8080";			
	public static String	SERVER_URL	= "http://" + SERVER_IP + ":" + SERVER_PORT + "/dcsinterface/";
	public static String 	JS_VERSION_CODE = "SYS_CODE_VERSION_DCS_JIANGSU";
	
	/**
	 * API接口定义
	 */
	public static String LOGIN_API 				= SERVER_URL + "login.do";				//登陆
	public static String UPLOAD_API 			= SERVER_URL + "reportUpload.do";		//上传
//	public static String VERSION_UPLOAD_API 	= SERVER_URL + "getVersion.do";			//版本更新
	public static String VERSION_UPLOADNEW_API 	= SERVER_URL + "getVersionByCode.do";	//版本更新
	public static String MSG_LIST_API			= SERVER_URL + "getMessage.do";			//0：消息中心 , 1：常见问题+condition , 2：知识库+sortid
	public static String TYPE_LIST_API			= SERVER_URL + "jsonTypeListNew.do";	//类别列表：暂时只给知识库用于组菜单子菜单名字用的
	public static String VIDEO_LIST_API			= SERVER_URL + "getVideos.do";			//视频列表
	public static String GET_ITEM_API			= SERVER_URL + "getAllItems.do";			//检测项目
	
	/**
	 * 江苏接口的basic auth
	 * */
	public static String JS_CONTENT_TYPE = "Content-Type";
	public static String JS_CONTENT_TYPEVALUE = "application/json";
	public static String JS_BASICAUTH = "Authorization";
	public static String JS_BASICAUTH_NAME = "zhiruida";
	public static String JS_BASICAUTH_PSW = "B02D517B47F773AE534B31024827C141";
	public static String JS_BASICAUTH_ACCESS = JS_BASICAUTH_NAME+":"+JS_BASICAUTH_PSW;
//	public static String JS_BASICAUTH_KEY = "Basic "+Base64.encodeToString(ServerConst.JS_BASICAUTH_ACCESS.getBytes(), Base64.DEFAULT);//会多一个\n最后
	public static String JS_BASICAUTH_KEY = "Basic emhpcnVpZGE6QjAyRDUxN0I0N0Y3NzNBRTUzNEIzMTAyNDgyN0MxNDE=";//会多一个\n最后
	
	public static String JS_PARAM_FROM = "2";//来源仪器
	public static String JS_SERVER = "http://jgj.jsagri.gov.cn:8081/index.php/rest";//服务器网页
	public static String JS_LOGIN_API = JS_SERVER+
			"/user/check/username/%s/from/%s";//登陆接口
	
	public static String JS_BASEINFO_API = JS_SERVER+
			"/dict/index/region_code/%s/username/%s/from/%s";//所有基础数据接口
	
	public static String JS_BASEINFO_PRODUCE_API = JS_SERVER+
			"/dict/items/module/system/code/produce/region_code/%s/username/%s/from/%s";//(产品类别)基础数据接口
	
	public static String JS_GETLATEST_TIMESTAMP_API = JS_SERVER+
			"/dict/timestamp/region_code/%s/username/%s/from/%s";//获得最新的时间戳：无法获得 timestamp=false
	
	public static String JS_NEED_MODIFY_TIME_API = JS_SERVER+
			"/dict/modified/timestamp/%s/region_code/%s/username/%s/from/%s"; //是否需要修正最新时间戳来判断更数据 ==false
	
	public static String JS_UPLOAD_API = JS_SERVER+"/monitor/index/username/%s/from/%s";//基础数据接口
	
	
	//==================================================================================================
	private static String ERROR_500 		= "服务器异常，请联系管理员。";			//服务器运行时候出错
	private static String ERROR_404 		= "服务器请求失败，请联系管理员。";		//访问到服务器但是访问不到该接口，或接口有问题
	private static String ERROR_TIMEOUT 	= "服务器访问超时，请检查网络。";			//有网但是访问超时
	private static String ERROR_REFUSE 		= "服务器拒绝访问，请检查网络。";			//服务器关闭，wifi关闭，ip错误都报这个
	private static String ERROR_UNKNOW_HOST = "服务器域名解析失败，请检查网络。"; 	//域名解析失败
	private static String ERROR_OTHER 		= "未知异常，请联系管理员";				//可能后台断点调试
	
	public static String getHttpError(int errorNo,String strMsg){
		if(errorNo == 500){
			return ERROR_500;
		}
		else if(errorNo == 404){
			return ERROR_404;
		}
		else if(strMsg == null){
			return ERROR_OTHER + "。";
		}
		else if(strMsg.equals("Connection to http://"+SERVER_IP+":"+SERVER_PORT+" refused")){
			return ERROR_REFUSE;
		}
		else if(strMsg.equals("Connect to /"+SERVER_IP+":"+SERVER_PORT+" timed out")){
			return ERROR_TIMEOUT;
		}
		else if(strMsg.equals("unknownHostException：can't resolve host")){
			return ERROR_UNKNOW_HOST;
		}
		else{
			return ERROR_OTHER + "("+strMsg+")。";
		}
	}
	
	/**
	 * 江苏的Url需要替换一般传string[]
	 * */
	public static String formatJSUrl(String formatstr,Object[] params){
		try{
			return String.format(formatstr, params);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
