
This is a basic Android app demonstrating how to use reSIProcate on
Android.  It sends a text message using the SIP MESSAGE method.

You must have the Android SDK, the NDK and a recent version of Gradle.

1. Checkout the reSIProcate ndkports fork and this demo from git:

      git clone https://github.com/resiprocate/resiprocate.git
      git clone https://github.com/resiprocate/ndkports.git
      git clone https://github.com/resiprocate/android-demo-message.git

2. Put a reSIProcate tarball into /tmp

      cd resiprocate
      build/release-tarball.sh
      cp resiprocate-1.13.0~alpha1.tar.gz /tmp/reSIProcate-snapshot.tar.gz

3. Build AAR files for OpenSSL and reSIProcate in ndkports:

      cd ../ndkports
      git checkout pocock/resiprocate
      gradle -PndkPath=/home/daniel/Android/Sdk/ndk/23.1.7779620 release -x test

4. Build android-demo-message:

      cd ../android-demo-message
      gradle assembleDebug

5. Deploy the APK file to a phone,

   adb install ./build/outputs/apk/debug/android-demo-message-debug.apk

Now you have an app that you can run on Android

Notes:
* the recipient field must include the sip: prefix
* sender address is hard coded
* the stack is reset after each message
* can't receive messages yet
* discovery of DNS servers from system properties net.dns1 and net.dns2
* if you have DNS problems, try sending to an IP address
* it always uses UDP as the transport
* it binds to a fixed source port defined in the code, hopefully
  this won't clash with a port in use by any other app
* reSIProcate's logging messages are logged to the Android
  logging facility and they can be monitoring with `adb logcat`

http://www.resiprocate.org

Copyright (C) 2013-2022 Daniel Pocock
https://danielpocock.com  <daniel@pocock.com>
https://softwarefreedom.institute

