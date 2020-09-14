package com.example.submit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class userfeed extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ListView listView;
    ImageView heart;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> arrayList2 = new ArrayList<>();
    TextView change;
    Toolbar toool;
    DrawerLayout draw;
    NavigationView nav;
    String placeOn= UUID.randomUUID().toString()+".jpg";
    Uri MImage;
    String s;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    public class customList extends ArrayAdapter<String> {
        Activity context;
        ArrayList<String> names;
        ArrayList<String> users;
        //A custom listview for the page to scroll and it automatically renders the image and the user who
        //have uploaded the image and also you can support the user by liking the image
        public customList(Activity context,ArrayList<String> names,ArrayList<String> users){
            super(context, R.layout.listview,names);
            this.context=context;
            this.names=names;
            this.users=users;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflate=context.getLayoutInflater();
            View row=inflate.inflate(R.layout.listview,null,true);
            LinearLayout lin=row.findViewById(R.id.lin);
            LinearLayout lin2=row.findViewById(R.id.lin2);
            TextView t1 =lin.findViewById(R.id.textView32);
            ImageView imageView = lin2.findViewById(R.id.imageView4);
            final ImageView imageViews = row.findViewById(R.id.imageView5);
            imageViews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //On clicking on the heart the image change
                    imageViews.setImageResource(R.drawable.heart64);
                }
            });
            t1.setText(users.get(position));
            //call's the download image and sets the imageview in userfeed
            ImageDownlaoder task=new ImageDownlaoder();
            Bitmap myImage;
            try{
                String ur=names.get(position);
                myImage=task.execute(ur).get();
                imageView.setImageBitmap(myImage);
            }catch(Exception e){
                e.printStackTrace();
            }
            imageViews.setImageResource(R.drawable.heart);
            return row;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userfeed);
        change = findViewById(R.id.textView34);
        mAuth.getCurrentUser();
        heart=findViewById(R.id.imageView5);
        //navigation view check the dependencies

        draw = findViewById(R.id.drawer);
        nav = findViewById(R.id.navview);
        toool = (Toolbar) findViewById(R.id.tool);
        setSupportActionBar(toool);
        ActionBarDrawerToggle bar = new ActionBarDrawerToggle
                (userfeed.this, draw, toool, R.string.openNavDrawer, R.string.closeNavDrawer);
        draw.addDrawerListener(bar);
        bar.syncState();
        nav.setNavigationItemSelectedListener(this);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Images");
        listView = (ListView) findViewById(R.id.lists);
        arrayList=new ArrayList<>();
        arrayList2=new ArrayList<>();
        //rendering of nmae and the Image from firebase database and storing it into the arraylist
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value=snapshot.child("pic").getValue(String.class);
                String value2=snapshot.child("name").getValue(String.class);
                arrayList.add(value);
                arrayList2.add(value2);
                customList pokemon=new customList(userfeed.this,arrayList,arrayList2);
                listView.setAdapter(pokemon);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    //on selecting the given option given inthe navigation view it takes the user to respective activity
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //takes the user to setting activity
        if(item.getItemId()==R.id.nav_message){
            Intent sett=new Intent(userfeed.this,setting_page.class);
            startActivity(sett);
        }
        //takes the user to upload activity
        else if(item.getItemId()==R.id.image){
            Intent page=new Intent(getApplicationContext(),Image_selection.class);
            startActivity(page);
        }
        //log's out the user from the feed
        else if(item.getItemId()==R.id.out){
            mAuth.signOut();
            finish();
        }
        return false;
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MImage=data.getData();
        if(requestCode==1&&resultCode== Activity.RESULT_OK){
            try{
                UploadTask task= FirebaseStorage.getInstance().getReference().child("images").child(placeOn).putFile(MImage);
                task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri url;
                        Task<Uri> uri=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isComplete());
                        url = uri.getResult();
                    }
                });
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }*/
    //download the image from firebase which is given in url form
    public class ImageDownlaoder extends AsyncTask<String,Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try{
                URL url=new URL(strings[0]);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in=connection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(in);
                return myBitmap;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }
}