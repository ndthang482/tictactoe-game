package com.cubeia.tutorial.dice.game;

import com.google.common.primitives.Chars;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable {

    public char[] board = {'Z','Z','Z','Z'};
    public int playerToAct;
    public int four_white;
    public int four_yellow;
    public int three_white;
    public int three_yellow;
    public int odd;
    public int even;
    public int white;
    public int yellow;
    public int payment;
    private static final Logger log = Logger.getLogger(Board.class);

    @Override
    public String toString() {
        return new String(board);
    }

    public int getPlayerToAct() {
        return playerToAct;
    }

    public void setPlayers(int four_white, int four_yellow, int three_white, int three_yellow, 
    		int odd, int even, int white, int yellow, int payment) {
        log.info("four_white = " + four_white + " four_yellow = " + four_yellow + 
        		"three_white" +three_white + "three_yellow" + three_yellow
        		+ "odd" + odd + "even" + even);
        this.four_white = four_white;
        this.four_yellow = four_yellow;
        this.even = even;
        this.odd = odd;
        this.three_white = three_white;
        this.three_yellow = three_yellow;
        this.white = white;
        this.yellow = yellow;
        this.payment = payment;
    }
    
    public Winner play(int cell, int playerId) {
        if (board[cell] == 'Z') {
        	
        	if(playerToAct == even) {
        		playerToAct = even;
        	}
        	else if(playerToAct == odd) {
        		playerToAct = odd;
        	}
        	else if(playerToAct == four_white) {
        		playerToAct = four_white;
        	}
        	else if(playerToAct == four_yellow) {
        		playerToAct = four_yellow;
        	}
        	else if(playerToAct == three_white) {
        		playerToAct = three_white;
        	}
        	else if(playerToAct == three_yellow) {
        		playerToAct = three_yellow;
        	}
        }
        return getWinner(playerId);
    }

    private Winner getWinner(int playerId) {
        Winner winner = Chars.contains(board, 'Z') ? Winner.WHITE : Winner.YELLOW;
        
        char symbolToCheck = 0;
        if(playerId == even) {
    		symbolToCheck = 'e';
    	}
    	else if(playerId == odd) {
    		symbolToCheck = 'o';
    	}
    	else if(playerId == four_white) {
    		symbolToCheck = 'f';
    	}
    	else if(playerId == four_yellow) {
    		symbolToCheck = 'F';
    	}
    	else if(playerId == three_white) {
    		symbolToCheck = 'W';
    	}
    	else if(playerId == three_yellow) {
    		symbolToCheck = 'Y';
    	}
    	else if(playerId == payment) {
    		symbolToCheck = 'p';
    	}
        for (int[] combo :getRowCombos()) {
        	// check chan 
            if (board[combo[0]] == symbolToCheck || board[combo[5]] == symbolToCheck ||
            		board[combo[6]] == symbolToCheck || board[combo[7]] == symbolToCheck 
            		|| board[combo[11]] == symbolToCheck) {
                winner = Winner.EVEN;
            	
            //check 4 white
            if (board[combo[0]] == symbolToCheck) {

            	  winner = Winner.FOUR_WHITE;
               		
            	
            }//check 4 yellow
             else if (board[combo[11]] == symbolToCheck) {
             	  winner = Winner.FOUR_YELLOW;
            	
             }
            } //check le
            else if(board[combo[1]] == symbolToCheck || board[combo[2]] == symbolToCheck 
            		|| board[combo[3]] == symbolToCheck || board[combo[4]] == symbolToCheck
            		 || board[combo[8]] == symbolToCheck || board[combo[9]] == symbolToCheck
            		 || board[combo[10]] == symbolToCheck|| board[combo[11]] == symbolToCheck
            		 || board[combo[12]] == symbolToCheck|| board[combo[13]] == symbolToCheck) {
            	winner = Winner.ODD;
            	
            	// check 3white 1yellow
            	if(board[combo[8]] == symbolToCheck || board[combo[9]] == symbolToCheck
            			| board[combo[10]] == symbolToCheck) {
            		winner = Winner.THREE_WHITE;
            		
            	}//check 3 yellow 1white
            	else if(board[combo[11]] == symbolToCheck || board[combo[12]] == symbolToCheck
            			| board[combo[13]] == symbolToCheck) {
            		winner = Winner.THREE_YELLOW;
            		
            	}
            }
        }
        return winner;
    }

    private List<int[]> getRowCombos() {
        List<int[]> combos = new ArrayList<int[]>();
        combos.add(new int[] {0, 0, 0, 0});// 0 chan 4white
        combos.add(new int[] {0, 0, 0, 1});// 1 le
        combos.add(new int[] {0, 0, 1, 0});// 2 le
        combos.add(new int[] {0, 1, 0, 0});// 3 le
        combos.add(new int[] {1, 0, 0, 0});// 4 le
        combos.add(new int[] {1, 1, 0, 0});// 5 chan
        combos.add(new int[] {1, 0, 1, 0});// 6 chan
        combos.add(new int[] {1, 0, 0, 1});// 7 chan
        combos.add(new int[] {1, 1, 0, 1});// 8 3white
        combos.add(new int[] {0, 1, 1, 1});// 9 3white
        combos.add(new int[] {1, 0, 1, 1});// 10 3white
        combos.add(new int[] {1, 1, 0, 1});// 11 3yellow
        combos.add(new int[] {0, 1, 1, 1});// 12 3yellow
        combos.add(new int[] {1, 0, 1, 1});// 13 3yellow
        combos.add(new int[] {1, 1, 1, 1});// 14 chan 4yellow
        return combos;
    }

    void clear() {
        board = new char[]{'Z','Z','Z','Z'};
    }

}

