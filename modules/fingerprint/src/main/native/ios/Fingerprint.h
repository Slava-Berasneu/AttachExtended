#import <UIKit/UIKit.h>
#import <LocalAuthentication/LocalAuthentication.h>
#include "jni.h"
#include "AttachMacros.h"

@interface Fingerprint : NSObject
- (void) authenticateWithPolicy:(LAPolicy)policy;
@end

void notifyAuthenticationSuccess(JNIEnv *env);
void notifyAuthenticationFailure(JNIEnv *env, NSString *message);