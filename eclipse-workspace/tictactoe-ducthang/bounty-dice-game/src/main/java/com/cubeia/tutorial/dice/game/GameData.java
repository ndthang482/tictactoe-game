package com.cubeia.tutorial.dice.game;

public class GameData {
    String action;
    int pid;
    String board;
    int chip;

    public String getAction() {
        return action;
    }

    public int getPid() {
        return pid;
    }

    public String getBoard() {
        return board;
    }
    public int getChip() {
    	return chip;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setBoard(String board) {
        this.board = board;
    }
    public void setChip(int chip) {
        this.chip = chip;
    }
}
