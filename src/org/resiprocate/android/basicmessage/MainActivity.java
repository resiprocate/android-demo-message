package org.resiprocate.android.basicmessage;

import java.util.logging.Logger;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	Logger logger = Logger.getLogger(MainActivity.class.getCanonicalName());
	
	SipStackRemote mSipStackRemote;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button buttonSend = (Button) findViewById(R.id.ButtonSend);
		buttonSend.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				EditText recipientField = (EditText)findViewById(R.id.recipient);
				EditText bodyField = (EditText)findViewById(R.id.msg_body);
				
				logger.info("trying to send a message....");
				try {
					mSipStackRemote.sendMessage(
							recipientField.getText().toString(),
							bodyField.getText().toString());
					logger.info("done message send");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					logger.severe("failed message send");
					e.printStackTrace();
				}
				
			}
		});
				
		startService(new Intent(SipService.class.getName()));

		bindService(new Intent(SipService.class.getName()),
                mConnection, Context.BIND_AUTO_CREATE);
		
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Bundle b = intent.getExtras();
	    if (b != null) {
	        String sender = b.getString("sender");
	        if(sender != null)
	        {
	        	logger.info("Replying to sender: " + sender);
	        	EditText recipientField = (EditText)findViewById(R.id.recipient);
	        	recipientField.setText(sender);
	        } else {
	        	logger.info("No sender to reply to");
	        }
	    } else {
	    	logger.info("no extras found in Intent");
	    }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		disconnectService();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
 
        case R.id.action_settings:
            Intent i = new Intent(this, Settings.class);
            startActivity(i);
            break;
            
        case R.id.action_exit:
    		// This tests the DUM and stack shutdown
    		// Otherwise they just keep running in the background
        	disconnectService();
			stopService(new Intent(SipService.class.getName()));
			// Make the activity screen go away:
			MainActivity.this.finish();
			break;
        }
 
        return true;
    }
	
	private ServiceConnection mConnection = new ServiceConnection() {
	    // Called when the connection with the service is established
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // Following the example above for an AIDL interface,
	        // this gets an instance of the IRemoteInterface, which we can use to call on the service
	        mSipStackRemote = SipStackRemote.Stub.asInterface(service);
	    }

	    // Called when the connection with the service disconnects unexpectedly
	    public void onServiceDisconnected(ComponentName className) {
	        logger.severe("Service has unexpectedly disconnected");
	        mSipStackRemote = null;
	    }
	};
	
	private void disconnectService() {
		if(mSipStackRemote == null)
			return;
		unbindService(mConnection);
	}

}
