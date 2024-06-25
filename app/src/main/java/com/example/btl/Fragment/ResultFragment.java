package com.example.btl.Fragment;

import android.content.Intent;
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

import com.example.btl.Gameplay.Game;
import com.example.btl.Gameplay.Offline2Player.Offline2PlayerActivity;
import com.example.btl.MainActivity;
import com.example.btl.R;

public class ResultFragment extends Fragment implements View.OnTouchListener,View.OnClickListener {

    private static final String Game_TAG = "ResultGame";
    private TextView resultTextView;

    private ConstraintLayout rootLayout;
    private Game game;

    private Button rematchButton;

    private Button exitBtn;

    public ResultFragment() {
        super(R.layout.result_fragment);
    }


    public static ResultFragment newInstance(Game game) {
        ResultFragment fragment = new ResultFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("game", game);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.result_fragment,container,false);
        this.resultTextView=view.findViewById(R.id.winner_tv);

        this.game = (Game) getArguments().getSerializable("game");
        this.rootLayout = view.findViewById(R.id.result_root);

        rootLayout.setOnTouchListener(this);

        rematchButton = view.findViewById(R.id.rematch_btn);
        exitBtn = view.findViewById(R.id.result_exit_btn);

        rematchButton.setOnClickListener(this);
        exitBtn.setOnClickListener(this);


        if (game.getPlayer()==1)
            resultTextView.setText("Người chơi 2 chiến thắng");
        else
            resultTextView.setText("Người chơi 1 chiến thắng");
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
