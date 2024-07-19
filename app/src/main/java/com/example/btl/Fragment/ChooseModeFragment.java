package com.example.btl.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.btl.Gameplay.Online.PrivateRoomActivity;
import com.example.btl.Gameplay.Online.QuickPlayActivity;
import com.example.btl.R;


public class ChooseModeFragment extends Fragment implements View.OnClickListener {

    public ChooseModeFragment() {
        // Required empty public constructor
    }


    public static ChooseModeFragment newInstance(String param1, String param2) {
        ChooseModeFragment fragment = new ChooseModeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_mode, container, false);
        ConstraintLayout root=view.findViewById(R.id.choose_mode_root);

        Button quickPlayBtn = view.findViewById(R.id.quickplay_btn);

        Button createRoomBtn = view.findViewById(R.id.create_room_btn);

        Button joinRoomBtn = view.findViewById(R.id.join_room_btn);

        quickPlayBtn.setOnClickListener(this);
        createRoomBtn.setOnClickListener(this);
        joinRoomBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.quickplay_btn){
            Intent intent = new Intent(this.getActivity(), QuickPlayActivity.class);
            intent.putExtra("origin","MainActivity");
            startActivity(intent);
        }else if(id == R.id.create_room_btn){
            Intent intent = new Intent(this.getActivity(), PrivateRoomActivity.class);
            intent.putExtra("mode","createRoom");
            startActivity(intent);
        }else if(id == R.id.join_room_btn){
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new EnterRoomIdFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
}