package Exceptions;

public class NotFoundException extends RuntimeException {
    private final String symbols;
    public NotFoundException(String message, String symbols) {
        super(message);
        this.symbols = symbols;
    }
}
