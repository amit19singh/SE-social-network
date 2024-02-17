package org.sn.socialnetwork.ExceptionHandler;

// Custom exception for email already in use
public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}


