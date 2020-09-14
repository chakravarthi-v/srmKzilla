package com.example.submit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class Image_selection extends AppCompatActivity {
    Uri MImage;
    ImageView picasa;
    String placeOn= UUID.randomUUID().toString()+".jpg";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String username;
    public void back(View view){
        finish();
    }

    //depending upon what user pressed
    //it takes an user to camera or gallery to upload image
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MImage=data.getData();
        if(requestCode==1&&resultCode== Activity.RESULT_OK){
            try{
                Bitmap pic=MediaStore.Images.Media.getBitmap(this.getContentResolver(),MImage);
                picasa.setImageBitmap(pic);

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else if(requestCode==2&&resultCode== Activity.RESULT_OK){
            try{
                Bitmap pic=(Bitmap) data.getExtras().get("data");
                picasa.setImageBitmap(pic);

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    //renders the current username from the database such tha
    public void userName(){
        FirebaseDatabase.getInstance().getReference("emails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               username= (String) snapshot.child(mAuth.getCurrentUser().getUid()).getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    //uploading the image to firebase storage as a bitmap and (url and name) to firebase database
    public void Upload(View view){
        picasa.setDrawingCacheEnabled(true);
        picasa.buildDrawingCache();
        Bitmap bitmap=picasa.getDrawingCache();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data =baos.toByteArray();
        UploadTask task= FirebaseStorage.getInstance().getReference().child("images").child(placeOn).putBytes(data);
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {@Override
                                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                      if (taskSnapshot.getMetadata() != null) {
                                          String url;
                                          //url=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                          if (taskSnapshot.getMetadata().getReference() != null) {
                                              Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                              result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                  @Override
                                                  public void onSuccess(Uri uri) {
                                                      String url = uri.toString();
                                                      Log.i("text",url);

                                                      FirebaseDatabase.getInstance().getReference("Images")
                                                              .child(mAuth.getCurrentUser().getUid()).child("pic").setValue(url);
                                                      FirebaseDatabase.getInstance().getReference("Images")
                                                              .child(mAuth.getCurrentUser().getUid()).child("name").setValue(username);

                                                  }
                                              });
                                          }

                                      }
                                  }
                                  }
        );

        Toast.makeText(Image_selection.this, "Uploaded", Toast.LENGTH_SHORT).show();
    }
     //gets image from gallery
    public void getPhoto(){
        Intent pico= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pico,1);
    }
    //requests permission from user
    public void gallery(View view){
        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }
        else{
            getPhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            getPhoto();
        }
        else if(requestCode==2){
            getImage();
        }
    }

    //gets image from camera
    public void getImage(){
        Intent cam = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cam,2);
    }
    // requesting permission and intent to camera
    public void camera(View view){
        if(checkSelfPermission(android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.CAMERA},2);
        }
        else {
            getImage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);
        picasa=findViewById(R.id.imageView3);
        userName();
    }
}