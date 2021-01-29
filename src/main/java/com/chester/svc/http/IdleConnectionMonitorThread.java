package com.chester.svc.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

@Slf4j
public  class IdleConnectionMonitorThread extends Thread {

    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
        super();
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);
                    // 关闭超时的连接
                    connMgr.closeExpiredConnections();
                    // 关闭空闲时间大于30s的连接
                    connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            // terminate
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}