package com.gluonhq.helloandroid;

import android.app.Activity;
import android.content.Context;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.util.concurrent.Executor;

public class DalvikAndroidFingerprintService {

    private final Context context;

    // Assume these are the native methods defined elsewhere in your JNI code
    private native void onAuthenticationSuccess();
    private native void onAuthenticationFailure(String errorMessage);

    public DalvikAndroidFingerprintService(Context context) {
        this.context = context;
    }

    public void authenticate() {
        Executor executor = ContextCompat.getMainExecutor(context);
        BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) context, executor,
          new BiometricPrompt.AuthenticationCallback() {
              public void onAuthenticationError(int errorCode, CharSequence errString) {
                  super.onAuthenticationError(errorCode, errString);
                  onAuthenticationFailure(errString.toString());
              }

              public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                  super.onAuthenticationSucceeded(result);
                  onAuthenticationSuccess();
              }

              public void onAuthenticationFailed() {
                  super.onAuthenticationFailed();
                  onAuthenticationFailure("Authentication failed.");
              }
          });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication Required")
                .setSubtitle("Please use your fingerprint to authenticate")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
