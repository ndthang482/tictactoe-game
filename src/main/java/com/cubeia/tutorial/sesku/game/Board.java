package com.cubeia.tutorial.sesku.game;

import org.apache.log4j.Logger;

import java.io.Serializable;


public class Board implements Serializable {

    private static final long serialVersionUID = 5756907997747980375L;

	public int board[] = {'Z','Z','Z','Z'};
    public int playerToAct;
    private static final Logger log = Logger.getLogger(Board.class);

    public int getPlayerToAct() {
        return playerToAct;
    }

//    public void setPlayers(List<Player> players) {
//        	this.players = players;
//    	log.info("players: "+ players);
//    }
//    

//    private Winner getWinner(int playerId) {
//        Winner winner = Chars.contains(board, 'Z') ? Winner.WHITE : Winner.YELLOW;
//        
//        char symbolToCheck = 0;
//        if(playerId == even) {
//    		symbolToCheck = 'e';
//    	}
//    	else if(playerId == odd) {
//    		symbolToCheck = 'o';
//    	}
//    	else if(playerId == four_white) {
//    		symbolToCheck = 'f';
//    	}
//    	else if(playerId == four_yellow) {
//    		symbolToCheck = 'F';
//    	}
//    	else if(playerId == three_white) {
//    		symbolToCheck = 'W';
//    	}
//    	else if(playerId == three_yellow) {
//    		symbolToCheck = 'Y';
//    	}
//    	else if(playerId == payment) {
//    		symbolToCheck = 'p';
//    	}
//        for (int[] combo :getRowCombos()) {
//        	// check chan 
//            if (board[combo[0]] == symbolToCheck || board[combo[5]] == symbolToCheck ||
//            		board[combo[6]] == symbolToCheck || board[combo[7]] == symbolToCheck 
//            		|| board[combo[11]] == symbolToCheck) {
//                winner = Winner.EVEN;
//            	
//            //check 4 white
//            if (board[combo[0]] == symbolToCheck) {
//
//            	  winner = Winner.FOUR_WHITE;
//               		
//            	
//            }//check 4 yellow
//             else if (board[combo[16]] == symbolToCheck) {
//             	  winner = Winner.FOUR_YELLOW;
//            	
//             }
//            } //check le
//            else if(board[combo[1]] == symbolToCheck || board[combo[2]] == symbolToCheck 
//            		|| board[combo[3]] == symbolToCheck || board[combo[4]] == symbolToCheck
//            		 || board[combo[8]] == symbolToCheck || board[combo[9]] == symbolToCheck
//            		 || board[combo[10]] == symbolToCheck|| board[combo[11]] == symbolToCheck
//            		 || board[combo[12]] == symbolToCheck|| board[combo[13]] == symbolToCheck
//            		 || board[combo[14]] == symbolToCheck|| board[combo[15]] == symbolToCheck) {
//            	if(playerId == odd) {
//            	winner = Winner.ODD;
//            	}
//            	// check 3white 1yellow
//            	if(board[combo[8]] == symbolToCheck || board[combo[9]] == symbolToCheck
//            			|| board[combo[10]] == symbolToCheck) {
//            		winner = Winner.THREE_WHITE;
//            		
//            	}//check 3 yellow 1white
//            	else if(board[combo[12]] == symbolToCheck || board[combo[13]] == symbolToCheck
//            			|| board[combo[14]] == symbolToCheck || board[combo[14]] == symbolToCheck) {
//            		winner = Winner.THREE_YELLOW;
//            		
//            	}
//            }
//        }
//        return winner;
//    }

//
//    private List<int[]> getRowCombos() {
//    	List<int[]> combos = new ArrayList<int[]>();
//    	combos.add(new int[]{0, 0, 0, 0});// 0 chan 4white
//        combos.add(new int[] {0, 0, 0, 1});// 1 le
//        combos.add(new int[] {0, 0, 1, 0});// 2 le
//        combos.add(new int[] {0, 1, 0, 0});// 3 le
//        combos.add(new int[] {1, 0, 0, 0});// 4 le
//        combos.add(new int[] {1, 1, 0, 0});// 5 chan 
//        combos.add(new int[] {1, 0, 1, 0});// 6 chan 
//        combos.add(new int[] {1, 0, 0, 1});// 7 chan 
//        combos.add(new int[] {1, 1, 0, 1});// 8 3white
//        combos.add(new int[] {0, 1, 1, 1});// 9 3white
//        combos.add(new int[] {1, 0, 1, 1});// 10 3white
//        combos.add(new int[] {1, 1, 1, 0});// 11 3white
//        combos.add(new int[] {1, 1, 0, 1});// 12 3yellow
//        combos.add(new int[] {0, 1, 1, 1});// 13 3yellow
//        combos.add(new int[] {1, 0, 1, 1});// 14 3yellow
//        combos.add(new int[] {1, 1, 1, 0});// 15 3yellow
//        combos.add(new int[] {1, 1, 1, 1});// 16 chan 4yellow
//        return combos;
//    }
    
    void clear() {
        board = new int[]{'Z','Z','Z','Z'};
    }


}

