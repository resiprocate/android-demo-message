


You must have the Android SDK and the NDK

You must have already built the reSIProcate libs for Android, use
build/android-custom-ndk script from the reSIProcate tree.


1. Compile the Java class files

2. Run the build-jni.sh

   * the shared objects should now be under the libs directory
   * if you are using Eclipse, press F5 to detect the new files

3. Build an APK file (e.g. use Export Android Application in Eclipse)


Now you have an app that you can run on Android

Notes:
* the recipient field must include the sip: prefix
* sender address is hard coded
* the stack is reset after each message
* can't receive messages
* if you have DNS problems, try sending to an IP address
* it always uses UDP as the transport
* it binds to a fixed source port defined in the code


