package com.gluonhq.attachextended.fingerprint;

public class AuthenticationResult
{
    private final boolean success;
    private final String message;

    public AuthenticationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
