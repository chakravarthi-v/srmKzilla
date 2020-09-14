package com.example.submit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
//A beautiful ui made with different dependencies available in github, refer gradle for more info
public class MainActivity extends AppCompatActivity {
    EditText user,pass;
    Button butt;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();

    public void signUp(View view) {//Moves to Signup page
        Intent check=new Intent(MainActivity.this,signUP.class);
        startActivity(check);
    }
    public void keyboard(){
        InputMethodManager keys= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        keys.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
    public void LogIn(){//moves to feed
        Intent intent=new Intent(getApplication(),userfeed.class);
        startActivity(intent);
    }
    public void Login(View view){
        //first check's whether all the fields are entered or not
        if(user.getText().toString().isEmpty()||pass.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this,"All fields are required", Toast.LENGTH_SHORT).show();
        }
        //#FF9800
        //If yes then proceed to find the the email from the firebase database and then makes the user
        //to sigin to the feed
        //If there's a mistake a toast appears to the user saying that the given fields are wrong
        else {
            keyboard();
            FirebaseDatabase.getInstance().getReference("users")
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String c = (String) snapshot.child(user.getText().toString()).getValue();
                            try {
                                mAuth.signInWithEmailAndPassword(c, pass.getText().toString())
                                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                Toast.makeText(MainActivity.this, "signed in", Toast.LENGTH_SHORT).show();
                                                user.setText("");
                                                pass.setText("");
                                                LogIn();
                                            }
                                        });
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "check you username and password", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        user=findViewById(R.id.user);
        pass=findViewById(R.id.pass);
        butt=findViewById(R.id.butt1);
        //Even if the user exits the app before logging out
        //It checks out whether the previous user out logged out or not
        //if yes then it automatically takes the user to the feed page
        //if no then asks the uer to enter the details
        if(mAuth.getCurrentUser()!=null){
            LogIn();
        }

    }
}