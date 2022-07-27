package com.cubeia.tutorial.tictactoe.game;

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

import static com.cubeia.tutorial.tictactoe.game.Winner.NONE;
import static java.nio.ByteBuffer.wrap;

public class Processor implements GameProcessor, TournamentProcessor {

    private static final Logger log = Logger.getLogger(Processor.class);

    // Xử lý trong bàn cờ caro
    public void handle(GameDataAction action, Table table) {
        Board board = (Board) table.getGameState().getState();
        if (action.getPlayerId() == board.getPlayerToAct()) {        	
            int cell = Integer.parseInt(new String(action.getData().array()));
            // xử lý phần chơi game
            Winner winner = board.play(cell, action.getPlayerId());
            // tạo, update dữ liệu khi chơi game
            notifyAllPlayers(table, createGameData(board, "update"));
            // xử lý
            progress(table, board, winner, action.getPlayerId());
        } else { 
            log.info("Ignoring action from " + action.getPlayerId() + ", expected: " + board.getPlayerToAct());
        	
        }
    }

    // xử lý 
    private void progress(Table table, Board board, Winner winner, int playerId) {
        GameData data;
        	// act
        if (winner == NONE) {
            data = createGameData(board, "act");
            data.pid = board.getPlayerToAct();
            // hòa
        } else if (winner == Winner.TIE) {
            data = createGameData(board, "tie");
            scheduleNewGame(table);
        } else {
            // người chơi thắng
            data = createGameData(board, "win");
            data.pid = playerId;
            if (isTournamentTable(table)) {
                sendRoundReport(table, playerId);
            } else {
                scheduleNewGame(table);
            }
        }
        notifyAllPlayers(table, data);
    }

    // start game mới trong 5s	
    private void scheduleNewGame(Table table) {
        GameObjectAction action = new GameObjectAction(table.getId());
        action.setAttachment("start");
        table.getScheduler().scheduleAction(action, 5000);
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
        }else if("autoPlay".equals(action.getAttachment())) {
        	autoPlay(table);
        	autoPlayAction(table);
        }
        else {
            log.info("Received action with data: " + action.getAttachment());
        }
    }
    
    private void startGame(Table table) {
    	TablePlayerSet players = table.getPlayerSet();
        // Nếu playId < 2 sẽ ko thể bắt đầu game, ngược lại
        if (players.getPlayerCount() < 2) {
            log.info("Won't start game on table " + table.getId() + " because we only have " + players.getPlayerCount() + " players.");
            return;
        }

        Board board = (Board) table.getGameState().getState();
        board.clear();
        Iterator<GenericPlayer> iterator = players.getPlayers().iterator();
        board.setPlayers(iterator.next().getPlayerId(), iterator.next().getPlayerId());
        GameData data = createGameData(board, "start");
        notifyAllPlayers(table, data);
        
        progress(table, board, Winner.NONE, board.getPlayerToAct());
        
    }
    
    private void autoPlay(Table table) {
    	GameObjectAction action = new GameObjectAction(table.getId());
        action.setAttachment("autoPlay");
        table.getScheduler().scheduleAction(action, 5000);
    }
    
    private void autoPlayAction(Table table) {
    	TablePlayerSet players = table.getPlayerSet();
        // Nếu playId < 2 sẽ ko thể bắt đầu game, ngược lại
        if (players.getPlayerCount() < 2) {
            log.info("Won't start game on table " + table.getId() + " because we only have " + players.getPlayerCount() + " players.");
            return;
        }

        Board board = (Board) table.getGameState().getState();
        board.clear();
        Iterator<GenericPlayer> iterator = players.getPlayers().iterator();
        board.setPlayers(iterator.next().getPlayerId(), iterator.next().getPlayerId());
//        board.setPlayers(1, 2);
        if(board.getPlayerToAct() == 1) {
        	board.play(0, 1);
        	board.play(2, 1);
        	board.play(4, 1);
        	board.play(6, 1);
        	board.play(8, 1);
        } if(board.getPlayerToAct() ==2) {
        	board.play(1, 2);
        	board.play(3, 2);
        	board.play(5, 2);
        	board.play(7, 2);
        }
        GameData data = createGameData(board, "autoPlayAction");
        notifyAllPlayers(table, data);
        
        progress(table, board, Winner.NONE, board.getPlayerToAct());
    }
    // Các hành động của người chơi
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

    // Truyền dữ liệu start 3s
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

    }
}
