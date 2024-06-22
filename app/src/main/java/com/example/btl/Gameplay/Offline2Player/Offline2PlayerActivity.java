package com.example.btl.Gameplay.Offline2Player;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl.Fragment.BoardFragment;
import com.example.btl.Gameplay.BoardGameAdapter;
import com.example.btl.R;

public class Offline2PlayerActivity extends AppCompatActivity implements BoardGameAdapter.OnCellClickListener {


    private TextView testTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_offline2_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        testTV=findViewById(R.id.test);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.offline_board_container,new BoardFragment(this))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCellClick(int pos) {
        testTV.setText(String.valueOf(pos));
    }
}