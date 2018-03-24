package org.manlier.srapp.component;

public class ComponentNotFoundException extends ComponentException {

    public ComponentNotFoundException(String message) {
        super(message);
    }

    public ComponentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
