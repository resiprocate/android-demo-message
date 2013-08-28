package org.resiprocate.android.basicmessage;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class SipService extends Service {
	
	Logger logger = Logger.getLogger(SipService.class.getCanonicalName());

	String mSipAddr;
	String mSipRealm;
	String mSipUser;
	String mSipPassword;

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
	private static final long INITIAL_DELAY = 50L;
	private static final long STACK_INTERVAL = 50L;

	private static final String ALARM_ACTION = "org.resiprocate.android.basicmessage.SipService.WAKEUP";
	
	AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	private PendingIntent pendingIntentWakeup;

	SipStack mSipStack = null;

	private void setAlarms(long delay) {
		alarmManager.set( AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + STACK_INTERVAL, pendingIntent );
		// Make sure we get woken up again if the phone sleeps:
		alarmManager.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntentWakeup );		
	}
	
	private void cancelAlarms() {
		alarmManager.cancel(pendingIntent);
		alarmManager.cancel(pendingIntentWakeup);
	}
	
	private void runEventLoop() {
		cancelAlarms();
		logger.finest("trying to run event loop");
		long delay = 0;
		while(delay == 0)
			synchronized(mSipStack) {
				delay = mSipStack.handleEvents();
			}
		logger.finest("finished running event loop, next delay = " + delay + " ms");
		setAlarms(delay);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	private void configure() {
	    SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
	    mSipAddr = sp.getString("uri", "anonymous@example.org");
	    mSipRealm = sp.getString("realm", "example.org");
	    mSipUser = sp.getString("user", "anonymous");
	    mSipPassword = sp.getString("password", "123");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		logger.info("Service starting");
		Handler handler; // Handler for the separate Thread

		HandlerThread handlerThread = new HandlerThread("MyNewThread");
		handlerThread.start();
		// Now get the Looper from the HandlerThread so that we can create a Handler that is attached to
		//  the HandlerThread
		// NOTE: This call will block until the HandlerThread gets control and initializes its Looper
		Looper looper = handlerThread.getLooper();
		// Create a handler for the service
		handler = new Handler(looper);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ALARM_ACTION);
		filter.addDataScheme("foo");
		registerReceiver(alarmReceiver, filter, null, handler);
		
		alarmManager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
		pendingIntent = PendingIntent.getBroadcast( this, 0, new Intent(ALARM_ACTION, Uri.parse("foo:normal")), 0);
		pendingIntentWakeup = PendingIntent.getBroadcast( this, 0, new Intent(ALARM_ACTION, Uri.parse("foo:wake")), 0);
		//alarmManager.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + INITIAL_DELAY, pendingIntent );

		mSipStack = new SipStack();
		configure();
		mSipStack.init("sip:" + mSipAddr, mSipRealm, mSipUser, mSipPassword);
		mSipStack.setMessageHandler(mh);
		setAlarms(INITIAL_DELAY);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		logger.info("Service finishing");

		cancelAlarms();
		pendingIntent = null;
		unregisterReceiver(alarmReceiver);
		
		mSipStack.done();
	}

	private final BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ALARM_ACTION)) {
				// do it
				runEventLoop();
			}
		}
	};
	
	private final SipStackRemote.Stub mBinder = new SipStackRemote.Stub() {
		@Override
		public void sendMessage(String recipient, String body) {
			synchronized(mSipStack) {
				mSipStack.sendMessage("sip:" + recipient, body);
			}
		}
	};
	
	private final MessageHandler mh = new MessageHandler() {
		@Override
		public void onMessage(String sender, String body) {
			logger.info("Got message from SIP stack: " + body);

			// TODO: send the SIP MESSAGE body in a Broadcast?
			
			// Make a notification - one of the less elegant things
			// to do in Android programming
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			int icon = R.drawable.ic_launcher;
			CharSequence notiText = body;
			long ts = System.currentTimeMillis();
			Notification notification = new Notification(icon, notiText, ts);

			Context context = getApplicationContext();
			CharSequence contentTitle = "SIP message";
			CharSequence contentText = body;
			Intent notificationIntent = new Intent(SipService.this, MainActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(SipService.this, 0,
					notificationIntent, 0);

			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);

			int SERVER_DATA_RECEIVED = 1;
			notificationManager.notify(SERVER_DATA_RECEIVED, notification);

		}
	};
}
