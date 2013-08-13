package org.resiprocate.android.basicmessage;

import java.util.logging.Logger;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	Logger logger = Logger.getLogger(MainActivity.class.getCanonicalName());
	
	MessageSender ms = new MessageSender();
	
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
				ms.sendMessage(
						recipientField.getText().toString(),
						bodyField.getText().toString());
				logger.info("done message send");
				
			}
		});
		
		startService(new Intent(SipService.class.getName()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}

}
