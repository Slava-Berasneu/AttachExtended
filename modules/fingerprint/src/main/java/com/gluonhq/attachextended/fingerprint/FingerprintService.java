package com.gluonhq.attachextended.fingerprint;

import com.gluonhq.attach.util.Services;
import java.util.Optional;

public interface FingerprintService {

    /**
     * Returns an instance of {@link FingerprintService}.
     * @return An instance of {@link FingerprintService}.
     */
    static Optional<FingerprintService> create() {
        return Services.get(FingerprintService.class);
    }

    /**
     * Initiates a fingerprint authentication process.
     *
     * @param callback A callback to handle the authentication result.
     */
    void authenticate(FingerprintAuthenticationCallback callback);

    /**
     * Interface for handling the result of fingerprint authentication.
     */
    interface FingerprintAuthenticationCallback {
        /**
         * Called when authentication is successful.
         */
        void onSuccess();

        /**
         * Called when authentication fails.
         */
        void onFailure(String errorMessage);
    }
}
