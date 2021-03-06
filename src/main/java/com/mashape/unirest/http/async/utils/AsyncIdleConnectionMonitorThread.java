package com.mashape.unirest.http.async.utils;

import com.mashape.unirest.http.utils.ConnectionMonitorThread;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class AsyncIdleConnectionMonitorThread extends Thread implements ConnectionMonitorThread {

	private final PoolingNHttpClientConnectionManager connMgr;
	private boolean shutdown;

	public AsyncIdleConnectionMonitorThread(PoolingNHttpClientConnectionManager connMgr) {
		super();
		super.setDaemon(true);
		this.connMgr = connMgr;
		this.shutdown = false;
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				synchronized (this) {
					wait(5000);
					// Close expired connections
					connMgr.closeExpiredConnections();
					// Optionally, close connections
					// that have been idle longer than 30 sec
					connMgr.closeIdleConnections(30, TimeUnit.SECONDS);

					if (shutdown) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
		} catch (InterruptedException ex) {
			// terminate
		}
	}

	public void shutdown() {
		this.shutdown = true;
	}

}
