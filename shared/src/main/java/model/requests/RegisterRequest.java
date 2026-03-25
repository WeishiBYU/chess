package model.requests;

import com.google.gson.Gson;

public record RegisterRequest(String username, String password, String email) {

    public String toString() {
        return new Gson().toJson(this);
    }
}
