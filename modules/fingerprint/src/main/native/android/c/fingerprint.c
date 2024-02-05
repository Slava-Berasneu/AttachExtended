#include "util.h"

// Graal handles
static jclass jGraalFingerprintServiceClass;
static jmethodID jGraalAuthenticationSuccessMethod;
static jmethodID jGraalAuthenticationFailureMethod;

// Dalvik handles
static jclass jFingerprintServiceClass;
static jobject jDalvikFingerprintService;

static void initializeGraalHandles(JNIEnv* env) {
    jGraalFingerprintServiceClass = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "com/gluonhq/attachextended/fingerprint/impl/AndroidFingerprintService"));
    jGraalAuthenticationSuccessMethod = (*env)->GetStaticMethodID(env, jGraalFingerprintServiceClass, "onAuthenticationSuccess", "()V");
    jGraalAuthenticationFailureMethod = (*env)->GetStaticMethodID(env, jGraalFingerprintServiceClass, "onAuthenticationFailure", "(Ljava/lang/String;)V");
}

static void initializeDalvikHandles() {
    jclass activityClass = substrateGetActivityClass();
    jobject jActivity = substrateGetActivity();
    jFingerprintServiceClass = GET_REGISTER_DALVIK_CLASS(jFingerprintServiceClass, "com/gluonhq/helloandroid/DalvikFingerprintService");
    ATTACH_DALVIK();
    jmethodID jFingerprintServiceInitMethod = (*dalvikEnv)->GetMethodID(dalvikEnv, jFingerprintServiceClass, "<init>", "(Landroid/content/Context;)V");
    jobject jObj = (*dalvikEnv)->NewObject(dalvikEnv, jFingerprintServiceClass, jFingerprintServiceInitMethod, jActivity);
    jDalvikFingerprintService = (jobject)(*dalvikEnv)->NewGlobalRef(dalvikEnv, jObj);
    DETACH_DALVIK();
}

// JNI OnLoad
JNIEXPORT jint JNICALL
JNI_OnLoad_fingerprint(JavaVM *vm, void *reserved)
{
#ifdef JNI_VERSION_1_8
    JNIEnv* graalEnv;
    if ((*vm)->GetEnv(vm, (void **)&graalEnv, JNI_VERSION_1_8) != JNI_OK) {
        return JNI_FALSE; // JNI version not supported.
    }
    initializeGraalHandles(graalEnv);
    initializeDalvikHandles();
    return JNI_VERSION_1_8;
#else
    #error "Java 8+ SDK is required."
#endif
}

// From Dalvik to native - Callbacks from the Android layer to notify about authentication results
JNIEXPORT void JNICALL Java_com_gluonhq_helloandroid_DalvikFingerprintService_notifyAuthenticationSuccess(JNIEnv *env, jobject service) {
    ATTACH_GRAAL();
    (*graalEnv)->CallStaticVoidMethod(graalEnv, jGraalFingerprintServiceClass, jGraalAuthenticationSuccessMethod);
    DETACH_GRAAL();
}

JNIEXPORT void JNICALL Java_com_gluonhq_helloandroid_DalvikFingerprintService_notifyAuthenticationFailure(JNIEnv *env, jobject service, jstring errorMessage) {
    const char *nativeErrorMessage = (*env)->GetStringUTFChars(env, errorMessage, 0);
    ATTACH_GRAAL();
    (*graalEnv)->CallStaticVoidMethod(graalEnv, jGraalFingerprintServiceClass, jGraalAuthenticationFailureMethod, (*env)->NewStringUTF(env, nativeErrorMessage));
    DETACH_GRAAL();
    (*env)->ReleaseStringUTFChars(env, errorMessage, nativeErrorMessage);
}