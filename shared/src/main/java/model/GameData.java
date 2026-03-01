package model;

import com.google.gson.Gson;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    
    public String toString() {
        return new Gson().toJson(this);
    }
}
