package com.zrodo.demo.print;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lvrenyang.rwbt.BTHeartBeatThread;
import com.lvrenyang.rwusb.PL2303Driver;
import com.lvrenyang.rwusb.USBDriver;
import com.lvrenyang.rwusb.USBHeartBeatThread;
import com.lvrenyang.rwwifi.NETHeartBeatThread;
import com.lvrenyang.utils.DataUtils;
import com.lvrenyang.utils.FileUtils;
import com.zrodo.demo.Global;
import com.zrodo.demo.WorkService;
import com.zrodo.demo.utils.AppUtils;
import com.zrodo.demo.utils.CoreUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "PrintDemo";

    private static Handler mHandler = null;

    private static String ACTION_USB_PERMISSION;
    private PendingIntent mPermissionIntent;//获取外设权限的意图


    private static final int GET_THREAD_CODE = 0x1;
    //get thread to probe
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case GET_THREAD_CODE:
                    Log.d(TAG, "--> handler continue probe");
                    probe();

                    break;

                default:
                    break;
            }
        }

    };

    private Button btnGoFun, btnGoPrint, btnCloseApp, btnJS;
    private Button btnAnyWifiPrint;
    private TextView tvDevid, tvDevParam, tvDevPrinter, tvDevChooseRect;
    private TextView tvUsbDev;
    private RadioGroup rgRect;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init);

        initView();
        initPrinter();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * 初始化控件
     */
    private void initView() {
        //二维码和拍照
        btnGoFun = (Button) findViewById(R.id.btnGoFun);
        btnGoFun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(MainActivity.this, DecodeActivity.class);
                startActivity(it);
            }
        });

        //打印
        btnGoPrint = (Button) findViewById(R.id.btnGoPrint);
        btnGoPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goDemo();
            }
        });

        //关闭app
        btnJS = (Button) findViewById(R.id.btnJS);
        btnJS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(MainActivity.this, JSUploadActivity.class);
                startActivity(it);
            }
        });

        //关闭app
        btnCloseApp = (Button) findViewById(R.id.btnCloseApp);
        btnCloseApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //任何设备的无线打印技术
        btnAnyWifiPrint = (Button) findViewById(R.id.btnAnyWifiPrint);
        btnAnyWifiPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(MainActivity.this, AnyPrinterActivity.class);
                startActivity(it);
            }
        });

        //usb连接的外接设备id
        tvUsbDev = (TextView) findViewById(R.id.tvUsbDev);

        //设备号
        tvDevid = (TextView) findViewById(R.id.tvDevid);
        tvDevid.setText("设备号：" + AppUtils.getServiceId(this));

        //屏幕参数
        tvDevParam = (TextView) findViewById(R.id.tvDevParam);
        tvDevParam.setText("屏幕参数：" + AppUtils.testDevice(this, this));

        //选择二维码屏幕有效参数
        tvDevChooseRect = (TextView) findViewById(R.id.tvDevChooseRect);
        tvDevChooseRect.setText("选择了：" + CoreUtils.frameRect.toString());
        rgRect = (RadioGroup) findViewById(R.id.rgRect);
        rgRect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();

                switch (radioButtonId) {
                    case R.id.rbJinbiao1:
                        CoreUtils.frameRect = new Rect(0, 0, 2048, 1536);
                        CoreUtils.ScreenForward = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                        CoreUtils.DisplayOrientation = 90;//往下
                        CoreUtils.TakePhotoOrientation = 0;//往右
                        CoreUtils.NeedZoom = true;
                        CoreUtils.isHandleSetPrevSize = false;
                        CoreUtils.NeedFocus = true;
                        tvDevChooseRect.setText("选择了：" + CoreUtils.frameRect.toString() + "|横屏");
                        break;

                    //screen(2048,1440) , best-preview-size(1920,1088) , but need biggest-size(1440,1080)
                    //screen all size: ...1280*720,1440*1080,1920*1080,1920*1088
                    case R.id.rbJinbiao2:
//					CoreUtils.frameRect = new Rect(0,0,2048,1536);
                        CoreUtils.frameRect = new Rect(0, 0, 2048, 1440); //like screen point
                        CoreUtils.ScreenForward = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                        CoreUtils.DisplayOrientation = 0;//往下0
                        CoreUtils.TakePhotoOrientation = 270;//往右
                        CoreUtils.NeedZoom = false;
                        CoreUtils.isHandleSetPrevSize = true;
                        CoreUtils.HandlePoint.x = 1440;
                        CoreUtils.HandlePoint.y = 1080;
                        CoreUtils.NeedFocus = true;
                        tvDevChooseRect.setText("选择了：" + CoreUtils.frameRect.toString() + "|反横屏");
                        break;

                    case R.id.rbShouchi1:
                        CoreUtils.frameRect = new Rect(0, 0, 720, 1184);
                        CoreUtils.ScreenForward = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        CoreUtils.DisplayOrientation = 90;//往下
                        CoreUtils.TakePhotoOrientation = 0;//往右
                        CoreUtils.NeedZoom = false;            //-->不放大
//					CoreUtils.isHandleSetPrevSize = true;
//					CoreUtils.HandlePoint.x = 800;
//					CoreUtils.HandlePoint.y = 480;
                        CoreUtils.isHandleSetPrevSize = false;
                        CoreUtils.NeedFocus = false;
                        tvDevChooseRect.setText("选择了：" + CoreUtils.frameRect.toString() + "|竖屏");
                        break;

//				case R.id.rbShouchiPro:
//					CoreUtils.frameRect = new Rect(0,0,540,960);
//					CoreUtils.ScreenForward = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//					CoreUtils.DisplayOrientation = 90;//往下
//					CoreUtils.NeedZoom = true;
//					CoreUtils.isHandleSetPrevSize = false;
//					tvDevChooseRect.setText("选择了："+CoreUtils.frameRect.toString()+"|竖屏");
//					break;

                    default:
                        break;
                }

            }
        });


    }

    /**
     * 初始化打印机
     */
    private void initPrinter() {
        tvDevPrinter = (TextView) findViewById(R.id.tvDevPrinter);//打印机
        //打印机系列===============================================================================
        mHandler = new MHandler(this);
        WorkService.addHandler(mHandler);

        //create work thread
        if (null == WorkService.workThread) {
            Log.d(TAG, "workThread is null,create it.");
            Intent intent = new Intent(this, WorkService.class);
            startService(intent);//start service and create workthread,open this thread maybe wait seconds

            //ei always null
            Log.v(TAG, "--> MainActivity.onCreate" + ((WorkService.workThread == null) ? "workThread is null" : "workThread not null"));
        }

        FileUtils.debug = true;
//		handleIntent(getIntent());//处理intent

        //connect printer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            noticeDev();
            probe();
        } else {
            Toast.makeText(this, "sdk version must > 12,can use printer.", Toast.LENGTH_SHORT).show();
        }
    }

    //第一个类的话handler不删除
    @Override
    protected void onDestroy() {
        super.onDestroy();
        WorkService.delHandler(mHandler);
        mHandler = null;
        unregisterReceiver(mUsbReceiver);
//		stopService(new Intent(this, WorkService.class));//just like exit
    }

//	@Override
//	protected void onNewIntent(Intent intent) {
//		setIntent(intent);
//		handleIntent(intent);
//	}

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 通知监听外设权限注册状态
     * PendingIntent：连接外设的intent
     */
    private void noticeDev() {
        Log.d(TAG, "try notice Dev send broadcast.");
        ACTION_USB_PERMISSION = this.getApplicationInfo().packageName;//action
        //ask permission
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        //注册receiver
        registerReceiver(mUsbReceiver, filter);
    }

    /**
     * 尝试访问具有权限的打印机设备
     * 无权限：请求权限
     * 有权限：获得设备，设置端口
     */
    private void probe() {
        Log.d(TAG, "try probe printer dev.");
        StringBuilder sb = new StringBuilder();

        boolean isPrinter = false;
        final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = usbDevices.values().iterator();

        //try get enable printer dev
        if (usbDevices.size() > 0) {
            while (deviceIterator.hasNext()) {
                UsbDevice dev = deviceIterator.next();
                String vpId = String.format(" VID:%04X | PID:%04X", dev.getVendorId(), dev.getProductId());
                Log.d(TAG, vpId);

                //show usb dev v&p id
                sb.append(vpId + "\n");
                tvUsbDev.setText(sb.toString());

                //to find printer device
                //vid = 1659 = 0x067b , pid = 8963 = 0x2303
                if (dev.getVendorId() == 0x067b || dev.getVendorId() == 0x0FE6) {
                    isPrinter = true;
                    final UsbDevice usbDev = dev;

                    if (!usbManager.hasPermission(usbDev)) {
                        //ask has dev permission,pop dialog
                        Log.d(TAG, "printer dev has no permission,try request it.");
                        usbManager.requestPermission(usbDev, mPermissionIntent);//will recall mUsbReceiver
                    } else {
                        //when dev has granted permission , connect printer by usb
                        Log.d(TAG, "printer dev has granted permission,connect usb.");
                        USBDriver.USBPort port = new USBDriver.USBPort(usbManager, usbDev);
                        PL2303Driver.TTYTermios serial = new PL2303Driver.TTYTermios(9600,
                                PL2303Driver.TTYTermios.FlowControl.NONE, PL2303Driver.TTYTermios.Parity.NONE, PL2303Driver.TTYTermios.StopBits.ONE, 8);

                        if (WorkService.workThread == null) {
                            Log.e(TAG, "usb WorkService.workThread is null.");
//							Toast.makeText(this,"what's the fuck , workThread is null ?? why", Toast.LENGTH_SHORT).show();
                            handler.sendEmptyMessageDelayed(GET_THREAD_CODE, 200);
                        } else {
                            tvDevPrinter.setText("连上打印机");
                            WorkService.workThread.connectUsb(port, serial);
                        }


                    }
                }
            }

        } else {
            tvUsbDev.setText("未找到任何usb外连设备。");
        }


        if (!isPrinter) {
            tvDevPrinter.setText("未有找到任何打印设备。");
            Log.e(TAG, "not find printer.");
            Toast.makeText(this, "not find printer.", Toast.LENGTH_SHORT).show();
        }

    }


    // 监听需要权限的外部连接设备 ，弹出提示框，让用户赋予权限
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "receiver action : " + action);

            if (ACTION_USB_PERMISSION.equals(action)) {// 获取权限
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if ((UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE) != null) {
//							usbDev = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                            //after request dev permission and granted pemission , connect printer by usb
                            Log.d(TAG, "after request dev permission and granted pemission , connect printer by usb.");

                            probe();//go to connect printer
                        }
                    }
                }

            }

        }
    };

    //去demo界面
    private void goDemo() {
        Intent it = new Intent(MainActivity.this, DemoActivity.class);
        startActivity(it);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.zrodo.demo.print/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.zrodo.demo.print/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    //返回监听
    static class MHandler extends Handler {

        WeakReference<MainActivity> mActivity;

        MHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity theActivity = mActivity.get();
            switch (msg.what) {

                /**
                 * DrawerService 的 onStartCommand会发送这个消息
                 */
                case Global.MSG_WORKTHREAD_SEND_CONNECTUSBRESULT:
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? "Connect Printer Success." : "Connect Printer Fail.", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Connect Result: " + result);
                    break;


                case Global.MSG_ALLTHREAD_READY: {
                    Log.v(TAG, "MSG_ALLTHREAD_READY");
                    if (WorkService.workThread.isConnected()) {
//					theActivity.progressBar.setIndeterminate(false);
//					theActivity.progressBar.setProgress(100);
                    } else {
//					theActivity.progressBar.setIndeterminate(false);
//					theActivity.progressBar.setProgress(0);
                    }
                    FileUtils.AddToFile("MHandler MSG_ALLTHREAD_READY\r\n",
                            FileUtils.sdcard_dump_txt);
                    break;
                }

                case BTHeartBeatThread.MSG_BTHEARTBEATTHREAD_UPDATESTATUS:
                case NETHeartBeatThread.MSG_NETHEARTBEATTHREAD_UPDATESTATUS:
                case USBHeartBeatThread.MSG_USBHEARTBEATTHREAD_UPDATESTATUS: {
                    int statusOK = msg.arg1;
                    int status = msg.arg2;
                    Log.v(TAG, "statusOK: " + statusOK + " status: " + DataUtils.byteToStr((byte) status));

//				theActivity.progressBar.setIndeterminate(false);
                    if (statusOK == 1) {
//					theActivity.progressBar.setProgress(100);
                    } else {
//					theActivity.progressBar.setProgress(0);
                    }

                    FileUtils.DebugAddToFile("statusOK: " + statusOK + " status: " + DataUtils.byteToStr((byte) status) + "\r\n", FileUtils.sdcard_dump_txt);
                    break;
                }

                case Global.CMD_POS_PRINTPICTURERESULT:
                case Global.CMD_POS_WRITE_BT_FLOWCONTROL_RESULT: {
                    Log.v(TAG, "Result: " + msg.arg1);
                    break;
                }

            }

        }
    }


//	private void handleIntent(Intent intent) {
//		String action = intent.getAction();
//		String type = intent.getType();
//		if (Intent.ACTION_SEND.equals(action) && type != null) {
//			if ("text/plain".equals(type)) {
//				handleSendText(intent); // Handle text being sent
//			} else if (type.startsWith("image/")) {
//				handleSendImage(intent); // Handle single image being sent
//			} else {
//				handleSendRaw(intent);
//			}
//		}
//	}
//
//	private void handleSendText(Intent intent) {
//		Uri textUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
//		if (textUri != null) {
//			// Update UI to reflect text being shared
//
//			if (WorkService.workThread.isConnected()) {
//				byte[] buffer = { 0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39, 0x01 }; // 设置中文，切换双字节编码。
//				Bundle data = new Bundle();
//				data.putByteArray(Global.BYTESPARA1, buffer);
//				data.putInt(Global.INTPARA1, 0);
//				data.putInt(Global.INTPARA2, buffer.length);
//				WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
//			}
//			if (WorkService.workThread.isConnected()) {
//				String path = textUri.getPath();
//				String strText = FileUtils.ReadToString(path);
//				byte buffer[] = strText.getBytes();
//
//				Bundle data = new Bundle();
//				data.putByteArray(Global.BYTESPARA1, buffer);
//				data.putInt(Global.INTPARA1, 0);
//				data.putInt(Global.INTPARA2, buffer.length);
//				data.putInt(Global.INTPARA3, 128);
//				WorkService.workThread.handleCmd(
//						Global.CMD_POS_WRITE_BT_FLOWCONTROL, data);
//
//			} else {
//				Toast.makeText(this, Global.toast_notconnect,
//						Toast.LENGTH_SHORT).show();
//			}
//
//			finish();
//		}
//	}
//
//	private void handleSendRaw(Intent intent) {
//		Uri textUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
//		if (textUri != null) {
//			// Update UI to reflect text being shared
//			if (WorkService.workThread.isConnected()) {
//				String path = textUri.getPath();
//				byte buffer[] = FileUtils.ReadToMem(path);
//				//Toast.makeText(this, "length:" + buffer.length, Toast.LENGTH_LONG).show();
//				Bundle data = new Bundle();
//				data.putByteArray(Global.BYTESPARA1, buffer);
//				data.putInt(Global.INTPARA1, 0);
//				data.putInt(Global.INTPARA2, buffer.length);
//				data.putInt(Global.INTPARA3, 256);
//				WorkService.workThread.handleCmd(
//						Global.CMD_POS_WRITE_BT_FLOWCONTROL, data);
//
//			} else {
//				Toast.makeText(this, Global.toast_notconnect,
//						Toast.LENGTH_SHORT).show();
//			}
//
//			//finish();
//		}
//	}
//
//	private void handleSendImage(Intent intent) {
//		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
//		if (imageUri != null) {
//			String path = getRealPathFromURI(imageUri);
//
//			BitmapFactory.Options opts = new BitmapFactory.Options();
//			opts.inJustDecodeBounds = true;
//			BitmapFactory.decodeFile(path, opts);
//			opts.inJustDecodeBounds = false;
//			if (opts.outWidth > 1200) {
//				opts.inSampleSize = opts.outWidth / 1200;
//			}
//
//			Bitmap mBitmap = BitmapFactory.decodeFile(path);
//
//			if (mBitmap != null) {
//				if (WorkService.workThread.isConnected()) {
//					Bundle data = new Bundle();
//					data.putParcelable(Global.PARCE1, mBitmap);
//					data.putInt(Global.INTPARA1, 384);
//					data.putInt(Global.INTPARA2, 0);
//					WorkService.workThread.handleCmd(
//							Global.CMD_POS_PRINTPICTURE, data);
//				} else {
//					Toast.makeText(this, Global.toast_notconnect,
//							Toast.LENGTH_SHORT).show();
//				}
//			}
//			finish();
//		}
//	}
//
//	private String getRealPathFromURI(Uri contentUri) {
//		String[] proj = { MediaColumns.DATA };
//		CursorLoader loader = new CursorLoader(this, contentUri, proj, null,
//				null, null);
//		Cursor cursor = loader.loadInBackground();
//		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
//		cursor.moveToFirst();
//		String path = cursor.getString(column_index);
//		cursor.close();
//		return path;
//	}


}
