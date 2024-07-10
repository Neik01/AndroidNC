package com.example.btl.Gameplay.Online;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import com.example.btl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WaitingRoomActivity extends AppCompatActivity implements ValueEventListener {

    FirebaseDatabase database;
    DatabaseReference gameRef;

    String playerName;
    String roomId;

    TextView readyText,player1,player2;

    LinearLayout playerContainer;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_waiting_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        readyText = findViewById(R.id.ready_text);
        readyText.setText("Đang tìm đối thủ");

        playerContainer = findViewById(R.id.player_container);
        playerContainer.setVisibility(View.GONE);

        player1 = findViewById(R.id.player1_name);
        player2 = findViewById(R.id.player2_name);

        database = FirebaseDatabase.getInstance();
        gameRef = database.getReference("games");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        playerName= sharedPreferences.getString("name", "Player 1");



        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String origin = extras.getString("origin");
            if (origin.equals("MainActivity")){
                findRoom();
            } else if (origin.equals("ResultFragment")) {

                String roomId = extras.getString("roomId");
                GameRoom room = (GameRoom) extras.getSerializable("room");
                rematch(roomId,room);
            }
        }

    }

    public void rematch(String roomId, GameRoom room) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                 gameRef.child(roomId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        GameRoom gameRoom = task.getResult().getValue(GameRoom.class);
                        if (gameRoom.getRoomState()== RoomState.REMATCH_WAITING){
                            joinRoom(roomId,gameRoom);
                        } else if (gameRoom.getRoomState()== RoomState.FINISHED) {
                            createRematch(roomId,gameRoom);
                        }
                    }
                });
            }
        });


    }

    private void findRoom() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                gameRef.limitToLast(5).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot data: task.getResult().getChildren()
                        ) {
                            String roomId = data.getKey();
                            GameRoom room = data.getValue(GameRoom.class);

                            if(room.getRoomState() == RoomState.WAITING){
                                joinRoom(roomId,room);
                                return;
                            }

                        }
                        createRoom();
                    }
                });
            }
        });
    }

    private void createRoom() {

        GameRoom room = new GameRoom();

        room.setRoomState(RoomState.WAITING);
        room.setPlayer1(playerName);
        room.setTurns(1);

        roomId = gameRef.push().getKey();
        gameRef.child(roomId).setValue(room.toInitialMap());
        gameRef.child(roomId).addValueEventListener(this);
    }

    private void joinRoom(String roomId,GameRoom room) {

        room.setPlayer2(playerName);
        gameRef.child(roomId).updateChildren(room.toInitialMap());

        showPlayerContainter(room);

        startGame(roomId,room);
    }

    private void createRematch(String roomId, GameRoom room) {
        room.setTurns(1);

        room.setPlayer1(playerName);
        room.setPlayer2(null);

        room.setRoomState(RoomState.REMATCH_WAITING);
        gameRef.child(roomId).updateChildren(room.toInitialMap());
        gameRef.child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GameRoom gameRoom= snapshot.getValue(GameRoom.class);
                if (gameRoom.getRoomState()== RoomState.REMATCH_WAITING&&gameRoom.getPlayer2()!=null){
                    gameRoom.setRoomState(RoomState.PLAYING);
                    gameRef.child(roomId).updateChildren(gameRoom.toInitialMap());
                    showPlayerContainter(gameRoom);
                    startGame(roomId,gameRoom);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                GameRoom gameRoom = snapshot.getValue(GameRoom.class);
                if (gameRoom.getPlayer2()!=null&& gameRoom.getRoomState() == RoomState.WAITING){
                    showPlayerContainter(gameRoom);

                    startGame(roomId,gameRoom);

                }
            }
        });


    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }

    public void startGame(String roomId,GameRoom gameRoom){
        intent = new Intent(this,OnlineGameActivity.class);

        intent.putExtra("roomId", roomId);
        intent.putExtra("GameRoom", gameRoom);
        startActivity(intent);
        gameRef.removeEventListener(this);
        finish();
    }

    public void showPlayerContainter(GameRoom gameRoom){
        runOnUiThread(() -> {

                player2.setText(gameRoom.getPlayer2());
                player1.setText(gameRoom.getPlayer1());
                playerContainer.setVisibility(View.VISIBLE);

        });
    }
}