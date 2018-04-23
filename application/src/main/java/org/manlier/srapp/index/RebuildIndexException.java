package org.manlier.srapp.index;

public class RebuildIndexException extends RuntimeException {

    public RebuildIndexException(String message) {
        super(message);
    }

    public RebuildIndexException(String message, Throwable cause) {
        super(message, cause);
    }
}
