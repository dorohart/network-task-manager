package Exceptions;

public class UncorrectException extends RuntimeException {
    private String symbols;
    public UncorrectException(String message, String symbols) {
        super(message);
        this.symbols = symbols;
    }
}
