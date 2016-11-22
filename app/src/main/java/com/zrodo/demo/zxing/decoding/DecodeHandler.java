/*
 * Copyright (C) 2010 ZXing authors
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

import java.util.Hashtable;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
//import com.google.zxing.PlanarYUVLuminanceSource;

final class DecodeHandler extends Handler {

    private static final String TAG = DecodeHandler.class.getSimpleName();
    // 最后的解码类
    private final MultiFormatReader multiFormatReader;


    private CaptureActivityHandler CapActHandler;

    DecodeHandler(CaptureActivityHandler CapActHandler, Hashtable<DecodeHintType, Object> hints) {
        // 获得解码类
        multiFormatReader = new MultiFormatReader();
        // 设置解码参数
        multiFormatReader.setHints(hints);
        this.CapActHandler = CapActHandler;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.decode://from zxing.PreviewCallback
                // Log.d(TAG, "Got decode message");
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case R.id.quit:
                Looper.myLooper().quit();
                break;
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it
     * took. For efficiency, reuse the same reader objects from one decode to
     * the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {

        ComUtils.print("DecodeHandler decode():解码byte[].length=" + data.length + ",width=" + width + ",height=" + height);
        long start = System.currentTimeMillis();
        Result rawResult = null;

        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width; // Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;
        data = rotatedData;

        ComUtils.print("DecodeHandler decode():图片反转byte[].length=" + rotatedData.length + ",width=" + width + ",height=" + height);

        PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(data, width, height);
        ComUtils.print("DecodeHandler decode():获得了PlanarYUVLuminanceSource图片源！");

        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            rawResult = multiFormatReader.decodeWithState(bitmap);
            System.out.print("rawResult:" + rawResult);
        } catch (ReaderException re) {
            // continue
        } finally {
            multiFormatReader.reset();
        }

        if (rawResult != null) {
            ComUtils.print("DecodeHandler decode():获得解码结果：rawResult=" + rawResult.toString() + ",还需recode以下才能得到正解。");
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n"
                    + rawResult.toString());

            Message message = Message.obtain(CapActHandler, R.id.decode_succeeded, rawResult);

            Bundle bundle = new Bundle();
            bundle.putParcelable(DecodeThread.BARCODE_BITMAP,
                    source.renderCroppedGreyscaleBitmap());
            message.setData(bundle);
            // Log.d(TAG, "Sending decode succeeded message...");
            message.sendToTarget();

        } else {
            ComUtils.print("DecodeHandler decode():解码失败,继续预览拍照...");
            Message message = Message.obtain(CapActHandler, R.id.decode_failed);
            message.sendToTarget();
        }
    }

}
