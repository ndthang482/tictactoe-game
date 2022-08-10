package com.cubeia.tutorial.sesku.game;
import java.util.Random;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.game.GameProcessor;
import com.cubeia.firebase.api.game.TournamentProcessor;
import com.cubeia.firebase.api.game.player.Player;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableType;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static java.nio.ByteBuffer.wrap;

public class Processor implements GameProcessor, TournamentProcessor {

    private static final Logger log = Logger.getLogger(Processor.class);
	private static final int OutGame = -1;
	private static final int JoinGame = 1;
	private static final int o = 3;
	private static final int e = 6;
	private static final int fw = 0;
	private static final int fy = 4;
	private static final int tw = -1;
	private static final int ty = 1;
	private static final int x2Bet = 2;
	
    public int odd;
    public int even;
    public int four_white;
    public int four_yellow;
    public int three_white;
    public int three_yellow;
    int AG_min;    
    public List<Player> players = new ArrayList<Player>();

	//Xử lý trong bàn cờ caro
    public void handle(GameDataAction action, Table table) {
    	 Board board = (Board) table.getGameState().getState();
         if (table.getPlayerSet().getPlayerCount() > 1) {        	
			// xử lý phần chơi game
        	   CheckBoard winner = play(table, RandomBoard(table), action.getPlayerId());
             // tạo, update dữ liệu khi chơi game
             notifyAllPlayers(table, createGameData(board, "update"));
             // xử lý
             progress(table, board, winner, action.getPlayerId());
         } else { 
             log.info("Ignoring action from " + action.getPlayerId() + ", expected: " + board.getPlayerToAct());
         	
         }
     }

    // xử lý 
    private void progress(Table table, Board board, CheckBoard check, int playerId) {
        GameData data;
        if (check == CheckBoard.NONE) {
            data = createGameData(board, "bet");
            data.userId = board.getPlayerToAct();
            
             notifyAllPlayers(table, data);
             timeBet(table);
        }
        else if(check == CheckBoard.EVEN) {
            data = createGameData(board, "even");
            if(check == CheckBoard.LOST) {
            	data.userId = playerId;
            	data.Ag = data.Ag - Bet(table, AG_min);
            }else if(check == CheckBoard.WON) {
            	data.userId = playerId;
            	data.Ag = data.Ag + Bet(table, AG_min);
            }
            
            notifyAllPlayers(table, data);
        	scheduleNewGame(table);

        }
        else if(check == CheckBoard.ODD) {
            data = createGameData(board, "odd");
            if(check == CheckBoard.LOST) {
            	data.userId = playerId;
            	data.Ag = data.Ag - Bet(table, AG_min);
            }else if(check == CheckBoard.WON) {
            	data.userId = playerId;
            	data.Ag = data.Ag + Bet(table, AG_min);
            }
            notifyAllPlayers(table, data);
        	scheduleNewGame(table);

        }
        else if(check == CheckBoard.THREE_WHITE) {
            data = createGameData(board, "three_white");
            if(check == CheckBoard.LOST) {
            	data.userId = playerId;
            	data.Ag = data.Ag - Bet(table, AG_min) * 3;
            }else if(check == CheckBoard.WON) {
            	data.userId = playerId;
            	data.Ag = data.Ag + Bet(table, AG_min) * 3;
            }
            notifyAllPlayers(table, data);
        	scheduleNewGame(table);

        }
        else if(check == CheckBoard.THREE_YELLOW) {
            data = createGameData(board, "three_yellow");
            if(check == CheckBoard.LOST) {
            	data.userId = playerId;
            	data.Ag = data.Ag - Bet(table, AG_min) * 3;
            }else if(check == CheckBoard.WON) {
            	data.userId = playerId;
            	data.Ag = data.Ag + Bet(table, AG_min) * 3;
            }
            notifyAllPlayers(table, data);
        	scheduleNewGame(table);

        }
        
        else if(check == CheckBoard.FOUR_WHITE) {
            data = createGameData(board, "four_white");
            if(check == CheckBoard.LOST) {
            	  data.userId = playerId;
                  data.Ag = data.Ag - Bet(table, AG_min);
            }else if(check == CheckBoard.WON) {
          	  data.userId = playerId;
              data.Ag = data.Ag + Bet(table, AG_min);
            }
            notifyAllPlayers(table, data);
        	scheduleNewGame(table);

        }
        else if(check == CheckBoard.FOUR_YELLOW) {
            data = createGameData(board, "four_yellow");
            if(check == CheckBoard.LOST) {
            	  data.userId = playerId;
                  data.Ag = data.Ag - Bet(table, AG_min) * 14;
            }else if(check == CheckBoard.WON) {
          	  data.userId = playerId;
              data.Ag = data.Ag + Bet(table, AG_min) * 14;
            }
            notifyAllPlayers(table, data);
        	scheduleNewGame(table);

        }
        
    }
    private void scheduleNewGame(Table table) {
        GameObjectAction action = new GameObjectAction(table.getId());
        action.setAttachment("start");
        table.getScheduler().scheduleAction(action, 5000);
    }
    

    // check table
    protected boolean isTournamentTable(Table table) {
        return table.getMetaData().getType() == TableType.MULTI_TABLE_TOURNAMENT;
    }

    public void handle(GameObjectAction action, Table table) {
    	if ("start".equals(action.getAttachment())) {
        	startGame(table);
        	log.info("StartGame: "+ action.getAttachment());
       	}
    	else if ("timeBet".equals(action.getAttachment())) {
    		startBet(table);
        	log.info("timeBet: "+ action.getAttachment());
       	}
    	else if ("finishGame".equals(action.getAttachment())) {
    		finishGame(table);
        	log.info("finishGame: "+ action.getAttachment());
       	}
        else if ("userJoinGame".equals(action.getAttachment()) ||"OutGame".equals(action.getAttachment()) ) {
   		 risetBoard(table);
        	log.info("risetBoard: "+ action.getAttachment());
       	}
        else {
            log.info("Received action with data: " + action.getAttachment());
        }
    }
	public void timeUserJoinGame(Table table) {
    	GameObjectAction action = new GameObjectAction(table.getId());
    	action.setAttachment("userJoinGame");
    	table.getScheduler().scheduleAction(action, 5000);

    }
	// bat dau van moi
    
    private void startGame(Table table) {
    	if(table.getPlayerSet().getPlayerCount() > 1 && table.getPlayerSet().getPlayerCount() < 10) {
    	
    	
    	Board board = (Board) table.getGameState().getState();
    	
    	board.clear();
    	
    	CheckBoard check = play(table, RandomBoard(table), board.playerToAct);
    	
    	setPlayers(players());
    	
    	GameData data = createGameData(board, "start");
    	
    	notifyAllPlayers(table, data);
    	
    	progress(table, board, check, board.playerToAct);
    	
    	scheduleNewGame(table);
    	    	
    	}
    }


    private void timeBet(Table table) {
    	GameObjectAction action = new GameObjectAction(table.getId());
    	action.setAttachment("timeBet");
    	table.getScheduler().scheduleAction(action, 20000);
    	
	}
    // tổng time là 20s, cho phép mỗi lần chọn bet được chọn nhiều cửa hoặc chọn 1 cửa nhiều lần 
    // đến khi hết chip
    private void startBet(Table table) {
    	Board board = (Board) table.getGameState().getState();
    	
    	GameData data = createGameData(board, "timeBet");
    	
    	notifyAllPlayers(table, data);
    	
    	progress(table, board, CheckBoard.NONE, board.playerToAct);
    	
    	timeBet(table);
    }
    
	public void timeFinishGame(Table table) {
    	GameObjectAction action = new GameObjectAction(table.getId());
    	action.setAttachment("finishGame");
    	table.getScheduler().scheduleAction(action, 3000);
    }
	
    // finish game thu tien, tra tien
	
	public void finishGame(Table table) {
    	Board board = (Board) table.getGameState().getState();
    	
    	GameData data = createGameData(board, "finishGame");
    	
    	CheckBoard check = play(table, RandomBoard(table), board.playerToAct);
    	
    	notifyAllPlayers(table, data);
    	
    	if(check == CheckBoard.LOST || check == CheckBoard.WON) {
    		
    	progress(table, board, check, board.playerToAct);
    	
    	timeFinishGame(table);
    	}
	}
	
	public void risetBoard(Table table) {
    	Board board = (Board) table.getGameState().getState();
    	
    	if(board.playerToAct == JoinGame) {

    	GameData data = createGameData(board, "userJoinGame");
    	
    	CheckBoard check = play(table, RandomBoard(table), board.playerToAct);

    	notifyAllPlayers(table, data);
    	
    	progress(table, board, check, board.playerToAct);
    	
    	timeUserJoinGame(table);
    	}
    	else if(board.playerToAct == OutGame) {
    	
    	GameData data1 = createGameData(board, "OutGame");
    	CheckBoard check1 = play(table, RandomBoard(table), board.playerToAct);

    	notifyAllPlayers(table, data1);
    	
    	if(check1 == CheckBoard.LOST || check1 == CheckBoard.WON) {

    	progress(table, board, CheckBoard.NONE, board.playerToAct);
    	
    	timeOutGame(table);
    		}
    	}
    }
	public void timeOutGame(Table table) {
    	GameObjectAction action = new GameObjectAction(table.getId());
    	action.setAttachment("OutGame");
    	table.getScheduler().scheduleAction(action, 2000);
    }
    
    // Thuc thi hành động update toi' client
    private void notifyAllPlayers(Table table, GameData data) {
        GameDataAction action = createAction(table, data);
        table.getNotifier().notifyAllPlayers(action);
    }

    // Tạo dữ liệu các nước đi
    private GameDataAction createAction(Table table, GameData data) {
        GameDataAction action = new GameDataAction(-1, table.getId());
        JSONObject json = JSONObject.fromObject(data);
        action.setData(wrap(json.toString().getBytes()));
        return action;
    }

    // Tạo dữ liệu 
    private GameData createGameData(Board board, String action) {
        GameData data = new GameData();
        data.board = board.toString();
        data.action = action;
        return data;
    }

    public Board RandomBoard(Table table) {
    	Board board = (Board) table.getGameState().getState();
        // in ra 2 số ngẫu nhiên
        for (int i=0; i<4; i++) {
            Random rand = new Random();
            board.board[i] = rand.nextInt(2) + 0;
        }
		return board;
    }
       
    public CheckBoard play(Table table,Board cell, int playerId) {
    	Board board = (Board) table.getGameState().getState();
        if (board == cell) {
        	 if(board.playerToAct == e) {
        		AG_min = AG_min * board.playerToAct;
        	}
        	else if(board.playerToAct == o) {
        		AG_min = AG_min * board.playerToAct;
        	}
        	else if(board.playerToAct == tw) {
        		AG_min = AG_min * board.playerToAct;
        	}
        	else if(board.playerToAct == ty ){
        		AG_min = AG_min * board.playerToAct;
        	}
        	else if(board.playerToAct == fw) {
        		AG_min = AG_min * board.playerToAct;
        	}
        	else if(board.playerToAct == fy) {
        		AG_min = AG_min * board.playerToAct;
        	}
        }
			return getWinner(table, playerId);
		
    }
    private CheckBoard getWinner(Table table, int playerId) {
    	Board board = (Board) table.getGameState().getState(); 
        CheckBoard winner = play(table, RandomBoard(table), board.playerToAct);
        for (int[] combos :getRowCombos()) {
            if (winner == CheckBoard.EVEN) {
            	even = combos[0];
            	even = combos[5];
            	even = combos[6];
            	even = combos[7];
            	even = combos[8];
            	even = combos[9]; 
            	even = combos[18];
            	//check 4 white
            if (winner == CheckBoard.FOUR_WHITE) {
            	  four_white = combos[0];
               			
            	}//check 4 yellow
             else if (winner == CheckBoard.FOUR_YELLOW) {
             	 four_yellow = combos[18];
             	}
            } //check le
            else if(winner == CheckBoard.ODD) {
            	odd = combos[1];
            	odd = combos[2];
            	odd = combos[3];
            	odd = combos[4];
            	odd = combos[10];
            	odd = combos[11];
            	odd = combos[12];
            	odd = combos[13];
            	odd = combos[14];
            	odd = combos[15];
            	odd = combos[16];
            	odd = combos[17];
            	// check 3white 1yellow
            	if(winner == CheckBoard.THREE_WHITE) {
            		three_white = combos[10]; 
            		three_white = combos[11];
            		three_white = combos[12];
            		three_white = combos[13];
            	}//check 3 yellow 1white
            	else if(winner == CheckBoard.THREE_YELLOW){
            		three_yellow = combos[14]; 
            		three_yellow = combos[15];
            		three_yellow = combos[16];
            		three_yellow = combos[17];
            	}
            }
        }
        return winner;
    }

    private static List<int[]> getRowCombos() {
    	List<int[]> combos = new ArrayList<int[]>();
        // 0 4 white even
        // 1 odd
        // 2 odd
        // 3 odd
        // 4 odd
        // 5 even
        // 6 even
        // 7 even
        // 8 even
        // 9 even
        // 10 3white odd
        // 11 3white odd
        // 12 3white odd 
        // 13 3white odd
        // 14 3 yellow odd
        // 15 3 yellow odd
        // 16 3 yellow odd 
        // 17 3 yellow odd
        // 18 4 yellow even
    	combos.add(new int[] {0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1});
        combos.add(new int[] {0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1});
        combos.add(new int[] {0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1});
        combos.add(new int[] {0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1});
        return combos;
    }
    public int Bet(Table table, int AG_min) {
    	Board board = (Board) table.getGameState().getState();
    		if(board.playerToAct == x2Bet) {
    			AG_min = 2 * AG_min * board.playerToAct;
    		}else {
    			AG_min = AG_min * board.playerToAct;
    		}
    	return AG_min;
    } 
    
//   start 3s
	 @Override
	 public void startRound(Table table) {
	     log.info("Starting round " + table.getId() + " in 5 seconds..");
	     GameObjectAction action = new GameObjectAction(table.getId());
	     action.setAttachment("start");
	     table.getScheduler().scheduleAction(action, 5000);
	 }
	
	 // stop
	 @Override
	 public void stopRound(Table table) {
	 	GameObjectAction action = new GameObjectAction(table.getId());
	     action.setAttachment("stop");
	     table.getScheduler().scheduleAction(action, 30000);
	 }
	 
	@SuppressWarnings("unchecked")
	private List<Player> players() {
		return (List<Player>) players.iterator();
	}
	public List<Player> getPlayers() {
		return players;
	}
	 
	 public int getAG_min() {
	    return AG_min;
	 }

	 public void setAG_min(int AG_min) {
	    this.AG_min = AG_min;
	 }
	   
	public int getEven() {
		return even;
	}

	public void setEven(int even) {
		this.even = even;
	}
	public int getOdd() {
		return odd;
	}

	public void setOdd(int odd) {
		this.odd = odd;
	}
	public int getThree_white() {
		return three_white;
	}

	public void setThree_white(int three_white) {
		this.three_white = three_white;
	}
	public int getThree_yellow() {
		return three_white;
	}

	public void setThree_yellow(int three_yellow) {
		this.three_yellow = three_yellow;
	}
	public int getFour_white() {
		return four_white;
	}

	public void setFour_white(int four_white) {
		this.four_white = four_white;
	}
	public int getFour_yellow() {
		return four_yellow;
	}

	public void setFour_yellow(int four_yellow) {
		this.four_yellow = four_yellow;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}
}

