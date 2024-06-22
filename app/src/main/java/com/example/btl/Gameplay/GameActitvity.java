package com.example.btl.Gameplay;



import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl.Fragment.BoardFragment;
import com.example.btl.R;

public class GameActitvity extends AppCompatActivity implements
        BoardGameAdapter.OnCellClickListener,Timer.TimerListener {

    private Game game;

    GridView gameBoardView;
    private Timer timer;
    TextView winnerText;

    TextView timerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_actitvity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        init();

    }

    public void init(){
        game = Game.getInstance();

        timer= Timer.getInstance();

        timerView= findViewById(R.id.timer);



        winnerText= findViewById(R.id.score_text);
//
//        BoardGameAdapter adapter= new BoardGameAdapter(this,game.getGameBoard(),this);
//        gameBoardView = findViewById(R.id.game_board);
//        gameBoardView.setAdapter(adapter);
//
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.board_container,new BoardFragment(this))
                .addToBackStack(null)
                .commit();


        timer.startTimer(30000,this);
        winnerText.setText("Lượt của người chơi 1");
    }

    @Override
    public void onCellClick(int position) {
        int player= game.getPlayer();
        if(game.checkWinner(position)){
            timer.cancelTimer();
            if(player==1){
                this.winnerText.setText("Player 2 win");
            }
            else this.winnerText.setText("Player 1 win");
        }
        else {
            timer.startTimer(10000,this);
            if (player==1){
                winnerText.setText("Lượt của người chơi 1");
            }
            else{
                winnerText.setText("Lượt của người chơi 2");
            }
        }
    }

    @Override
    public void onTick(long millisUntilFinished) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timerView.setText("0:"+ millisUntilFinished / 1000);
            }
        });
    }

    @Override
    public void onFinish() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(game.getPlayer()==1){
                    winnerText.setText("Player 2 win");
                }
                else {
                    winnerText.setText("Player 1 win");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        game.destroyInstance();
        timer.cancelTimer();
    }
}