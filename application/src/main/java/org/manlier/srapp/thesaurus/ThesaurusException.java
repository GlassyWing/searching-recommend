package org.manlier.srapp.thesaurus;

public class ThesaurusException extends RuntimeException {

    public ThesaurusException(String message) {
        super(message);
    }

    public ThesaurusException(String message, Throwable cause) {
        super(message, cause);
    }
}
