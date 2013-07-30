#!/bin/bash

set -e

NDK_HOME=${HOME}/android/android-ndk-r8e/

export NDK_MODULE_PATH=${HOME}/ndk-prebuilt

cd jni

# generate JNI C function prototypes by reading the native
# method prototypes in the Java code:

javah -jni -classpath ../bin/classes \
  org.resiprocate.android.basicmessage.MessageSender

${NDK_HOME}/ndk-build

