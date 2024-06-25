package com.example.btl.Gameplay.OfflineSinglePlayer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.btl.Gameplay.Game;
import com.example.btl.Gameplay.GameActitvity;
import com.example.btl.R;

import java.util.Random;

public class OfflineSinglePlayer extends GameActitvity {


    Game game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.game=Game.getInstance();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        GridView gridView = (GridView) parent;

        int player = game.getPlayer();

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_game_cell);

        if(game.move(position)){
            if (player==1){
                imageView.setImageResource(R.drawable.cross);
                game.setPlayer(2);

                gridView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int botMovePosition = botMove(position,1);
                        Long itemId = gridView.getAdapter().getItemId(botMovePosition);
                        ImageView botImageView = (ImageView) gridView.getChildAt(botMovePosition);
                        gridView.performItemClick(botImageView,botMovePosition,itemId);
                    }
                },3000);
            }
            else if (player==2){
                imageView.setImageResource(R.drawable.circle);
                game.setPlayer(1);
            }
        }

        super.checkWinner(position,player);

    }

    public int botMove(int playerMove, int step){

        int[] coord= game.findCoordinate(playerMove);
        int row = coord[0];
        int col = coord[1];

        Random random = new Random();
        int randomCol = col + getRandomMove(-step,step,random);
        int randomRow = row + getRandomMove(-step,step,random);

        int randomMovePosition = randomRow*15 + randomCol;

        if (game.getGameBoard()[randomRow][randomCol]==0){
            return randomMovePosition;
        }
        return  botMove(playerMove,step+1);
    }


    public int getRandomMove(int min,int max, Random random){
        return random.nextInt((max - min) + 1) + min;
    }
}
