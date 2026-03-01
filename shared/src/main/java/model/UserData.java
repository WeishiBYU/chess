package model;

import com.google.gson.*;

public record UserData(String username, String password, String email) {

    public String toString() {
        return new Gson().toJson(this);
    }
}
