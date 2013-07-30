


You must have the Android SDK and the NDK

You must have already built the reSIProcate libs for Android, use
build/android-custom-ndk script from the reSIProcate tree.

1. Checkout from git:

      git clone https://github.com/resiprocate/android-demo-message.git
      cd android-demo-message

2. Create transient files:

      android update project -p . -t android-14
      mkdir gen

3. Compile the Java class files

   * You can compile with ant or by importing the project into Eclipse

4. Run build-jni.sh

   * the shared objects should now be under the libs directory
   * if you are using Eclipse, press F5 to detect the new files

5. Build an APK file (e.g. use Export Android Application in Eclipse)

6. Deploy the APK file to a phone,

   adb install BasicMessage.apk

Now you have an app that you can run on Android

Notes:
* the recipient field must include the sip: prefix
* sender address is hard coded
* the stack is reset after each message
* can't receive messages
* if you have DNS problems, try sending to an IP address
* it always uses UDP as the transport
* it binds to a fixed source port defined in the code
* reSIProcate logging doesn't go to the Android logcat tool yet

