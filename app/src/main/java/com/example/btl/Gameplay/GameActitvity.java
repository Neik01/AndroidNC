package com.example.btl.Gameplay;



import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl.Fragment.ResultFragment;
import com.example.btl.R;

public class GameActitvity extends AppCompatActivity implements
        Timer.TimerListener, AdapterView.OnItemClickListener {

    private TextView testTV;

    private Game game;

    GridView gameBoardView;
    private Timer timer;
    TextView turnTextView;

    TextView timerView;

    BoardGameAdapter boardGameAdapter;

    private  String player1Name = "người chơi 1";

    private  String player2Name = "người chơi 2";

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

        turnTextView = findViewById(R.id.score_text);

        gameBoardView = findViewById(R.id.game_board);

        boardGameAdapter = new BoardGameAdapter(this,game.getGameBoard());

        gameBoardView.setAdapter(boardGameAdapter);

        gameBoardView.setOnItemClickListener(this::onItemClick);

        timer.startTimer(31000,this);

        turnTextView.setText("Lượt của "+ player1Name);
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
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.game_result_container,ResultFragment.newInstance(game))
                .commit();
        this.game.destroyInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        game.destroyInstance();
        timer.cancelTimer();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_game_cell);

        int player= game.getPlayer();

        if (game.move(position)) {
            if (player==1){
                imageView.setImageResource(R.drawable.cross);
                game.setPlayer(2);
            }
            else if (player==2){
                imageView.setImageResource(R.drawable.circle);
                game.setPlayer(1);
            }

        }

        checkWinner(position, player);
    }

    public void checkWinner(int position, int player) {
        if(game.checkWinner(position)){

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.game_result_container, ResultFragment.newInstance(game))
                    .commit();

            timer.cancelTimer();
            this.game.destroyInstance();

        }
        else {
            timer.startTimer(31000,this);
            if (player ==1){
                turnTextView.setText("Lượt của "+ player1Name);
            }
            else{
                turnTextView.setText("Lượt của "+ player2Name);
            }
        }
    }


}