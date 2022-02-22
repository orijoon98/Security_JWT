package joon.security.exception;

public class CodeException extends RuntimeException {

    private static final String MESSAGE = "유효하지 않은 인가코드입니다.";

    public CodeException() {
        super(MESSAGE);
    }
}
