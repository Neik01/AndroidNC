package com.example.btl.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.btl.Gameplay.BoardGameAdapter;
import com.example.btl.Gameplay.Game;
import com.example.btl.R;

public class BoardFragment extends Fragment
        //implements BoardGameAdapter.OnCellClickListener
        {
    GridView gridView;

    Game game;

    BoardGameAdapter.OnCellClickListener onCellClickListener;
    public BoardFragment() {
        super(R.layout.game_board_fragment);
    }

    public BoardFragment(BoardGameAdapter.OnCellClickListener onCellClickListener) {
        super(R.layout.game_board_fragment);
        this.game=Game.getInstance();
        this.onCellClickListener = onCellClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view= inflater.inflate(R.layout.game_board_fragment,container,false);
        BoardGameAdapter adapter= new BoardGameAdapter(getContext(),game.getGameBoard(),onCellClickListener);
        gridView = view.findViewById(R.id.game_board_in_fragment);
        gridView.setAdapter(adapter);

        return view;

    }

//    @Override
//    public void onCellClick(int position) {
//
//        if(game.checkWinner(position)){
//            timer.cancelTimer();
//            if(game.getPlayer()==1){
//                this.winnerText.setText("Player 2 win");
//            }
//            else this.winnerText.setText("Player 1 win");
//        }
//        else timer.startTimer(10000,this);
//    }

}
