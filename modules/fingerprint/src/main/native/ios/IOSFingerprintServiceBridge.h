// IOSFingerprintServiceBridge.h

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_gluonhq_attachextended_fingerprint_impl_IOSFingerprintService_authenticateWithFingerprint
(JNIEnv *, jobject, jobject);

#ifdef __cplusplus
}
#endif