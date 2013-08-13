package org.resiprocate.android.basicmessage;

public class SipStack {
	
	// SIP stack lifecycle:
	
	public native void init(String sipUser, String realm, String user, String password);
	
	public native void handleEvents();
	
	public native void done();
	
	// SIP stack communication:
	
	//public native void sendMessage(String recipient, String body);

}
