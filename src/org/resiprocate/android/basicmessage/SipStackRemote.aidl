package org.resiprocate.android.basicmessage;

interface SipStackRemote {

    void sendMessage(String recipient, String body);
    
}
