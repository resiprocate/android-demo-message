package org.resiprocate.android.basicmessage;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SipService extends Service {

	final static String SIP_ADDR = "sip:user@example.org";
	final static String SIP_REALM = "example.org";
	final static String SIP_USER = "user";
	final static String SIP_PASSWORD = "test";

	public SipService() {
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("crypto");
		System.loadLibrary("ssl");
		System.loadLibrary("resipares-1.9");
		System.loadLibrary("rutil-1.9");
		System.loadLibrary("resip-1.9");
		System.loadLibrary("dum-1.9");
		System.loadLibrary("BasicPhone");
	}

	final static String TAG = "SipService";

	SipStack mSipStack = null;

	private Timer timer;

	private TimerTask sipPoll = new TimerTask() {
		@Override
		public void run() {
			mSipStack.handleEvents();
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Service starting");

		mSipStack = new SipStack();
		mSipStack.init(SIP_ADDR, SIP_REALM, SIP_USER, SIP_PASSWORD);
		timer = new Timer("SipEventHandler");
		// check for SIP events every 50 ms
		timer.schedule(sipPoll, 50L, 50L);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Service finishing");

		timer.cancel();
		timer = null;

		mSipStack.done();
	}
}
