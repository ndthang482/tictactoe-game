package com.cubeia.tutorial.dice.game;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.action.mtt.MttRoundReportAction;
import com.cubeia.firebase.api.game.GameProcessor;
import com.cubeia.firebase.api.game.TournamentProcessor;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TablePlayerSet;
import com.cubeia.firebase.api.game.table.TableType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.Iterator;

import static java.nio.ByteBuffer.wrap;

public class Processor implements GameProcessor, TournamentProcessor {

    private static final Logger log = Logger.getLogger(Processor.class);

    //Xử lý trong bàn cờ caro
    public void handle(GameDataAction action, Table table) {
        String message = new String(action.getData().array());
//        JsonObject je = (JsonObject) parser.parse(message);

        Board board = (Board) table.getGameState().getState();
        if (action.getPlayerId() == board.getPlayerToAct()) {        	
            int cell = Integer.parseInt(new String(action.getData().array()));
            // xử lý phần chơi game
            Winner winner = board.play(cell, action.getPlayerId());
            // tạo, update dữ liệu khi chơi game
            notifyAllPlayers(table, createGameData(board, "update"));
            // xử lý logic            
            progress(table, board, winner, action.getPlayerId());
        } else { 
            log.info("Ignoring action from " + action.getPlayerId() + ", expected: " + board.getPlayerToAct());
        	
        }
    }

    // xử lý 
    private void progress(Table table, Board board, Winner winner, int playerId) {
        GameData data;
        if (winner == Winner.EVEN) {
            data = createGameData(board, "even");
            data.pid = board.getPlayerToAct();

        }
          else if(winner == Winner.ODD) {
            data = createGameData(board, "odd");
            data.pid = board.getPlayerToAct();

        }
          else if(winner == Winner.FOUR_WHITE) {
            data = createGameData(board, "four_white");
            data.pid = board.getPlayerToAct();

        } else if (winner == Winner.FOUR_YELLOW) {  
            data = createGameData(board, "four_yellow");
            data.pid = board.getPlayerToAct();

        }
          else if (winner == Winner.THREE_WHITE) {  
            data = createGameData(board, "three_white");
            data.pid = board.getPlayerToAct();

        }
          else if (winner == Winner.THREE_YELLOW) {  
            data = createGameData(board, "three_yellow");
            data.pid = board.getPlayerToAct();
        }
          else if(winner == Winner.LOST) {  
            data = createGameData(board, "lost");
            if(winner == Winner.EVEN) {
            data.pid = playerId;
            board.payment = - data.chip * board.playerToAct;
          }else if(winner == Winner.ODD) {
              data.pid = playerId;
              board.payment = - data.chip * board.playerToAct;
            }
          else if(winner == Winner.FOUR_WHITE) {
              data.pid = playerId;
              board.payment = - data.chip * board.playerToAct;
            }
          else if(winner == Winner.FOUR_YELLOW) {
              data.pid = playerId;
              board.payment = - data.chip * board.playerToAct;
            }
          else if(winner == Winner.THREE_WHITE) {
              data.pid = playerId;
              board.payment = - data.chip * board.playerToAct;
            }
          else if(winner == Winner.THREE_YELLOW) {
              data.pid = playerId;
              board.payment = - data.chip * board.playerToAct;
            }
        	startRound(table);
        } else {
            data = createGameData(board, "win");
            if(winner == Winner.EVEN) {
            data.pid = playerId;
            board.payment = data.chip * board.playerToAct;
            }
            else if(winner == Winner.ODD) {
            data.pid = playerId;
            board.payment = data.chip * board.playerToAct;
            }
            else if(winner == Winner.FOUR_WHITE) {
            data.pid = playerId;
            board.payment = data.chip * board.playerToAct*14;
            }	
            else if(winner == Winner.FOUR_YELLOW) {
            data.pid = playerId;
            board.payment = data.chip * board.playerToAct*14;
            }	
            else if(winner == Winner.THREE_WHITE) {
            data.pid = playerId;
            board.payment = data.chip * board.playerToAct*3;
            }	
            else if(winner == Winner.THREE_YELLOW) {
            data.pid = playerId;
            board.payment = data.chip * board.playerToAct*3;
            }	
            if (isTournamentTable(table)) {
                sendRoundReport(table, playerId);
            } else {
            	startRound(table);
            }
        }
        
        notifyAllPlayers(table, data);
        
    }

    // thiết lập bàn cờ chơi game 
    private void sendRoundReport(Table table, int winner) {
        MttRoundReportAction roundReport = new MttRoundReportAction
        		(table.getMetaData().getMttId(), table.getId());
        roundReport.setAttachment(winner);
        log.info("Sending round report where winner is " + winner + 
        		" mttId is " + roundReport.getMttId() + " and tableId is " + roundReport.getTableId());
        table.getTournamentNotifier().sendToTournament(roundReport);
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
        else {
            log.info("Received action with data: " + action.getAttachment());
        }
    }
	public void timeUserJoinGame(Table table) {
    	GameObjectAction action = new GameObjectAction(table.getId());
    	action.setAttachment("userJoinGame");
    	table.getScheduler().scheduleAction(action, 5000);

    }
	public void UserJoinGame(Table table) {
		Board board = (Board) table.getGameState().getState();
		
	}
	// bat dau van moi
    private void timeStartGame(Table table) {
    	GameObjectAction action = new GameObjectAction(table.getId());
    	action.setAttachment("start");
    	table.getScheduler().scheduleAction(action, 3000);
	}
    
    
    private void startGame(Table table) {
    	TablePlayerSet players = table.getPlayerSet();
    	Iterator<GenericPlayer> iterator = players.getPlayers().iterator();
    	Board board = (Board) table.getGameState().getState();
    	board.clear();
    	board.setPlayers(iterator.next().getPlayerId(), iterator.next().getPlayerId(), iterator.next().getPlayerId(),
    			iterator.next().getPlayerId(), iterator.next().getPlayerId(), iterator.next().getPlayerId(),
    			iterator.next().getPlayerId(), iterator.next().getPlayerId(), iterator.next().getPlayerId());
    	GameData data = createGameData(board, "start");
    	notifyAllPlayers(table, data);
    	progress(table, board, Winner.NONE, board.playerToAct);
    }
    
    // tổng time là 20s, cho phép mỗi lần chọn bet được chọn nhiều cửa hoặc chọn 1 cửa nhiều lần 
    // đến khi hết chip
    private void TimeStartBet(Table table) {
    	GameObjectAction action = new GameObjectAction(table.getId());
    	action.setAttachment("timeStartBet");
    	table.getScheduler().scheduleAction(action, 20000);
	}
    
    // finish game thu tien, tra tien
	public void FinishGame(Table table) {
    	GameObjectAction action = new GameObjectAction(table.getId());
    	action.setAttachment("finishGame");
    	table.getScheduler().scheduleAction(action, 3000);
    }
	// cho phep out or join
	public void RisetBoard(Table table) {
    	GameObjectAction action = new GameObjectAction(table.getId());
    	action.setAttachment("risetBoard");
    	table.getScheduler().scheduleAction(action, 3000);
    }

	
    public int creatPointRandom(Table table) {
    	Board board = (Board) table.getGameState().getState();
		int k = 0;
		k += (board.board[0] == 'Z') ? 1 : 0;
		k += (board.board[2] == 'Z') ? 1 : 0;
		if (k > 0) {
			int h = (int) ((k - 1) * Math.random() + 1);
			k = 0;
			k += (board.board[0] == 'Z') ? 1 : 0;
			if (k == h)
				return 0;
			k += (board.board[2] == 'Z') ? 1 : 0;
			if (k == h)
				return 2;
		}
		for (int i = 0; i < 4; i++)
			if (board.board[i] == 'Z')
				k++;
		int h = (int) ((k - 1) * Math.random() + 1);
				k = 0;
		for (int i = 0; i < 4; i++)
			if (board.board[i] == 'Z') {
				k++;
				if (k == h)
					return i;
			}
		return 0;
	}
    
    // Thuc thi hành động update toi' client của người chơi
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

//     Truyền dữ liệu start 3s
    @Override
    public void startRound(Table table) {
        log.info("Starting round " + table.getId() + " in 3 seconds..");
        GameObjectAction action = new GameObjectAction(table.getId());
        action.setAttachment("start");
        table.getScheduler().scheduleAction(action, 3000);
    }

    // stop
    @Override
    public void stopRound(Table table) {
    	GameObjectAction action = new GameObjectAction(table.getId());
        action.setAttachment("stop");
        table.getScheduler().scheduleAction(action, 30000);
    }
}

