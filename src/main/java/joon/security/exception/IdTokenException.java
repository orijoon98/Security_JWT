package joon.security.exception;

public class IdTokenException extends RuntimeException {

    private static final String MESSAGE = "유효하지 않은 idToken입니다.";

    public IdTokenException() {
        super(MESSAGE);
    }
}
