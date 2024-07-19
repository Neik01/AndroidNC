package com.example.btl.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.btl.Gameplay.Online.PrivateRoomActivity;
import com.example.btl.Gameplay.Online.Model.GameRoom;
import com.example.btl.Gameplay.Online.Model.RoomState;
import com.example.btl.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class EnterRoomIdFragment extends Fragment {



    EditText roomIdInput;

    Button confirmBtn;
    FirebaseDatabase database;
    DatabaseReference gameRef;

    TextView errorText;

    public EnterRoomIdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_room_id, container, false);

        ConstraintLayout rootLayout = view.findViewById(R.id.enter_id_root);
        rootLayout.setOnTouchListener((v, event) -> true);

        roomIdInput = view.findViewById(R.id.room_id_input);
        confirmBtn = view.findViewById(R.id.confirm_btn);
        errorText = view.findViewById(R.id.error_text);

        database = FirebaseDatabase.getInstance();
        gameRef = database.getReference("games");

        confirmBtn.setOnClickListener((v)->{
            String roomId = roomIdInput.getText().toString().trim();
            gameRef.get().addOnCompleteListener(task -> {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    GameRoom gameRoom = snapshot.getValue(GameRoom.class);
                    if (gameRoom.getRoomId()!=null){
                        if (gameRoom.getRoomId().equals(roomId)
                                &&gameRoom.getRoomState() == RoomState.PRIVATE_WAITING) {
                            joinRoom(gameRoom,snapshot.getKey());

                            return;
                        }
                    }

                }
                errorText.setText("Không tìm thấy phòng");
                errorText.setVisibility(View.VISIBLE);
            });
        });
        return view;
    }

    private void joinRoom(GameRoom gameRoom,String roomKey) {
       Intent intent = new Intent(getActivity(), PrivateRoomActivity.class);
       intent.putExtra("gameRoom", gameRoom);
       intent.putExtra("mode", "joinRoom");
       intent.putExtra("roomKey",roomKey);
       startActivity(intent);

    }
}