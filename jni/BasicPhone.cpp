
#include "rutil/Log.hxx"
#include "rutil/Logger.hxx"
#include "rutil/Subsystem.hxx"
#include "resip/dum/ClientAuthManager.hxx"
#include "resip/dum/ClientRegistration.hxx"
#include "resip/dum/DialogUsageManager.hxx"
#include "resip/dum/InviteSessionHandler.hxx"
#include "resip/dum/MasterProfile.hxx"
#include "resip/dum/Profile.hxx"
#include "resip/dum/UserProfile.hxx"
#include "resip/dum/RegistrationHandler.hxx"
#include "resip/dum/ClientPagerMessage.hxx"
#include "resip/dum/ServerPagerMessage.hxx"

#include "resip/dum/DialogUsageManager.hxx"
#include "resip/dum/AppDialogSet.hxx"
#include "resip/dum/AppDialog.hxx"
#include "resip/dum/RegistrationHandler.hxx"
#include "resip/dum/PagerMessageHandler.hxx"
#include "resip/stack/PlainContents.hxx"

#include <iostream>
#include <string>
#include <sstream>

#include "org_resiprocate_android_basicmessage_MessageSender.h"

using namespace std;
using namespace resip;

#define RESIPROCATE_SUBSYSTEM Subsystem::TEST

class ClientMessageHandler : public ClientPagerMessageHandler {
public:
   ClientMessageHandler()
      : finished(false),
        successful(false)
   {
   };

   virtual void onSuccess(ClientPagerMessageHandle, const SipMessage& status)
   {
      InfoLog(<<"ClientMessageHandler::onSuccess\n");
      successful = true;
      finished = true;
   }

   virtual void onFailure(ClientPagerMessageHandle, const SipMessage& status, std::auto_ptr<Contents> contents)
   {
      ErrLog(<<"ClientMessageHandler::onFailure\n");
      successful = false;
      finished = true;
   }

   bool isFinished() { return finished; };
   bool isSuccessful() { return successful; };

private:
   bool finished;
   bool successful;
};

int send_message(int argc, const char *argv[], const char* body)
{
   Log::initialize(Log::Cout, Log::Info, argv[0]);

   if( (argc < 6) || (argc > 7) ) {
      ErrLog(<< "usage: " << argv[0] << " sip:from user passwd realm sip:to [port]\n");
      return 1;
   }

   string from(argv[1]);
   string user(argv[2]);
   string passwd(argv[3]);
   string realm(argv[4]);
   string to(argv[5]);
   int port = 5060;
   if(argc == 7)
   {
      string temp(argv[6]);
      istringstream src(temp);
      src >> port;
   }

   InfoLog(<< "log: from: " << from << ", to: " << to << ", port: " << port << "\n");
   InfoLog(<< "user: " << user << ", passwd: " << passwd << ", realm: " << realm << "\n");

   SharedPtr<MasterProfile> profile(new MasterProfile);
   auto_ptr<ClientAuthManager> clientAuth(new ClientAuthManager());

   SipStack clientStack;
   DialogUsageManager clientDum(clientStack);
   clientDum.addTransport(UDP, port);
   clientDum.setMasterProfile(profile);

   clientDum.setClientAuthManager(clientAuth);
   clientDum.getMasterProfile()->setDefaultRegistrationTime(70);
   clientDum.getMasterProfile()->addSupportedMethod(MESSAGE);
   clientDum.getMasterProfile()->addSupportedMimeType(MESSAGE, Mime("text", "plain"));
   ClientMessageHandler *cmh = new ClientMessageHandler();
   clientDum.setClientPagerMessageHandler(cmh);

   NameAddr naFrom(from.c_str());
   profile->setDefaultFrom(naFrom);
   profile->setDigestCredential(realm.c_str(), user.c_str(), passwd.c_str());

   InfoLog(<< "Sending MESSAGE\n");
   NameAddr naTo(to.c_str());
   ClientPagerMessageHandle cpmh = clientDum.makePagerMessage(naTo);

   Data messageBody(body);
   auto_ptr<Contents> content(new PlainContents(messageBody));
   cpmh.get()->page(content);

   // Event loop - stack will invoke callbacks in our app
   while(!cmh->isFinished())
   {
      clientStack.process(100);
      while(clientDum.process());
   }

   if(!cmh->isSuccessful())
   {
      ErrLog(<< "Message delivery failed, aborting");
      return 1;
   }

   return 0;
}

// Note: this is very crude, it will create a new SIP stack and DUM each
// time a message needs to be sent

// A proper implementation must keep the SIP stack active as long as the
// app is running.

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT void JNICALL Java_org_resiprocate_android_basicmessage_MessageSender_sendMessage
  (JNIEnv *env, jobject jthis, jstring recipient, jstring body)
{
   const char *_recipient = env->GetStringUTFChars(recipient, 0);
   const char *_body = env->GetStringUTFChars(body, 0);
   
   // These should be configured through Android preferences:
   
   const char *argv[8];
   argv[0] = "BasicMessage (JNI)";
   argv[1] = "sip:anonymous@example.org";
   argv[2] = "anonymous";
   argv[3] = "password";
   argv[4] = "realm";
   argv[5] = _recipient;
   
   // we don't use 5060 in case it clashes with some other app on the phone
   argv[6] = "5067";
   
   try {
   	int rc = send_message(7, argv, _body);
   } catch (exception& e)
  {
    cout << e.what() << endl;
  }
  catch(...)
  {
    cout << "some exception!" << endl;
  }
    
  env->ReleaseStringUTFChars(recipient, _recipient);
  env->ReleaseStringUTFChars(body, _body);
}
#ifdef __cplusplus
}
#endif
