package com.example.btl.Gameplay.Offline2Player;

import android.os.Bundle;

import com.example.btl.Gameplay.GameActitvity;

public class Offline2PlayerActivity extends GameActitvity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.setPlayer1Name("người chơi 1");
        super.setPlayer2Name("người chơi 2");
        super.onCreate(savedInstanceState);
    }

}