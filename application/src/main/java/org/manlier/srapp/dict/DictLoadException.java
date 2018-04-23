package org.manlier.srapp.dict;

public class DictLoadException extends DictException {
    public DictLoadException(String message) {
        super(message);
    }

    public DictLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
