package com.example.btl.Gameplay;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.btl.R;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BoardGameAdapter extends ArrayAdapter<Integer> {

    public BoardGameAdapter(@NonNull Context context, @NonNull int[][] objects) {
        super(context, 0,  Arrays.stream(objects)  // Stream of Integer[]
                .flatMapToInt(Arrays::stream) //IntStream
                .boxed()// Stream of Integer
                .collect(Collectors.toList()));

    }

    @NonNull
    @Override
    public View getView(int pos, View convertView, ViewGroup parent){

        View gridView= convertView;

        if (gridView==null){
            gridView= LayoutInflater.from(getContext()).inflate(R.layout.game_cell,parent,false);
        }

        return gridView;
    }

}
