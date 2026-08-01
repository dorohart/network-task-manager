package Exceptions;

public class AdminException extends Exception {
    private String symbols;

    public AdminException(String message, String symbols) {
        super(message);
        this.symbols = symbols;
    }

    public String getSymbols() { return symbols; }
}
