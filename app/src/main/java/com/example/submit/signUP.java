package com.example.submit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class signUP extends AppCompatActivity {
    EditText user,eMail,pass;
    Button butt;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public void keyboard(){
        InputMethodManager keys= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        keys.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
    public void back(View view){
        finish();
    }
    //checks whether all the fields are present or not
    public void SignUP(View view) {
        if(user.getText().toString().isEmpty()||pass.getText().toString().isEmpty()||eMail.getText().toString().isEmpty()) {
            Toast.makeText(signUP.this,"All fields are required", Toast.LENGTH_SHORT).show();
            user.setText("");
            pass.setText("");
            eMail.setText("");
        }
        //If yes authentication takes place and it updates the firebase database with username and password
        //If the user enter the same username which is present beforehand in the database by another user
        //It insists the new user to choose a new username
        else{
            keyboard();
            try {
                FirebaseDatabase.getInstance().getReference("users")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String c = (String) snapshot.child(user.getText().toString()).getValue();
                                if (c == null) {
                                    mAuth.createUserWithEmailAndPassword(eMail.getText().toString(), pass.getText().toString())
                                            .addOnCompleteListener(signUP.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseDatabase.getInstance().getReference("users")
                                                                .child(user.getText().toString()).setValue(eMail.getText().toString());
                                                        FirebaseDatabase.getInstance().getReference("emails")
                                                                .child(mAuth.getCurrentUser().getUid()).setValue(user.getText().toString());
                                                        Toast.makeText(signUP.this, "successfully signed up", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(signUP.this, "E-mail already exists", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                } else {
                                    Toast.makeText(signUP.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
            } catch (Exception e) {
                Toast.makeText(signUP.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_u_p);
        user=findViewById(R.id.user);
        eMail=findViewById(R.id.eMail);
        pass=findViewById(R.id.pass);
        butt=findViewById(R.id.go);

    }
}