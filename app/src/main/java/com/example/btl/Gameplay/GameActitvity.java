package com.example.btl.Gameplay;



import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import androidx.preference.PreferenceManager;

import com.example.btl.Fragment.ResultFragment;
import com.example.btl.R;

public class GameActitvity extends AppCompatActivity implements
        Timer.TimerListener, AdapterView.OnItemClickListener {

    private Gameplay gameplay;

    GridView gameBoardView;
    protected Timer timer;
    TextView turnTextView;

    TextView timerView;

    BoardGameAdapter boardGameAdapter;

    protected String player1Name;
    protected String player2Name;

    private TextView player1NameView;

    private TextView player2NameView;

    private boolean isSoundOn;

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
        gameplay = Gameplay.getInstance();

        timer= Timer.getInstance();

        timerView= findViewById(R.id.timer);

        turnTextView = findViewById(R.id.score_text);

        player1NameView = findViewById(R.id.player1_label);

        player2NameView = findViewById(R.id.player2_label);

        player1NameView.setText(player1Name.substring(0,1).toUpperCase()+player1Name.substring(1));

        player2NameView.setText(player2Name.substring(0,1).toUpperCase()+player2Name.substring(1));

        gameBoardView = findViewById(R.id.game_board);

        boardGameAdapter = new BoardGameAdapter(this, gameplay.getGameBoard());

        gameBoardView.setAdapter(boardGameAdapter);

        gameBoardView.setOnItemClickListener(this::onItemClick);

//        timer.startTimer(31000,this);

        turnTextView.setText("Lượt của "+ player1Name);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isSoundOn = sharedPreferences.getBoolean("sound",true);

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
        String winnerString1= player2Name+" chiến thắng";
        String winnerString2 = player1Name+" chiến thắng";
        Bundle bundle = setBundleForResultFragment(winnerString1,winnerString2);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.game_result_container,ResultFragment.newInstance(bundle))
                .commit();
        this.gameplay.destroyInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameplay.destroyInstance();
        timer.cancelTimer();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_game_cell);

        int player = gameplay.getPlayerTurn();

        if (gameplay.move(position)){

            updateBoard(imageView,player);

            if (!checkWinner(position)) {

                gameplay.setPlayerTurn(player == 1 ? 2 : 1);

                updateTurnView(gameplay.getPlayerTurn());
            }

        }

    }

    public boolean checkWinner(int position) {
        if(gameplay.checkWinner(position)){
            String winnerString1= player1Name+" chiến thắng";
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

    public void updateBoard(ImageView imageView,int player){
        isSoundOn = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sound",true);
        if (isSoundOn){
            final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.move_self);
            mediaPlayer.start();
        }

        runOnUiThread(() -> {
            if (player==1){
                imageView.setImageResource(R.drawable.cross);
            }
            else if (player==2){
                imageView.setImageResource(R.drawable.circle);
            }
        });

    }

    public void updateTurnView(int player){
//        timer.startTimer(31000,this);
        runOnUiThread(() -> {
            if (player ==1){
                turnTextView.setText("Lượt của "+ player1Name);
            }
            else{
                turnTextView.setText("Lượt của "+ player2Name);
            }
        });

    }

    public Bundle setBundleForResultFragment(String winnerString1,String winnerString2){
        Bundle bundle = new Bundle();
        bundle.putSerializable("game",gameplay);
        bundle.putString("winnerString1",winnerString1);
        bundle.putString("winnerString2",winnerString2);

        return bundle;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public GridView getGameBoardView() {
        return gameBoardView;
    }
}