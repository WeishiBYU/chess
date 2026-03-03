package model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, @Expose(serialize = false) ChessGame game) {
    
}
