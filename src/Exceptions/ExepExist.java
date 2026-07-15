package Exceptions;

public class ExepExist extends RuntimeException {
    private final String symbols;
    public ExepExist(String message, String symbols) {
        super(message);
        this.symbols = symbols;
    }
}
