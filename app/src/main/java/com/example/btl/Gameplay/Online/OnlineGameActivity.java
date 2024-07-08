package com.example.btl.Gameplay.Online;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.example.btl.Gameplay.GameActitvity;
import com.example.btl.Gameplay.Gameplay;
import com.example.btl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OnlineGameActivity extends GameActitvity implements ValueEventListener,OnCompleteListener<DataSnapshot>{

    FirebaseDatabase database;
    DatabaseReference gameRef;

    String roomId;
    GridView gameBoard;
    Gameplay gameplay;
    String playerName;

    GameRoom room;

    int opponentMove;

    int playerTurn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle info = getIntent().getExtras();

        if(info != null){
            roomId = info.getString("roomId");
            room = (GameRoom) info.getSerializable("GameRoom");
            playerTurn = info.getInt("playerTurn");


            super.setPlayer1Name(room.getPlayer1());
            super.setPlayer2Name(room.getPlayer2());
        }

        super.onCreate(savedInstanceState);


        database = FirebaseDatabase.getInstance();
        gameRef = database.getReference("games").child(roomId);
        gameBoard = super.getGameBoardView();

        gameplay = Gameplay.getInstance();

        room.setRoomState(RoomState.PLAYING);

        gameRef.updateChildren(room.toUpdateMap());
        gameRef.child("turns").addValueEventListener(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        playerName = sharedPreferences.getString("name", "Player");

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (room.getTurns()== playerTurn){

                ImageView imageView = (ImageView) view.findViewById(R.id.iv_game_cell);
                int player= gameplay.getPlayerTurn();

                if (gameplay.move(position)){
                    if (gameplay.getPlayerTurn() == playerTurn){
                        makeMove(position);
                    }

                    updateBoard(imageView,player);


                    if (!checkWinner(position)) {

                        gameplay.setPlayerTurn(player == 1 ? 2 : 1);

                        updateTurnView(gameplay.getPlayerTurn());
                    }
                }
            }

            else  Toast.makeText(this, "It's not your turn", Toast.LENGTH_SHORT).show();

    }

    private void makeMove(int move) {

        String playerKey = (playerTurn == 1) ? "player1" : "player2";
        room.getMoves().put(playerKey, move);

        room.setTurns(room.getTurns() == 1 ? 2 : 1);
        gameRef.updateChildren(room.toUpdateMap());

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        room.setTurns(snapshot.getValue(Integer.class));
        String opponentPlayerKey = (room.getTurns() == 1) ? "player2" : "player1";

        gameRef.child("moves").child(opponentPlayerKey)
                .get().addOnCompleteListener(this);

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }

    @Override
    public void onComplete(@NonNull Task<DataSnapshot> task) {
        Log.e("Turns", String.valueOf(task.getResult().getValue()));
        int move = task.getResult().getValue(Integer.class);
        if (move>=0){

            ImageView imageView = (ImageView) gameBoard.getChildAt(move);

            long itemId = gameBoard.getAdapter().getItemId(move);
            gameBoard.performItemClick(imageView, move, itemId);

        }
    }
}
