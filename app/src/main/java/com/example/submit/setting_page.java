package com.example.submit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class setting_page extends AppCompatActivity {
    TextView uuid,email,username;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    String c;
    public void back(View view){
        finish();
    }
    //This page renders the user uid,email and username from firebase database
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);
        uuid=findViewById(R.id.textView9);
        email=findViewById(R.id.textView);
        username=findViewById(R.id.textView69);
        uuid.setText("User ID:"+mAuth.getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference("emails")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        c = (String) snapshot.child(mAuth.getCurrentUser().getUid()).getValue();
                        email.setText("Username:"+c);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String d = (String) snapshot.child(c).getValue();
                        username.setText("Email:"+d);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}