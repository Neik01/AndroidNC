package com.example.btl.Gameplay.Online;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import com.example.btl.Gameplay.Online.Model.GameRoom;
import com.example.btl.Gameplay.Online.Model.RoomState;
import com.example.btl.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuickPlayActivity extends AppCompatActivity implements ValueEventListener, View.OnClickListener {

    FirebaseDatabase database;
    DatabaseReference gameRef;

    String playerName;
    String roomId;

    TextView readyText,player1,player2;

    LinearLayout playerContainer;

    Intent intent;

    Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        switchTheme();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quick_play);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cancelBtn = findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(this);

        readyText = findViewById(R.id.ready_text);

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
                readyText.setText("Đang tìm đối thủ");
                findRoom();
            } else if (origin.equals("ResultFragment")) {
                readyText.setText("Đang đợi đối thủ");
                roomId = extras.getString("roomId");
                GameRoom room = (GameRoom) extras.getSerializable("room");
                rematch(roomId,room);
            }
        }

    }

    public void switchTheme() {
        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPref.getString("theme", "light");
        if (theme.equals("light")){
            setTheme(R.style.Theme_BTL);
        }
        else setTheme(R.style.Theme_BTL_Dark);
        Log.e("SetTheme from main",theme);
    }
    public void rematch(String roomId, GameRoom room) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> gameRef.child(roomId).get().addOnCompleteListener(task -> {
            GameRoom gameRoom = task.getResult().getValue(GameRoom.class);
            if (gameRoom.getRoomState()== RoomState.REMATCH_WAITING){
                joinRoom(roomId,gameRoom);
            } else if (gameRoom.getRoomState()== RoomState.FINISHED) {
                createRematch(roomId,gameRoom);
            } else if (gameRoom.getRoomState() == RoomState.CANCELLED) {
                Toast.makeText(this,"Đối phương đã thoát khỏi phòng",Toast.LENGTH_SHORT).show();
                finish();
            }
        }));


    }

    private void findRoom() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> gameRef.limitToLast(5).get().addOnCompleteListener(task -> {
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
        }));
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
        overridePendingTransition(R.anim.zoom_in,R.anim.static_animation);

        gameRef.removeEventListener(this);
        finish();
    }

    public void showPlayerContainter(GameRoom gameRoom){
        runOnUiThread(() -> {

                player2.setText(gameRoom.getPlayer2());
                player1.setText(gameRoom.getPlayer1());
                playerContainer.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.GONE);

        });
    }

    @Override
    public void onClick(View v) {
        if(roomId!=null){
            gameRef.child(roomId).child("roomState").setValue(RoomState.CANCELLED);
            gameRef.child(roomId).removeEventListener(this);
            this.finish();
        }
    }
}