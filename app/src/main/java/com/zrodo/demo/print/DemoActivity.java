package com.zrodo.demo.print;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lvrenyang.utils.DataUtils;
import com.xmjcphone.AppUtils;
import com.xmjcphone.myprinter.Global;
import com.xmjcphone.myprinter.WorkService;

/**
 * 不同机器usb的处理流的限制不同
 * 所以最好数据包已64字节为单位发送，如果嫌慢可以128字节
 * */
public class DemoActivity extends Activity{
	private static final String TAG = "PhoneDemo";
	
	private static Handler mHandler = null;
	
	private Button btnPrint,btnBack;
	private Button btnPrint1,btnPrint2,btnPrint3,btnPrint_4,btnPrint_stop;
	private TextView tvPrtContent;
	
	private Handler mainHandler = new Handler(){

		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
			if(msg.what == 1){
				String text = (String)msg.obj;
				dev_print(text);//设备打印服务
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_main);
		
		mHandler = new MHandler(this);
		WorkService.addHandler(mHandler);
		
		tvPrtContent = (TextView)findViewById(R.id.tvPrtContent);
		
		
		//返回
		btnBack = (Button)findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		//打印
		btnPrint = (Button)findViewById(R.id.btnPrint);
		btnPrint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dev_print("\n 我是好人 \r\n");
			}
		});
		
		//打印1
		btnPrint1 = (Button)findViewById(R.id.btnPrint_1);
		btnPrint1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StringBuilder sb = new StringBuilder();
				
				sb.append("\r\n\r\n\r\n\r\n");
				sb.append("\n\n");
				
				sb.append("检测对象　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测项目　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测限值　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测样本　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("T C 数值　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("浓度比较　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测结果　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测卡编号：xxxxxxxxxxxxxxx" + "\n");

				sb.append("\r\n");
				
				dev_print(sb.toString());//设备打印服务
			}
		});
		
		
		//打印2
		btnPrint2 = (Button)findViewById(R.id.btnPrint_2);
		btnPrint2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StringBuilder sb = new StringBuilder();
				
				sb.append("\r\n\r\n\r\n\r\n");
				sb.append("\n\n");
				sb.append("===检测项目：xxxxxxxxxxxxxxx" +"===\n");
				sb.append("--------------------------------" + "\n");
				
				//三个步骤的打印设置
				sb.append("检测环节　：xxxxxxxxxxxxxxx \n");
				sb.append("检测站点　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测师　　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测日期　：xxxxxxxxxxxxxxx" + "\n");
				
				//宰前环节
				sb.append("来源产地　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("来源单位　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检疫证号　：xxxxxxxxxxxxxxx" + "\n");
				//批次号
				sb.append("批次号　　：xxxxxxxxxxxxxxx" + "\n");
				//批次头数
				sb.append("批次头数　：xxxxxxxxxxxxxxx" + "\n");
				//耳标号
				sb.append("耳标号　　：xxxxxxxxxxxxxxx" + "\n");

				
				sb.append("检测对象　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测项目　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测限值　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测样本　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("T C 数值　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("浓度比较　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测结果　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测卡编号：xxxxxxxxxxxxxxx" + "\n");
				sb.append("\r\n");
				
				dev_print(sb.toString());//设备打印服务
			}
		});
		

		//打印3  分段打印
		btnPrint3 = (Button)findViewById(R.id.btnPrint_3);
		btnPrint3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StringBuilder sb = new StringBuilder();
				sb.append("\r\n\r\n\r\n\r\n");
				sb.append("\n\n");
				sb.append("===检测项目：xxxxxxxxxxxxxxx" +"===\n");
				sb.append("--------------------------------" + "\n");
				
				//三个步骤的打印设置
				sb.append("检测环节　：xxxxxxxxxxxxxxx \n");
				sb.append("检测站点　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测师　　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测日期　：xxxxxxxxxxxxxxx" + "\n");
				dev_print(sb.toString());
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				sb = new StringBuilder();
				//宰前环节
				sb.append("来源产地　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("来源单位　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检疫证号　：xxxxxxxxxxxxxxx" + "\n");
				//批次号
				sb.append("批次号　　：xxxxxxxxxxxxxxx" + "\n");
				//批次头数
				sb.append("批次头数　：xxxxxxxxxxxxxxx" + "\n");
				//耳标号
				sb.append("耳标号　　：xxxxxxxxxxxxxxx" + "\n");
				dev_print(sb.toString());
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				sb.append("检测对象　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测项目　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测限值　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测样本　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("T C 数值　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("浓度比较　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测结果　：xxxxxxxxxxxxxxx" + "\n");
				sb.append("检测卡编号：xxxxxxxxxxxxxxx" + "\n");
				sb.append("\r\n\r\n\r\n\r\n");
				dev_print(sb.toString());
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		

		btnPrint_4 = (Button)findViewById(R.id.btnPrint_4);
		btnPrint_4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				mainHandler.post(PrtRun);
				
				for(int i=0;i<20;i++){
					StringBuilder sb = new StringBuilder();
					
					sb.append("\r\n\r\n");
					sb.append("\n\n");
					sb.append("===检测项目：xxxxxxxxxxxxxxx" +"===\n");
					sb.append("--------------------------------" + "\n");
					//三个步骤的打印设置
					sb.append("检测环节　：xxxxxxxxxxxxxxx \n");
					sb.append("检测站点　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("检测师　　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("检测日期　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("来源产地　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("来源单位　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("检疫证号　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("批次号　　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("批次头数　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("耳标号　　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("检测对象　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("检测项目　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("检测限值　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("检测样本　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("T C 数值　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("浓度比较　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("检测结果　：xxxxxxxxxxxxxxx" + "\n");
					sb.append("检测卡编号：xxxxxxxxxxxxxxx" + "\n");
					sb.append("\r\n");
					
					dev_print(sb.toString());
					try {
						Thread.sleep(2500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		});


		btnPrint_stop = (Button)findViewById(R.id.btnPrint_stop);
		btnPrint_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				mainHandler.removeCallbacks(PrtRun);
			}
		});
	}
	
	String str = "===////%%%&&&123abc哈哈哈  \n  ===////%%%&&&123abc哈哈哈  \n  ===////%%%&&&123abc哈哈哈  \n  \r\n\r\n\r\n";
	private Runnable PrtRun = new Runnable() {
		@Override
		public void run() {
			while(true){
				dev_print(str);
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	};
	
	
	private void dev_print(String text){
		if (WorkService.workThread.isConnected()) {
			
			byte header[] = null;
			byte strbuf[] = null;

				// 设置GBK编码
				// Android手机默认也是UTF8编码
//				header = new byte[] { 0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39,
//						00 };
				header = new byte[] { 0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39,
						0x01 };
//				header = new byte[] { 0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39,
//						0x05 };
				try {
//					strbuf = text.getBytes("GBK");
					strbuf = text.getBytes("UTF-8");
//					strbuf = text.getBytes("euc-kr");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			

			byte buffer[] = DataUtils.byteArraysToBytes(new byte[][] {
					header, strbuf });
			Bundle data = new Bundle();
			data.putByteArray(Global.BYTESPARA1, buffer);
			data.putInt(Global.INTPARA1, 0);
			data.putInt(Global.INTPARA2, buffer.length);
			
			tvPrtContent.setText("text.size="+text.length() + ",offset=0"+",count="+buffer.length);
			Log.d(TAG,"text.size="+text.length() + ",offset=0"+",count="+buffer.length);
			
			WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data);

		} else {
			Toast.makeText(DemoActivity.this, Global.toast_notconnect,
					Toast.LENGTH_SHORT).show();
		}
	}
	
	

	
	@Override
	protected void onResume() {
		super.onResume();
	}

	//第一个类的话handler不删除
	@Override
	protected void onDestroy() {
		super.onDestroy();
		WorkService.delHandler(mHandler);
		mHandler = null;
	}
	
	
	static class MHandler extends Handler {

		WeakReference<DemoActivity> mActivity;

		MHandler(DemoActivity activity) {
			mActivity = new WeakReference<DemoActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			DemoActivity theActivity = mActivity.get();
			switch (msg.what) {
			/**
			 * DrawerService 的 onStartCommand会发送这个消息
			 */
			//打印结束后
			case Global.CMD_POS_WRITERESULT: {
				int result = msg.arg1;
				Toast.makeText(theActivity,(result == 1) ? Global.toast_success: Global.toast_fail, Toast.LENGTH_SHORT).show();
				Log.v(TAG, "Result: " + result);
				break;
			}
			
			}
		}
	}

	
}
