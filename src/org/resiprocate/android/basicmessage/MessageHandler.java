package org.resiprocate.android.basicmessage;

public interface MessageHandler {
	
	public void onMessage(String sender, String body);

}
