package com.adityagunjal.sdl_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.adityagunjal.sdl_project.helpers.UserInfo;
import com.adityagunjal.sdl_project.models.ModelQuestion;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class SplashActivity extends AppCompatActivity {

    public static UserInfo userInfo = new UserInfo();
    public static boolean isAlreadyStarted = false;

    public static ValueEventListener valueEventListener;
    public static DatabaseReference databaseReference;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = this;

        Intent i = getIntent();
        int flag = i.getIntExtra("EXTRA_FLAG", 0);

        ImageView imageView = findViewById(R.id.background_loading_image);
        VideoView loadingVideo = findViewById(R.id.loading_video);

        if(flag == 1){
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.logo2);

            loadingVideo.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            loadingVideo.setVideoURI(uri);
            loadingVideo.start();
            loadingVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    SplashActivity.initApp(context, SplashActivity.this);
                }
            });
        }else{
            DrawableImageViewTarget drawableImageViewTarget = new DrawableImageViewTarget(imageView);
            Glide.with(this).load(R.drawable.logogif2).into(drawableImageViewTarget);
            SplashActivity.initApp(context, SplashActivity.this);
        }
    }

    public static void initApp(final Context context, final Activity activity){
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);

                if(!modelUser.getImagePath().equals("default")){
                    FirebaseStorage.getInstance().getReference(modelUser.getImagePath())
                            .getBytes(1024 * 1024)
                            .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                @Override
                                public void onComplete(@NonNull Task<byte[]> task) {
                                    if(task.isSuccessful()){
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                        userInfo.setData(userID, modelUser, bitmap);

                                        if(!isAlreadyStarted){
                                            context.startActivity(new Intent(context, MainActivity.class));
                                            activity.finish();
                                            isAlreadyStarted = true;
                                        }
                                    }else{
                                        Toast.makeText(context, "Failed to get Data !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    userInfo.setData(userID, modelUser, null);
                    if(!isAlreadyStarted){
                        context.startActivity(new Intent(context, MainActivity.class));
                        activity.finish();
                        isAlreadyStarted = true;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);
    }

}
