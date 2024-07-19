package com.example.btl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import com.example.btl.Fragment.ChooseModeFragment;
import com.example.btl.Fragment.SettingsFragment;
import com.example.btl.Gameplay.Offline2Player.Offline2PlayerActivity;
import com.example.btl.Gameplay.OfflineSinglePlayer.OfflineSinglePlayer;
import com.example.btl.Gameplay.Online.QuickPlayActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


     ImageButton settingsButton;

     Button buttonPlayOnline;

     Button buttonPlayOffline;

     Button buttonPlayBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        switchTheme();
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

        buttonPlayOnline =findViewById(R.id.button_play_online);
        buttonPlayOnline.setOnClickListener(this);

        buttonPlayOffline= findViewById(R.id.button_play_offline);
        buttonPlayOffline.setOnClickListener(this::onClick);

        buttonPlayBot= findViewById(R.id.button_play_bot);
        buttonPlayBot.setOnClickListener(this::onClick);
    }

    public void switchTheme() {
        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPref.getString("theme", "light");
        if (theme.equals("light")){
            setTheme(R.style.Theme_BTL);
        }
        else setTheme(R.style.Theme_BTL_Dark);
        Log.e("SetTheme from main",theme);
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
        if(id == R.id.button_play_online){

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new ChooseModeFragment())
                    .addToBackStack(null)
                    .commit();
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        }
        if (id == R.id.button_play_offline){
            Intent intent=new Intent(this, Offline2PlayerActivity.class);
            startActivity(intent);
        }
        if (id == R.id.button_play_bot){
            Intent intent=new Intent(this, OfflineSinglePlayer.class);
            startActivity(intent);
        }
    }
}