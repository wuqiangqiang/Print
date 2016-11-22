/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zrodo.demo.zxing.decoding;

import java.util.HashMap;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zrodo.demo.zxing.view.ViewfinderView;

/**
 * This class handles all the messaging which comprises the state machine for
 * capture. 解码处理类，负责掉其他解码线程（DecodeThread）来解码
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

    private static final String TAG = CaptureActivityHandler.class
            .getSimpleName();

    private final DecodeThread decodeThread;// 解码线程
    private State state;// 状态位


    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    private Activity activity;
    private ViewfinderView vfv;
    private Handler handler;
    private int code;


    public CaptureActivityHandler(Activity activity, Handler handler, int code,
                                  Vector<BarcodeFormat> decodeFormats,
                                  String characterSet,
                                  ViewfinderView vfv) {
        this.activity = activity;
        this.handler = handler;
        this.code = code;
        this.vfv = vfv;

        // 获得解码线程
        decodeThread = new DecodeThread(this, decodeFormats, characterSet,
                new ViewfinderResultPointCallback(this.vfv));

        //开启解码线程
        decodeThread.start();
        //状态为为success
        state = State.SUCCESS;

        //相机首次开启预览
        CameraManager.get().startPreview();
        //重启预览和解码
        restartPreviewAndDecode();
    }

    /**
     * dentify R.id.xxx in ids.xml
     * 定义的动作id
     */
    @Override
    public void handleMessage(Message message) {

        switch (message.what) {
            case R.id.auto_focus:
                if (state == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                }
                break;

            case R.id.restart_preview:
                Log.d(TAG, "Got restart preview message");
                restartPreviewAndDecode();
                break;

            //DecodeHandler解码成功
            case R.id.decode_succeeded:
                Log.d(TAG, "Got decode succeeded message");
                state = State.SUCCESS;
                Bundle bundle = message.getData();
                Bitmap barcode = bundle == null ? null : (Bitmap) bundle
                        .getParcelable(DecodeThread.BARCODE_BITMAP);
                // activity.handleDecode((Result) message.obj, barcode);

                Message chkMsg = new Message();
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("result", (Result) message.obj);
                map.put("barcode", barcode);
                chkMsg.obj = map;
                chkMsg.what = code;//DECODE2_OVER_CODE 2 chkactivity
                handler.sendMessage(chkMsg);

                break;

            //DecodeHandler解码失败，再次开启预览
            case R.id.decode_failed:
                // We're decoding as fast as possible, so when one decode fails,
                // start another.
                state = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
                        R.id.decode);
                break;

            case R.id.return_scan_result:
                Log.d(TAG, "Got return scan result message");
                activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
                activity.finish();
                break;

            case R.id.launch_product_query:
                Log.d(TAG, "Got product query message");
                String url = (String) message.obj;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                activity.startActivity(intent);
                break;
        }

    }

    /**
     * 在第一次有了CaptureActivityHandler之后，再次扫描时候如果此类不为null，那么一切参数关闭
     */
    public void quitSynchronously() {

        //状态为为done
        state = State.DONE;
        //关闭预览
        CameraManager.get().stopPreview();

        //发送message,相当于handler.sendmessage
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            decodeThread.join();
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    /**
     * 重启预览和解码
     * 然后开启自动对焦
     * 开启预览拍摄回调函数
     */
    private void restartPreviewAndDecode() {
        //状态为由success到PREVIEW
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            //相机的预览回掉开启和拍照回到开启：对应的回掉 => DecodeHandler.code=R.id.decode => decode()
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
                    R.id.decode);
            //相机的自动对焦开启：对应的回掉code = R.id.auto_focus
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);

            //本来是画扫描框的，没啥用
//			this.vfv.drawViewfinder();
        }
    }

}
