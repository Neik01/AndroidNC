package com.example.btl.Gameplay.Online;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrivateRoomActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {

    FirebaseDatabase database;
    DatabaseReference gameRef;


    //room's key in database
    String roomKey;

    String playername;

    GameRoom gameRoom;

    TextView roomIdTv, notifTV,player1Tv,player2Tv;

    LinearLayout  roomIdContainer, playerContainer;
    Button cancelBtn,startButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        switchTheme();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_private_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        roomIdTv = findViewById(R.id.room_id);
        roomIdContainer = findViewById(R.id.room_id_container);
        cancelBtn = findViewById(R.id.cancel_button);
        notifTV = findViewById(R.id.ready_text);
        startButton = findViewById(R.id.start_button);
        player1Tv = findViewById(R.id.player1_name);
        player2Tv = findViewById(R.id.player2_name);
        playerContainer = findViewById(R.id.player_container);

        startButton.setOnClickListener(this);
        cancelBtn.setOnClickListener(v -> {
            finish();

            this.gameRef.child(this.roomKey).removeEventListener(this);
            Map<String,Object> map = new HashMap<>();
            map.put("roomState", RoomState.CANCELLED);
            gameRef.child(roomKey).updateChildren(map);
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        playername = sharedPreferences.getString("name", "Player");

        database = FirebaseDatabase.getInstance();
        gameRef = database.getReference("games");


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String mode = extras.getString("mode");
            if (mode != null) {
                if (mode.equals("createRoom")){
                    createRoom();
                }
                else if (mode.equals("joinRoom")){
                    gameRoom = (GameRoom) extras.getSerializable("gameRoom");
                    roomKey = extras.getString("roomKey");
                    joinRoom();
                }
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

    public void createRoom(){
        roomKey = gameRef.push().getKey();
        gameRoom = new GameRoom();
        gameRoom.setPlayer1(playername);
        gameRoom.setPlayer2(null);
        gameRoom.setRoomState(RoomState.PRIVATE_WAITING);
        gameRoom.setTurns(1);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> gameRef.limitToLast(1).get().addOnCompleteListener(task -> {

            for (DataSnapshot snapshot : task.getResult().getChildren()) {
                GameRoom lastGameRoom = snapshot.getValue(GameRoom.class);
                Log.e("RoomKey", lastGameRoom.toString());
                if(lastGameRoom.getRoomId() != null){
                    gameRoom.setRoomId(gameRoom.createId(lastGameRoom.getRoomId()));
                }else{
                    gameRoom.setRoomId(gameRoom.createId("0"));
                }
                gameRef.child(roomKey).setValue(gameRoom.toInitialMap());
            }

            runOnUiThread(() -> {
                roomIdTv.setText(gameRoom.getRoomId());
                roomIdContainer.setVisibility(View.VISIBLE);
                notifTV.setText("Đang đợi đối thủ");
            });

            gameRef.child(roomKey).addValueEventListener(this);
        }));


    }

    public void joinRoom() {
        this.gameRoom.setPlayer2(playername);
        this.gameRoom.setTurns(1);

        gameRef.child(this.roomKey).updateChildren(gameRoom.toUpdateMap());
        showPlayerContainer(gameRoom);

        roomIdTv.setText(gameRoom.getRoomId());
        roomIdContainer.setVisibility(View.VISIBLE);
        gameRef.child(this.roomKey).addValueEventListener(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        Log.e("PrivateRoomActivity", snapshot.toString());
        GameRoom gameRoom1 = snapshot.getValue(GameRoom.class);
        if (gameRoom1.getPlayer2()!=null){
            this.gameRoom.setPlayer2(gameRoom1.getPlayer2()) ;
            notifTV.setText("");
            showPlayerContainer(gameRoom1);
        }

        if (gameRoom1.getRoomState()== RoomState.PLAYING){
            startGame();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.start_button){
            startGame();
        }
    }

    private void startGame() {

        if (gameRoom.getPlayer1()!=null && gameRoom.getPlayer2()!=null){
            Intent intent = new Intent(this,OnlineGameActivity.class);
            intent.putExtra("roomId",roomKey);
            intent.putExtra("GameRoom",gameRoom);
            startActivity(intent);
            this.gameRef.child(this.roomKey).removeEventListener(this);
            this.finish();
        }

    }

    public void showPlayerContainer(GameRoom gameRoom){
        runOnUiThread(()->{
            player1Tv.setText(gameRoom.getPlayer1());
            player2Tv.setText(gameRoom.getPlayer2());
            playerContainer.setVisibility(View.VISIBLE);

        });
    }
}