package com.example.btl.Gameplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.btl.R;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BoardGameAdapter extends ArrayAdapter<Integer> {

    private Game game;

    private  OnCellClickListener onCellClickListener;

    public interface OnCellClickListener{
        void onCellClick(int pos);
    }


    public BoardGameAdapter(@NonNull Context context, @NonNull int[][] objects
            ,OnCellClickListener onCellClickListener) {
        super(context, 0,  Arrays.stream(objects)  // Stream of Integer[]
                .flatMapToInt(Arrays::stream) //IntStream
                .boxed()// Stream of Integer
                .collect(Collectors.toList()));
        this.game=Game.getInstance();
        this.onCellClickListener=onCellClickListener;
    }


    @NonNull
    @Override
    public View getView(int pos, View convertView, ViewGroup parent){

        View gridView= convertView;

        if (gridView==null){
            gridView= LayoutInflater.from(getContext()).inflate(R.layout.game_cell,parent,false);
        }



        ImageView imageView = gridView.findViewById(R.id.iv_game_cell);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game.move(pos)) {
                    if (game.getPlayer()==1){
                        imageView.setImageResource(R.drawable.cross);
                        game.setPlayer(2);
                    }
                    else {
                        imageView.setImageResource(R.drawable.circle);
                        game.setPlayer(1);
                    }

                    onCellClickListener.onCellClick(pos);

                }
            }
        });


        return  gridView;
    }
}
