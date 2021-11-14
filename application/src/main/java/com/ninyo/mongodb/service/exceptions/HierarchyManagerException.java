package com.ninyo.mongodb.service.exceptions;

public class HierarchyManagerException extends RuntimeException {

    public HierarchyManagerException() {
    }

    public HierarchyManagerException(String message) {
        super(message);
    }

    public HierarchyManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public HierarchyManagerException(Throwable cause) {
        super(cause);
    }
}