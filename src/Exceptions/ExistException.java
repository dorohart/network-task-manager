package Exceptions;

public class ExistException extends RuntimeException {
    private final String symbols;
    public ExistException(String message, String symbols) {
        super(message);
        this.symbols = symbols;
    }
}
