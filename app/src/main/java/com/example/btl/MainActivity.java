package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl.Gameplay.GameActitvity;
import com.example.btl.Fragment.SettingsFragment;
import com.example.btl.Gameplay.Offline2Player.Offline2PlayerActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


     ImageButton settingsButton;

     Button button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(this);

        button1=findViewById(R.id.button_play);
        button1.setOnClickListener(this);
        Button button2= findViewById(R.id.button_play2);
        button2.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {

        int id= v.getId();
        if (id == R.id.settings_button) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new SettingsFragment())
                    .addToBackStack(null)
                    .commit();

            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        }
        if(id == R.id.button_play){
            Intent intent = new Intent(this, GameActitvity.class);
            startActivity(intent);
        }
        if (id == R.id.button_play2){
            Intent intent=new Intent(this, Offline2PlayerActivity.class);
            startActivity(intent);
        }
    }
}