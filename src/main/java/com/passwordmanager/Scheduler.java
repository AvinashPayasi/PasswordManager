/*
package com.passwordmanager;

import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Scheduler {

    private byte[] dataKey;
    private char[] password;

    private Runnable dataKeyScheduler = () -> {
        Arrays.fill(dataKey, (byte) 0);
        dataKey = null;
    };

    public void scheduleSessionKey(byte[] sessionKey) {
        this.dataKey = sessionKey;
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.schedule(dataKeyScheduler, 60, TimeUnit.SECONDS);
        scheduledThreadPoolExecutor.shutdown();
    }

}

*/
