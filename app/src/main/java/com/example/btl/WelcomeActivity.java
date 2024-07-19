package com.example.btl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (prefs.getBoolean("first_run", true)) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_welcome);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            EditText username = findViewById(R.id.name_et);
            Button okBtn = findViewById(R.id.ok_btn);
            okBtn.setOnClickListener(v -> {
                SharedPreferences.Editor editor = prefs.edit();

                String name = username.getText().toString().trim();
                if (name.isEmpty()) {

                    username.setHint("Nhập tên của bạn ");
                    username.setHintTextColor(ContextCompat.getColor(this, R.color.light_red));
                    username.setGravity(Gravity.CENTER);

                }
                else {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    editor.putBoolean("first_run", false);
                    editor.putString("name", username.getText().toString());

                    editor.apply();
                    startActivity(intent);
                    finish();
                }

            });

        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}