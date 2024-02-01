package com.gluonhq.helloandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.content.Context;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.util.concurrent.Executor;

public class AndroidNativeFingerprintService {

    private final Context context;

    public AndroidNativeFingerprintService(Context context) {
        this.context = context;
    }

    public void authenticate(FingerprintAuthenticationCallback callback) {
        Executor executor = ContextCompat.getMainExecutor(context);
        BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) context, executor,
                                                              new BiometricPrompt.AuthenticationCallback() {
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                callback.onFailure(errString.toString());
            }

            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                callback.onSuccess();
            }

            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                callback.onFailure("Authentication failed.");
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
