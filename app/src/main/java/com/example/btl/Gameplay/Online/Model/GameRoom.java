package com.example.btl.Gameplay.Online.Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GameRoom implements Serializable {

    private Map<String, Integer> moves = new HashMap<String, Integer>();

    private String roomId;

    private String player1;

    private String player2;

    private int turns;

    private RoomState roomState;


    public GameRoom(){

    }

    public Map<String, Integer> getMoves() {
        return moves;
    }

    public void setMoves(Map<String, Integer> moves) {
        this.moves = moves;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public int getTurns() {
        return turns;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }


    public RoomState getRoomState() {
        return roomState;
    }

    public void setRoomState(RoomState roomState) {
        this.roomState = roomState;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "GameRoom{" +
                "moves=" + moves +
                ", roomId='" + roomId + '\'' +
                ", player1='" + player1 + '\'' +
                ", player2='" + player2 + '\'' +
                ", turns=" + turns +
                ", roomState=" + roomState +
                '}';
    }

    public Map<String,Object> toInitialMap(){
        Map<String,Object> map = new HashMap<>();
        moves.put("player1",  -1);
        moves.put("player2",  -1);
        map.put("roomId", roomId);
        map.put("moves", moves);
        map.put("player1",player1);
        map.put("player2",player2);
        map.put("turns", turns);
        map.put("roomState", roomState);
        return map;
    }



    public Map<String,Object> toUpdateMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("moves", moves);
        map.put("roomId", roomId);
        map.put("player1",player1);
        map.put("player2",player2);
        map.put("turns", turns);
        map.put("roomState", roomState);
        return map;
    }
    public String createId(String lastId){
        int intId = Integer.parseInt(lastId)+1;

        return String.format("%04d",intId);
    }
}
