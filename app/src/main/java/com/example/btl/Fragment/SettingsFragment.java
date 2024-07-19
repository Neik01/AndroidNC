package com.example.btl.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.btl.R;

public class SettingsFragment extends Fragment implements View.OnClickListener,View.OnTouchListener {


    Button closeBtn;

    SharedPreferences sharedPreferences;

    CheckBox soundCheckBox;

    EditText nameEditText;

    RadioGroup selectThemeRadioGroup;
    public SettingsFragment() {
        super(R.layout.settings_fragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.settings_fragment,container,false);

        ConstraintLayout rootLayout = view.findViewById(R.id.settings_root_layout);
        rootLayout.setOnTouchListener(this);

        closeBtn = view.findViewById(R.id.settings_close_btn);
        closeBtn.setOnClickListener(this);

        soundCheckBox = view.findViewById(R.id.settings_sound_checkbox);
        nameEditText = view.findViewById(R.id.settings_name_edittext);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        nameEditText.setText(sharedPreferences.getString("name","Nhập tên bạn"));
        soundCheckBox.setChecked(sharedPreferences.getBoolean("sound",true));

        selectThemeRadioGroup = view.findViewById(R.id.choose_theme_radiogroup);
        String theme = sharedPreferences.getString("theme","light");
        if(theme.equals("light")){
            selectThemeRadioGroup.check(R.id.radioLightMode);
        } else {
            selectThemeRadioGroup.check(R.id.radioDarkMode);

        }

        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.settings_close_btn){
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("name", nameEditText.getText().toString());
            editor.putBoolean("sound", soundCheckBox.isChecked());

            int themeMode = selectThemeRadioGroup.getCheckedRadioButtonId();
            if (themeMode == R.id.radioLightMode){
                this.getActivity().setTheme(R.style.Theme_BTL);
                this.getActivity().recreate();
            }
            else {
                this.getActivity().setTheme(R.style.Theme_BTL_Dark);
                this.getActivity().recreate();
            }
            editor.putString("theme", selectThemeRadioGroup.getCheckedRadioButtonId() == R.id.radioLightMode ? "light" : "dark");
            editor.apply();



            getActivity().getSupportFragmentManager().popBackStack();

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}