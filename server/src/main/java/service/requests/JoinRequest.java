package service.requests;

public record JoinRequest(String authToken, String playerColor, int gameID) { 

}