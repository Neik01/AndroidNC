package com.example.btl.Gameplay.OfflineSinglePlayer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.btl.Gameplay.Gameplay;
import com.example.btl.Gameplay.GameActitvity;
import com.example.btl.R;

import java.util.Random;

public class OfflineSinglePlayer extends GameActitvity {


    Gameplay gameplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setPlayer1Name("bạn");
        super.setPlayer2Name("máy");
        super.onCreate(savedInstanceState);
        this.gameplay = Gameplay.getInstance();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_game_cell);

        GridView gridView =  (GridView) parent;

        int player= gameplay.getPlayerTurn();

        if (gameplay.move(position)){

            super.updateBoard(imageView,player);

            if (!checkWinner(position)) {

                gameplay.setPlayerTurn(player == 1 ? 2 : 1);

                super.updateTurnView(gameplay.getPlayerTurn());

                if (gameplay.getPlayerTurn() == 2)
                    performBotMove(gridView,position);
            }

        }

    }

    public void performBotMove(GridView gridView,int position){
        gridView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int move = botMove(position,1);
                ImageView imageView = (ImageView) gridView.getChildAt(move);
                long itemId = gridView.getAdapter().getItemId(move);
                gridView.performItemClick(imageView, move, itemId);

            }
        },1000);
    }

    public int botMove(int playerMove, int step){

        int[] coord= gameplay.findCoordinate(playerMove);
        int row = coord[0];
        int col = coord[1];

        Random random = new Random();
        int randomCol = col + getRandom(-step,step,random);
        int randomRow = row + getRandom(-step,step,random);

        int randomMovePosition = randomRow*15 + randomCol;

        if (gameplay.getGameBoard()[randomRow][randomCol]==0){
            return randomMovePosition;
        }
        return  botMove(playerMove,step+1);
    }


    public int getRandom(int min, int max, Random random){
        return random.nextInt((max - min) + 1) + min;
    }


}
