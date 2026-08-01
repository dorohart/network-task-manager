package Exceptions;

public class ExistUserException extends Exception {
    private String symbols;

    public ExistUserException(String message, String symbols) {
        super(message);
        this.symbols = symbols;
    }

    public String getSymbols() { return symbols; }
}
