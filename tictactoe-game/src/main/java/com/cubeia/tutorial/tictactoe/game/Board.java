package com.cubeia.tutorial.tictactoe.game;

import com.google.common.primitives.Chars;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable {

    private char[] board = {'Z','Z','Z','Z','Z','Z','Z','Z','Z'};
    private int playerToAct;
    private int cross;
    private int naught;
    private static final Logger log = Logger.getLogger(Board.class);

    @Override
    public String toString() {
        return new String(board);
    }

    public int getPlayerToAct() {
        return playerToAct;
    }

    public void setPlayers(int cross, int naught) {
        log.info("Cross = " + cross + " Naught = " + naught);
        this.cross = cross;
        this.naught = naught;
        playerToAct = cross;
    }
    
    public Winner play(int cell, int playerId) {
        if (board[cell] == 'Z') {
            board[cell] = (playerId == cross) ? 'X' : 'O';
            playerToAct = (playerToAct == cross) ? naught : cross;
        }
        return getWinner(playerId);
    }

    private Winner getWinner(int playerId) {
        Winner winner = Chars.contains(board, 'Z') ? Winner.NONE : Winner.TIE;
        char symbolToCheck = (playerId == cross) ? 'X' : 'O';
        for (int[] combo :getRowCombos()) {
            if (board[combo[0]] == symbolToCheck && board[combo[1]] == symbolToCheck &&
            		board[combo[2]] == symbolToCheck) {
                winner = (playerId == cross) ? Winner.CROSS : Winner.NAUGHT;
            }
        }
        return winner;
    }
   
    private List<int[]> getRowCombos() {
        List<int[]> combos = new ArrayList<int[]>();
        combos.add(new int[] {0, 1, 2});
        combos.add(new int[] {0, 3, 6});
        combos.add(new int[] {0, 4, 8});
        combos.add(new int[] {2, 5, 8});
        combos.add(new int[] {2, 4, 6});
        combos.add(new int[] {1, 4, 7});
        combos.add(new int[] {3, 4, 5});
        combos.add(new int[] {6, 7, 8});
        return combos;
    }

    void clear() {
        board = new char[]{'Z','Z','Z','Z','Z','Z','Z','Z','Z'};
    }

}
