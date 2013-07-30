package org.resiprocate.android.basicmessage;

public class MessageSender {
	
	public native void sendMessage(String recipient, String body);
	
	static {
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("crypto");
		System.loadLibrary("ssl");
		System.loadLibrary("resipares-1.9");
		System.loadLibrary("rutil-1.9");
		System.loadLibrary("resip-1.9");
		System.loadLibrary("dum-1.9");
        System.loadLibrary("BasicPhone");
    }

}
