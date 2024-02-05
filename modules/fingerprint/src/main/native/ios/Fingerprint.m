#include "Fingerprint.h"

JNIEnv *env;

JNIEXPORT jint JNICALL
JNI_OnLoad_Fingerprint(JavaVM *vm, void *reserved)
{
#ifdef JNI_VERSION_1_8
    if ((*vm)->GetEnv(vm, (void **)&env, JNI_VERSION_1_8) != JNI_OK) {
        return JNI_VERSION_1_4;
    }
    return JNI_VERSION_1_8;
#else
    return JNI_VERSION_1_4;
#endif
}

static int fingerprintInited = 0;

// Fingerprint
jclass jFingerprintServiceClass;
jmethodID jFingerprintService_onAuthenticationSuccess;
jmethodID jFingerprintService_onAuthenticationFailure;
Fingerprint *_fingerprint;

JNIEXPORT void JNICALL Java_com_gluonhq_attach_fingerprint_impl_IOSFingerprintService_initFingerprint
(JNIEnv *env, jclass jClass)
{
    if (fingerprintInited)
    {
        return;
    }
    fingerprintInited = 1;

    jFingerprintServiceClass = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "com/gluonhq/attach/fingerprint/impl/IOSFingerprintService"));
    jFingerprintService_onAuthenticationSuccess = (*env)->GetStaticMethodID(env, jFingerprintServiceClass, "onAuthenticationSuccess", "()V");
    jFingerprintService_onAuthenticationFailure = (*env)->GetStaticMethodID(env, jFingerprintServiceClass, "onAuthenticationFailure", "(Ljava/lang/String;)V");

    _fingerprint = [[Fingerprint alloc] init];
}

JNIEXPORT void JNICALL Java_com_gluonhq_attach_fingerprint_impl_IOSFingerprintService_authenticate
(JNIEnv *env, jclass jClass)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [_fingerprint authenticateWithPolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics];
    });
}

void notifyAuthenticationSuccess(JNIEnv *env) {
    (*env)->CallStaticVoidMethod(env, jFingerprintServiceClass, jFingerprintService_onAuthenticationSuccess);
}

void notifyAuthenticationFailure(JNIEnv *env, NSString *message) {
    jstring jMessage = (*env)->NewStringUTF(env, [message UTF8String]);
    (*env)->CallStaticVoidMethod(env, jFingerprintServiceClass, jFingerprintService_onAuthenticationFailure, jMessage);
}

@implementation Fingerprint

- (void)authenticateWithPolicy:(LAPolicy)policy {
    LAContext *context = [[LAContext alloc] init];
    NSError *error = nil;

    if ([context canEvaluatePolicy:policy error:&error]) {
        [context evaluatePolicy:policy localizedReason:@"Authenticate to proceed" reply:^(BOOL success, NSError *error) {
            if (success) {
                notifyAuthenticationSuccess(env);
            } else {
                NSString *message = [NSString stringWithFormat:@"Authentication failed: %@", error.localizedDescription];
                notifyAuthenticationFailure(env, message);
            }
        }];
    } else {
        NSString *message = [NSString stringWithFormat:@"Authentication not available: %@", error.localizedDescription];
        notifyAuthenticationFailure(env, message);
    }
}

@end