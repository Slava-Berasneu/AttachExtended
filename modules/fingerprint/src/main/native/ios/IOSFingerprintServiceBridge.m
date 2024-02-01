#include "IOSFingerprintServiceBridge.h"
#import "iosFingerprintService/iosFingerprintService-Swift.h"

// Helper functions to call back into Java
void callJavaSuccess(JNIEnv *env, jobject callbackObj) {
    jclass callbackClass = (*env)->GetObjectClass(env, callbackObj);
    jmethodID successMethod = (*env)->GetMethodID(env, callbackClass, "onSuccess", "()V");
    (*env)->CallVoidMethod(env, callbackObj, successMethod);
    (*env)->DeleteGlobalRef(env, callbackObj);
}

void callJavaFailure(JNIEnv *env, jobject callbackObj, NSString *errorMessage) {
    jclass callbackClass = (*env)->GetObjectClass(env, callbackObj);
    jmethodID failureMethod = (*env)->GetMethodID(env, callbackClass, "onFailure", "(Ljava/lang/String;)V");
    jstring jErrorMessage = (*env)->NewStringUTF(env, [errorMessage UTF8String]);
    (*env)->CallVoidMethod(env, callbackObj, failureMethod, jErrorMessage);
    (*env)->DeleteGlobalRef(env, callbackObj);
}

// Global reference to the Java callback object
static jobject globalCallbackRef;

JNIEXPORT void JNICALL Java_com_gluonhq_attachextended_fingerprint_impl_IOSFingerprintService_authenticateWithFingerprint(JNIEnv *env, jobject thisObj, jobject callback) {
    globalCallbackRef = (*env)->NewGlobalRef(env, callback);

    BiometricAuth *biometricAuth = [[BiometricAuth alloc] init];
    [biometricAuth authenticateUserWithCompletion:^(BOOL success, NSString * _Nullable errorMessage) {
        if (success) {
            callJavaSuccess(env, globalCallbackRef);
        } else {
            callJavaFailure(env, globalCallbackRef, errorMessage);
        }
    }];
}
