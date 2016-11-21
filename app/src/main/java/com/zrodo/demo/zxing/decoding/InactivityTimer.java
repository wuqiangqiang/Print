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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import android.os.Handler;

/**
 * Finishes an activity after a period of inactivity.
 * 定时器,比Timer更好的定时做出任务的方法，Timer是对绝对时间
 * （万一绝对时间突然变到以前了呢），而newSingleThreadScheduledExecutor是相对时间
 */
public final class InactivityTimer {
    // 一次二维码扫描持续8s
    private static final int INACTIVITY_DELAY_SECONDS = 1 * 8;

    // 在线程池中获得定时器线程
    private final ScheduledExecutorService inactivityTimer = Executors
            .newSingleThreadScheduledExecutor(new DaemonThreadFactory());
    private ScheduledFuture<?> inactivityFuture = null;


    /**
     * 定时器给的handler和code
     */
    public InactivityTimer(Handler handler, int code) {
        onActivity(handler, code);// 启动：获得单个有效的ScheduledFuture未来进度
    }

    /**
     * 启动：获得单个有效的ScheduledFuture未来进度
     */
    public void onActivity(Handler handler, int code) {
        //ScheduledFuture设为null
        cancel();
        //获得有效ScheduledFuture，设置任务FinishListener，定时时间20s,时间单位second
        inactivityFuture = inactivityTimer.schedule(new FinishListener(handler, code),
                INACTIVITY_DELAY_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * ScheduledFuture设为null
     */
    private void cancel() {
        if (inactivityFuture != null) {
            inactivityFuture.cancel(true);
            inactivityFuture = null;
        }
    }

    public void shutdown() {
        cancel();
        inactivityTimer.shutdown();
    }

    /**
     * 线程池中分配个线程
     */
    private static final class DaemonThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        }
    }

}
