package com.zrodo.demo.print;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zrodo.demo.utils.AppUtils;
import com.zrodo.demo.utils.ComUtils;
import com.zrodo.demo.utils.CoreUtils;
import com.zrodo.demo.utils.FileUtils;
import com.zrodo.demo.zxing.camera.CameraManager;
import com.zrodo.demo.zxing.decoding.CaptureActivityHandler;
import com.zrodo.demo.zxing.decoding.InactivityTimer;
import com.zrodo.demo.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class DecodeActivity extends Activity  implements SurfaceHolder.Callback,AutoFocusCallback{

	private static final String TAG = "PhoneDemo";

	//二维码与检测运用
	private InactivityTimer inactivityTimer;	//定时器
	private CaptureActivityHandler Capturehandler;		//zxing 拍摄处理类
	private ViewfinderView viewfinderView;		//zxing 二维码扫描区域
	private boolean						hasSurface;			//是否开启了surfaceview
	private Vector<BarcodeFormat>		decodeFormats;		//zxing 解码类型
	private String						characterSet;		//zxing 字符编码
	int									TimeCount = 0;		//计数
	public boolean						mError = false; 	//初始定义解析是错误的（规定时间内完不成解析）
//	public DecodeImageHandler			decode;				//zxing 解码的handler for 定标？？？
	private Camera						camera;				//相机
	private SurfaceView					surfaceView;		//相机预览view
	private ImageView 					detectImg;			//检测图片
	private String 						ct;					//定标位置
	
	
	private final static int TAKE_PICTURE = 0x3001;     	//before 1
	private final static int DECODE2_TIMEOUT_CODE = 0x2001;	//before 13
	private final static int DECODE2_OVER_CODE = 0x2002;	//..
	
	private Handler chkHandler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch(msg.what){
				
			//拍照
			case TAKE_PICTURE:
				TakePic();
				break;
				
			//二维码解析超时
			case DECODE2_TIMEOUT_CODE:
				if (!isFinishing()) {
					if(mError){
						mError = false;
						CameraManager.get().stopPreview();
						CameraManager.get().closeDriver();
						Capturehandler = null;
						tvResult.setText("二维码解析失败。");
					}else{
						//in time decode success
					}
				}
				break;
				
			//二维码解析成功：from CaptureActivityHandler's R.id.decode_succeeded
			case DECODE2_OVER_CODE:
				mError = false;//恢复成为失败flag
				HashMap<String,Object> map = (HashMap<String,Object>)msg.obj;
				Result result = (Result)map.get("result");
				Bitmap barcode = (Bitmap)map.get("barcode");
				handleDecode(result,barcode);
				break;
				

			default:break;	
			}
			
		}
		
	};
	
	private TextView tvResult;
	private Button btnDecode2,btnTakePic;
	private Button btnBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.decode);
		AppUtils.setSystemBarVisible(this, false);
		setRequestedOrientation(CoreUtils.ScreenForward);
		
		//给相机模式用的surfaceview
		surfaceView = (SurfaceView) findViewById(R.id.surface_new);
		//被检测到的二维码图片
		detectImg = (ImageView) findViewById(R.id.img_pic_new);
		
		tvResult = (TextView)findViewById(R.id.tv_result);
		btnDecode2 = (Button)findViewById(R.id.btnDecode2);
		btnTakePic = (Button)findViewById(R.id.btnTakePic);
		btnBack = (Button)findViewById(R.id.btnBack);
		
		btnDecode2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				detectImg.setVisibility(View.GONE);
				tvResult.setText("扫描二维码...");
				initCapture();
			}
		});
		
		btnTakePic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvResult.setText("拍照...");
				StartTest();
			}
		});
		
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	
	/**
	 * 扫描二维码step1:初始化Capture 相机的surfaceView
	 * */
	@SuppressWarnings("deprecation")
	private void initCapture() {
		ComUtils.print("==============================二维码扫描开始=====================");
		ComUtils.print("---> initCapture()");
		try {
			// 初始化相机管理：获得预览回掉类（相机参数与一次拍照回掉的flag），获得自动对焦类
			CameraManager.init(this);
			// 获得个自定义ViewfinderView控件,也就是中间的扫描区域再时时刷新的框框
			viewfinderView = (ViewfinderView) findViewById(R.id.mo_scanner_viewfinder_view_new);

			// 解码处理类不为null的情况下，
			if (Capturehandler != null) {
				Capturehandler.quitSynchronously();
				Capturehandler = null;
			}

			// surfaceview的flag设为false
			hasSurface = false;

			// 错误flag设为true:因为此如果扫描时间超过定时器设置的20s,
			// 那么定时器会执行FinishListener来发出sendmessage(13),报“二维码解析失败”,然后重新设为false
			mError = true;

			// 定时器，在android2.3以上免费试用的定时器
			inactivityTimer = new InactivityTimer(chkHandler,DECODE2_TIMEOUT_CODE);

			if (!hasSurface) {
				surfaceView.getHolder().addCallback(this);
				initCamera(surfaceView.getHolder());
			} else {
				surfaceView.getHolder().addCallback(this);
				surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			}

			// 以下两个都是放到MultiFormatReader解码类中取得参数
			// 解码格式设为null,QR_CODE_FORMATS=二维码
			decodeFormats = null;
			// 字体风格设为null，一般UTF8
			characterSet = null;
			
		} catch (Exception e) {
			tvResult.setText("相机服务异常！");
			ComUtils.print("相机服务异常！");
			e.printStackTrace();
		}
	}

	/**
	 * 扫描二维码step2:初始化Camera surfaceView好了之后初始化相机
	 * */
	private void initCamera(SurfaceHolder surfaceHolder) {
		ComUtils.print("---> initCamera()");
		try {
			// 相机管理打开相机驱动，打开相机，设置相机参数
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException e) {
			tvResult.setText("相机IO异常！");
			ComUtils.print("相机服务异常！");
			e.printStackTrace();
			return;
		} catch (RuntimeException e) {
			tvResult.setText("相机RUN异常！");
			ComUtils.print("相机服务异常！");
			e.printStackTrace();
			return;
		}

		// decodeFormats = null | characterSet = null
		if (Capturehandler == null) {
			// 解码处理类，负责掉其他解码线程（DecodeThread）来解码
			Capturehandler = new CaptureActivityHandler(this,chkHandler,DECODE2_OVER_CODE,
														decodeFormats, characterSet,viewfinderView);
		}
	}

	/**
	 * 扫描二维码step3:CaptureActivityHandler调用handleDecode：获得recode2的String
	 * */

	public void handleDecode(final Result result, Bitmap barcode) {
		ComUtils.print("---> handleDecode()");
		
		detectImg.setImageBitmap(barcode);
		detectImg.setVisibility(View.VISIBLE);
		
		//save decode.jpg and get recode str
		FileUtils.savePic(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DemoDecode.jpg", barcode, null);
		String recode = CoreUtils.recode(result.toString());
		
		tvResult.setText(recode);//显示结果
		
		//close camera and driver
		try {
			// 解码错误flag重新设为false
			mError = false;
			CameraManager.get().stopPreview();
			CameraManager.get().closeDriver();
		} catch (Exception e) {
			tvResult.setText("关闭摄像头异常！");
			ComUtils.print("关闭摄像头异常！");
			e.printStackTrace();
		}
		
	}
	
	
	/**
	* 解析检测图片step1:开始测试
	* 相机预览开启，准备拍照
	* Camera.Parameters.FLASH_MODE_TORCH:手电筒常亮
	* Camera.Parameters.FLASH_MODE_ON	：拍照开启闪光灯
	* Camera.Parameters.FLASH_MODE_OFF	：关闭闪光灯
	* */	
	private void StartTest() {
		ComUtils.print("==================检测图片开始=================");
		ComUtils.print("---> StartTest()");

		detectImg.setVisibility(View.GONE);
		if (camera == null) {
			camera = Camera.open();
//			//open flash light
//			Camera.Parameters mParameters = camera.getParameters();    
//		    mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);    
//		    camera.setParameters(mParameters); 
		    
			surfaceView.getHolder().addCallback(this);
			try {
				camera.setPreviewDisplay(surfaceView.getHolder());
			}
			catch (IOException e) {
				tvResult.setText("相机IO异常！");
				e.printStackTrace();
			}
		}
		camera.startPreview();
		
		if(CoreUtils.NeedFocus){
			camera.autoFocus(this);//wait 5s to take pic for pad_jinbiao
		}else{
			chkHandler.sendEmptyMessageDelayed(TAKE_PICTURE, 1000 * 1); //just for phone_jinbiao
		}
		
	}
	
	
	/**
	 * 解析检测图片step2:拍照
	 * 回调函数：picCallBack
	 * */
	private void TakePic() {
		ComUtils.print("---> TakePic()");
		if (camera != null) {
			camera.takePicture(null, null, picCallBack);
		}
	}
	
	/**
	 * 解析检测图片step3:拍照回调函数
	 * */
	private PictureCallback	picCallBack	= new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			ComUtils.print("---> PictureCallback()");
			tvResult.setText("拍照完成！");
			
			//
			try {
				//拍照的原始图片，低质量保存
				Bitmap bitmap = FileUtils.spinBmp(BitmapFactory.decodeByteArray(data, 0, data.length),
												CoreUtils.TakePhotoOrientation);
				FileUtils.savePic(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DemoPicture.jpg", bitmap, null,100);
//				FileUtils.saveByte2Pic(Constant.CHK_ORG_IMG_PATH,data);
				detectImg.setImageBitmap(bitmap);
				detectImg.setVisibility(View.VISIBLE);
				
			}
			catch (Exception e) {
				return;
			}
			
			
			// 释放相机资源
			if (DecodeActivity.this.camera != null) {
				DecodeActivity.this.camera.stopPreview();
//				//close flash light
//				Camera.Parameters mParameters = DecodeActivity.this.camera.getParameters();    
//			    mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);    
//			    DecodeActivity.this.camera.setParameters(mParameters);
			    
			    DecodeActivity.this.camera.release();
			    DecodeActivity.this.camera = null;
				CameraManager.get().closeDriver();
			}
			hasSurface = false;
			
		}

	};
	
	
	//====================================================camera listener start================================
	//在定焦成功后5秒拍照，否则一直定焦
	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		if (success) {
			chkHandler.sendEmptyMessageDelayed(TAKE_PICTURE, 1000 * 1);
			if (camera != null) {
			}
		}
		else {
			if (camera != null) {
				camera.autoFocus(this);
			}
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(arg0);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		if (camera != null) {
			camera.stopPreview();
//			//close flash light
//			Camera.Parameters mParameters = camera.getParameters();    
//		    mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);    
//		    camera.setParameters(mParameters);
		    
			camera.release();
			camera = null;
		}
		hasSurface = false;
	}
	//====================================================camera listener end================================
	
	//====================================================activity listener start================================
	@Override
	protected void onPause() {
		super.onPause();
		ComUtils.print("-----> onPause <-----");
		
		
		if (camera != null) {
			camera.stopPreview();
//			//close flash light
//			Camera.Parameters mParameters = camera.getParameters();    
//		    mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);    
//		    camera.setParameters(mParameters);
		    
			camera.release();
			camera = null;
		}
		if (Capturehandler != null) {
			Capturehandler.quitSynchronously();
			Capturehandler = null;
		}
		
		try {
			if (CameraManager.get() != null) {
				CameraManager.get().closeDriver();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		ComUtils.print("-----> onResume <-----");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ComUtils.print("-----> onDestroy <-----");
		this.chkHandler.removeCallbacksAndMessages(null);
		
		if (camera != null) {
			camera.stopPreview();
//			//close flash light
//			Camera.Parameters mParameters = camera.getParameters();    
//		    mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);    
//		    camera.setParameters(mParameters);
		    
			camera.release();
			camera = null;
		}
		if (Capturehandler != null) {
			Capturehandler.quitSynchronously();
			Capturehandler = null;
		}
		
		try {
			if (CameraManager.get() != null) {
				CameraManager.get().closeDriver();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	//====================================================activity listener end================================
}
