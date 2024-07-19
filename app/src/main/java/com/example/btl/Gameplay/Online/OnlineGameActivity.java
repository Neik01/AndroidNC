package com.example.btl.Gameplay.Online;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.example.btl.Fragment.NotifFragment;
import com.example.btl.Fragment.ResultFragment;
import com.example.btl.Gameplay.GameActitvity;
import com.example.btl.Gameplay.Gameplay;
import com.example.btl.Gameplay.Online.Model.GameRoom;
import com.example.btl.Gameplay.Online.Model.RoomState;
import com.example.btl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OnlineGameActivity extends GameActitvity implements ValueEventListener,OnCompleteListener<DataSnapshot>{

    FirebaseDatabase database;
    DatabaseReference gameRef;

    String roomId;
    GridView gameBoard;
    Gameplay gameplay;
    String playerName;

    GameRoom room;
    int playerTurn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Bundle info = getIntent().getExtras();

        if(info != null){
            roomId = info.getString("roomId");
            room = (GameRoom) info.getSerializable("GameRoom");


            super.setPlayer1Name(room.getPlayer1());
            super.setPlayer2Name(room.getPlayer2());
        }

        super.onCreate(savedInstanceState);


        database = FirebaseDatabase.getInstance();
        gameRef = database.getReference("games").child(roomId);
        gameBoard = super.getGameBoardView();

        this.gameplay = Gameplay.getInstance();

        room.setRoomState(RoomState.PLAYING);

        gameRef.updateChildren(room.toUpdateMap());
        gameRef.child("turns").addValueEventListener(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        playerName = sharedPreferences.getString("name", "Player");

        if(playerName.equals(room.getPlayer1()))
            playerTurn = 1;
        else
            playerTurn = 2;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (room.getTurns()== playerTurn){
                this.gameplay = Gameplay.getInstance();
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_game_cell);
                int player= this.gameplay.getPlayerTurn();

                if (this.gameplay.move(position)){
                    if (gameplay.getPlayerTurn() == playerTurn){
                        makeMove(position);
                    }
                    Log.e("OnlineGame","onItemClick"+position);
                    updateBoard(imageView,player);


                    if (!checkWinner(position)) {

                        gameplay.setPlayerTurn(player == 1 ? 2 : 1);

                        updateTurnView(gameplay.getPlayerTurn());
                    }
                    else {
                        room.setRoomState(RoomState.FINISHED);
                        gameRef.updateChildren(room.toUpdateMap());
                        gameRef.child("turns").removeEventListener(this);

                    }
                }}
                else  {
                    showFragment();

            }


    }

    private void showFragment() {
        hideFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in_bot, R.anim.slide_out_top);
        fragmentTransaction.replace(R.id.notif_fragment_container, new NotifFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        // Hide the fragment after 2 seconds using ExecutorService
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                // Post the UI operations back to the main thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        hideFragment();
                    }
                });
            }
        }, 1500, TimeUnit.MILLISECONDS);
    }

    public void hideFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_in_bot, R.anim.slide_out_top);
        Fragment fragment = fragmentManager.findFragmentById(R.id.notif_fragment_container);
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
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

        if(room.getTurns() == playerTurn){

            gameRef.child("moves").child(opponentPlayerKey)
                    .get().addOnCompleteListener(this);

        }

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

    @Override
    public Bundle setBundleForResultFragment(String winnerString1, String winnerString2) {
        Bundle bundle = new Bundle();
        bundle.putString("winnerString1", winnerString1);
        bundle.putString("winnerString2", winnerString2);
        bundle.putSerializable("game", gameplay);
        bundle.putSerializable("room", room);
        bundle.putString("roomId", roomId);
        return bundle;
    }


    @Override
    public boolean checkWinner(int position) {
        if(gameplay.checkWinner(position)){
            String winnerString1= super.player1Name+" chiến thắng";
            String winnerString2 = player2Name+" chiến thắng";
            Bundle bundle = setBundleForResultFragment(winnerString1,winnerString2);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.game_result_container, ResultFragment.newInstance(bundle))
                    .commit();

            timer.cancelTimer();
            this.gameplay.destroyInstance();
            return true;
        }
        return false;
    }

    @Override
    public void onFinish() {
        super.onFinish();
        room.setRoomState(RoomState.FINISHED);
        gameRef.updateChildren(room.toUpdateMap());
    }
}
