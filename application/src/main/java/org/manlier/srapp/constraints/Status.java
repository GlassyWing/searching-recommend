package org.manlier.srapp.constraints;

public enum Status {
    SUCCESS(0, "Operation success."),
    FATAL(100, "Ops, Operation failed."),
    RESOURCE_NOT_FOUND(34, "Sorry, the requested resource does not exist"),
    CREATED(201, "New resource has been created"),
    NOT_CONTENT(204, "The resource was successfully deleted."),
    BAD_REQUEST(400, "The request was invalid or cannot be served."),
    UNAUTHORIED(401, "The request requires an user authentication"),
    INTERNAL_SERVER_ERROR(500, " Internal Server Error");

    Status(int code, String message) {
        this.code = code;
        this.message = message;

    }

    private int code;
    private String message;

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
