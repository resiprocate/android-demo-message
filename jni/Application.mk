
APP_ABI := armeabi
# we have to generalize the resip build script to do
# these other platforms:
APP_ABI += armeabi-v7a
APP_ABI += x86

APP_MODULES := BasicPhone

APP_OPTIM := release 
APP_CFLAGS += -O3

APP_STL := gnustl_shared

