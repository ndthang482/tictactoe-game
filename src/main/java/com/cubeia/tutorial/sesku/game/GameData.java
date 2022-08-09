package com.cubeia.tutorial.sesku.game;

public class GameData {
    String action;
    int userId;
    String board;
    Long Ag;
    public String getAction() {
        return action;
    }

    public int getUserId() {
        return userId;
    }

    public String getBoard() {
        return board;
    }
    public Long getAg() {
        return Ag;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setBoard(String board) {
        this.board = board;
    }
    public void setAg(Long Ag) {
        this.Ag = Ag;
    }
}
