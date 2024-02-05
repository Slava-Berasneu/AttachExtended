package com.gluonhq.attachextended.fingerprint;

import com.gluonhq.attach.util.Services;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import java.util.Optional;

/**
 * The Fingerprint Service can be used as
 *
 * FingerprintService.create().ifPresent(service -> {
 *     service.authenticationResultProperty().addListener((obs, oldResult, newResult) -> {
 *         if (newResult.isSuccess()) {
 *             // Handle successful authentication
 *         } else {
 *             // Handle authentication failure, possibly using newResult.getMessage()
 *         }
 *     });
 *
 *     service.authenticate();
 * });
 */

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
     */
    void authenticate();

    /**
     * ReadOnlyObjectProperty for authentication result. The property will hold
     * an AuthenticationResult object which could contain information about
     * the success or failure of the authentication process.
     *
     * @return ReadOnlyObjectProperty for the authentication result.
     */
    ReadOnlyObjectProperty<AuthenticationResult> authenticationResultProperty();

    /**
     * A method to easily access the current value of the authentication result property.
     * @return The current authentication result.
     */
    default AuthenticationResult getAuthenticationResult() {
        return authenticationResultProperty().get();
    }
}
