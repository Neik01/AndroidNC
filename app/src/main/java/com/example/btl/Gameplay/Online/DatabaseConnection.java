package com.example.btl.Gameplay.Online;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseConnection {

    FirebaseDatabase database;

    DatabaseReference gameRef;

    String gameRoomId;

    public DatabaseConnection() {

    }

    public DatabaseConnection(String gameRoomId) {
     database = FirebaseDatabase.getInstance();
     this.gameRoomId =gameRoomId;
     gameRef = database.getReference("games").child(gameRoomId);
    }
}
