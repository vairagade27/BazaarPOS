package com.codexaa.exception;

public class UserExceptions extends Exception {

    public UserExceptions(String message) {
        super(message);
    }

    public UserExceptions(String message, Throwable cause) {
        super(message, cause);
    }

    // ── sub-types ─────────────────────────────────────────────────────────────

    public static class NotFoundException extends UserExceptions {
        public NotFoundException(String message) { super(message); }
    }

    public static class AccessDeniedException extends UserExceptions {
        public AccessDeniedException(String message) { super(message); }
    }

    public static class InsufficientStockException extends UserExceptions {
        public InsufficientStockException(String message) { super(message); }
    }

    public static class InvalidOrderStatusException extends UserExceptions {
        public InvalidOrderStatusException(String message) { super(message); }
    }

    public static class DuplicateResourceException extends UserExceptions {
        public DuplicateResourceException(String message) { super(message); }
    }
}