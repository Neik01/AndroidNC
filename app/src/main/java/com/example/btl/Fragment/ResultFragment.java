package com.example.btl.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.btl.Gameplay.Gameplay;
import com.example.btl.Gameplay.Online.OnlineGameActivity;
import com.example.btl.MainActivity;
import com.example.btl.R;

public class ResultFragment extends Fragment implements View.OnTouchListener,View.OnClickListener {

    private static final String Game_TAG = "ResultGame";
    private TextView resultTextView;

    private ConstraintLayout rootLayout;
    private Gameplay gameplay;

    private Button rematchButton;

    private Button exitBtn;

    private String winnerString1;

    private String winnerString2;

    MediaPlayer mediaPlayer;

    public ResultFragment() {
        super(R.layout.result_fragment);
    }


    public static ResultFragment newInstance(Gameplay gameplay, String winnerString1, String winnerString2) {
        ResultFragment fragment = new ResultFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("game", gameplay);
        bundle.putString("winnerString1",winnerString1);
        bundle.putString("winnerString2",winnerString2);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.result_fragment,container,false);
        this.resultTextView=view.findViewById(R.id.winner_tv);

        this.gameplay = (Gameplay) getArguments().getSerializable("game");

        this.winnerString1 = getArguments().getString("winnerString1");
        this.winnerString2 = getArguments().getString("winnerString2");

        this.rootLayout = view.findViewById(R.id.result_root);

        rootLayout.setOnTouchListener(this);

        rematchButton = view.findViewById(R.id.rematch_btn);
        exitBtn = view.findViewById(R.id.result_exit_btn);

        rematchButton.setOnClickListener(this);
        exitBtn.setOnClickListener(this);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        boolean sound = sharedPreferences.getBoolean("sound",true);
        if(sound){
            mediaPlayer = MediaPlayer.create(this.getActivity(), R.raw.goodresult_82807);
            mediaPlayer.start();

        }


        if (gameplay.getPlayerTurn()==1)
            resultTextView.setText(winnerString1.toUpperCase());
        else
            resultTextView.setText(winnerString2.toUpperCase());
        return view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rematch_btn) {

            if(this.getActivity() instanceof OnlineGameActivity){
                Log.e("Result","From online activity");
            }
            this.getActivity().recreate();
            this.getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
        else if (id == R.id.result_exit_btn) {
            Intent intent = new Intent(this.getActivity(), MainActivity.class);
            startActivity(intent);
            this.getActivity().finish();
        }
    }
}
