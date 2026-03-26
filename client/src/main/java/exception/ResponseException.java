package exception;

import com.google.gson.Gson;

public class ResponseException extends Exception {
    public enum Code {
        ClientError(400),
        Unauthorized(401),
        Forbidden(403),
        BadRequest(400),
        ServerError(500);

        private final int statusCode;

        Code(int statusCode) {
            this.statusCode = statusCode;
        }

        public int toHttpStatusCode() {
            return statusCode;
        }
    }

    private final Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public Code code() {
        return code;
    }

    public int toHttpStatusCode() {
        return code.toHttpStatusCode();
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static ResponseException fromJson(String json) {
        try {
            ErrorBody body = new Gson().fromJson(json, ErrorBody.class);
            String msg = (body != null && body.message != null) ? body.message : json;
            return new ResponseException(Code.ClientError, msg);
        } catch (Exception e) {
            return new ResponseException(Code.ServerError, json);
        }
    }

    private static class ErrorBody {
        String message;
        String error;
    }

    public static Code fromHttpStatusCode(int statusCode) {
        return switch (statusCode) {
            case 400 -> Code.BadRequest;
            case 401 -> Code.Unauthorized;
            case 403 -> Code.Forbidden;
            case 500 -> Code.ServerError;
            default -> Code.ServerError;
        };
    }
}