
package server;

public class ResponseException extends Exception {
    private final int statusCode;
    
    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public int toHttpStatusCode() {
        return statusCode;
    }
    
    public String toJson() {
        return String.format("{\"message\": \"Error: %s\"}", getMessage());
    }
}
