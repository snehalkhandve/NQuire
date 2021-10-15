package com.adityagunjal.sdl_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adityagunjal.sdl_project.helpers.Helpers;
import com.adityagunjal.sdl_project.models.ModelAnswer;
import com.adityagunjal.sdl_project.models.ModelDraft;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.Index;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class AnswerQuestionActivity extends AppCompatActivity {

    String questionID, questionText, userID, username,draftId;
    ImageView imageView;
    LinearLayout answerLinearLayout;
    EditText editText;
    String question;

    ProgressDialog progressDialog;

    HashMap<String, String> answer = new HashMap<>();
    HashMap<String,String> draft = new HashMap<>();
    LinkedHashMap<Integer, String> sortedDraftMap = new LinkedHashMap<>();
    ArrayList<Integer> sortedKeys = new ArrayList<>();
    int currentIndex = -1;
    int totalViews = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);
        answerLinearLayout = findViewById(R.id.answer_linear_layout);
        final TextView questionTextView = findViewById(R.id.question_text);
        editText = findViewById(R.id.edit_text1);
        editText.setId(0);
        currentIndex = 0;
        totalViews = 1;

        Intent i = getIntent();
       int flag = i.getIntExtra("EXTRA_FLAG",0);

       if(flag == 1)
       {

          draftId =  i.getStringExtra("EXTRA_DRAFT_ID");
          questionID = i.getStringExtra("EXTRA_QUESTION_ID");
          userID = i.getStringExtra("EXTRA_USER_ID");
          draft = (HashMap<String, String>) i.getSerializableExtra("EXTRA_DRAFT_ANSWER");
           answerLinearLayout.removeView(editText);
           Toast.makeText(this, "dd"+draftId, Toast.LENGTH_SHORT).show();
           FirebaseDatabase.getInstance().getReference("questions/"+questionID+"/text").addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   question = dataSnapshot.getValue(String.class);
                   questionTextView.setText(question);

               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                   Toast.makeText(AnswerQuestionActivity.this, "Cannot Retrieve The Question", Toast.LENGTH_SHORT).show();

               }
           });



          /* questionTextView.setTextColor(getResources().getColor(R.color.black));
           questionTextView.setTextSize(17);*/
           for(String key : draft.keySet()){
               sortedKeys.add(Integer.parseInt(key.substring(1)));
           }

           Collections.sort(sortedKeys);

           for(int i1 : sortedKeys){
               sortedDraftMap.put(i1, draft.get("k" + Integer.toString(i1)));
           }

           float factor =  this.getResources().getDisplayMetrics().density;
           Iterator draftIterator = sortedDraftMap.entrySet().iterator();
           while(draftIterator.hasNext()){
               Map.Entry<Integer, String> draftElement = (Map.Entry) draftIterator.next();
               int key = draftElement.getKey();

               if(key % 2 == 0){
                   EditText editText = new EditText(answerLinearLayout.getContext());
                   editText.setText(draftElement.getValue());
                   editText.setTextColor(getResources().getColor(R.color.black));
                   editText.setTextSize(17);
                   editText.setBackground(null);
                   answerLinearLayout.addView(editText);

               }else{

                   final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                   layoutParams.topMargin = (int)(factor * 7);
                   layoutParams.bottomMargin = (int)(factor * 7);
                   final ImageView imageView = new ImageView(answerLinearLayout.getContext());
                   imageView.setLayoutParams(layoutParams);
                   answerLinearLayout.addView(imageView);
                   imageView.setLongClickable(true);
                   imageView.setOnLongClickListener(new View.OnLongClickListener() {
                       @Override
                       public boolean onLongClick(final View v) {
                           Toast.makeText(AnswerQuestionActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                           AlertDialog.Builder builder = new AlertDialog.Builder(AnswerQuestionActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert);


                           builder.setPositiveButton("Remove Image", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   StorageReference storageReference;
                                   storageReference = FirebaseStorage.getInstance().getReference(v.getTag().toString());
                                   storageReference.delete();
                                   answerLinearLayout.removeView(imageView);
                               }
                           });
                           AlertDialog rem = builder.create();
                           rem.show();

                           return true;
                       }
                   });

                   FirebaseStorage.getInstance().getReference(draftElement.getValue())
                           .getBytes(1024 * 1024)
                           .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                               @Override
                               public void onComplete(@NonNull Task<byte[]> task) {
                                   if(task.isSuccessful()){
                                       imageView.setImageBitmap(BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length));
                                   }
                               }
                           });
               }
           }
           currentIndex = draft.size();
       }
       else {
           questionID = i.getStringExtra("EXTRA_QUESTION_ID");
           questionText = i.getStringExtra("EXTRA_QUESTION_TEXT");
           userID = i.getStringExtra("EXTRA_USER_ID");
           username = i.getStringExtra("EXTRA_USERNAME");
           questionTextView.setText(questionText);
       }







    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AnswerQuestionActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure, you want to leave?");

        builder.setPositiveButton("Save As Draft", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                saveAsDraft();
                dialog.dismiss();
                AnswerQuestionActivity.this.finish();
            }
        });

        builder.setNegativeButton("Clear", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


                AnswerQuestionActivity.this.finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onBackPressed(View view) {
       saveDraftConfirm();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap selectedImage = null;
        if (resultCode == RESULT_OK) {
            Uri returnUri = data.getData();
            try{
                //selectedImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), returnUri);
                selectedImage = checkForRoatatedImage(getApplicationContext(), returnUri);

            }catch (Exception e){
                Toast.makeText(AnswerQuestionActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                return;
            }

            float factor = imageView.getContext().getResources().getDisplayMetrics().density;

            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            imageParams.gravity = Gravity.CENTER;
            imageParams.topMargin = (int)(factor * 10);
            imageParams.bottomMargin = (int) (factor * 10);
            imageView.setLayoutParams(imageParams);
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final String imageID = UUID.randomUUID().toString();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            byte[] byteData = byteArrayOutputStream.toByteArray();
            final Context context = getApplicationContext();
            final Bitmap finalBitmapImage = selectedImage;

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading Image ...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            FirebaseStorage.getInstance().getReference("images/answers/" + userID + "/" + imageID)
                    .putBytes(byteData)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageView.setImageBitmap(finalBitmapImage);
                            imageView.setTag("images/answers/" + userID + "/" + imageID);
                            answerLinearLayout.addView(imageView, currentIndex + 1);


                               if(editText.getText().toString().equals("")){
                                   answerLinearLayout.removeView(editText);
                                   currentIndex--;}


                           // Toast.makeText(context, "current index:"+currentIndex+" cc"+answerLinearLayout.getChildCount(), Toast.LENGTH_SHORT).show();
                            if(currentIndex + 1 == answerLinearLayout.getChildCount() - 1){
                                editText = new EditText(context);
                                float factor = editText.getContext().getResources().getDisplayMetrics().density;
                                LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                editTextParams.topMargin = (int)(factor * 10);
                                editTextParams.leftMargin = (int)(factor * 15);
                                editTextParams.rightMargin = (int)(factor * 15);
                                editText.setPadding(0, (int)(factor * 10), 0, (int)(factor *10));
                                editText.setLayoutParams(editTextParams);
                                editText.setHint("Continue Answer Here ...");
                                editText.setHintTextColor(getResources().getColor(R.color.dark_grey));
                                editText.setBackground(null);
                                editText.requestFocus();
                                editText.setTextColor(getResources().getColor(R.color.answer_text_color));
                                answerLinearLayout.addView(editText);
                            }
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void startGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        if (galleryIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(galleryIntent, 1000);
        }
    }

    public void onAddImageClicked(View view){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
            }
        } else {
            editText = (EditText)getCurrentFocus();
            currentIndex = answerLinearLayout.indexOfChild(editText);
            imageView = new ImageView(this);
            startGallery();
        }
    }

    public void onPostButtonClicked(View view){


        int currentCount = -1;
        for(int i = 0; i < answerLinearLayout.getChildCount(); i++){
            View currentView = answerLinearLayout.getChildAt(i);

            if(currentView instanceof EditText){
                String text = ((EditText) currentView).getText().toString();
                currentCount = Helpers.nextEven(currentCount);
                answer.put("k" + Integer.toString(currentCount), text);

            }else if(currentView instanceof ImageView){

                String tag = (String) currentView.getTag();
                currentCount = Helpers.nextOdd(currentCount);
                answer.put("k" + Integer.toString(currentCount), tag);

            }
        }

        String date = Calendar.getInstance().getTime().toString();

        ModelAnswer modelAnswer = new ModelAnswer(SplashActivity.userInfo.getUserID(), questionID, answer, date);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Answers");
        final String answerID = ref.push().getKey();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting Answer ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ref.child(answerID).setValue(modelAnswer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(SplashActivity.userInfo.getUserID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                                            ArrayList<String> answersArrayList = modelUser.getAnswersArrayList();
                                            try{
                                                answersArrayList.add(answerID);
                                            }catch(NullPointerException e){
                                                answersArrayList = new ArrayList<>();
                                                answersArrayList.add(answerID);
                                            }
                                            modelUser.setAnswerCount(modelUser.getAnswerCount() + 1);
                                            modelUser.setAnswersArrayList(answersArrayList);
                                            FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(SplashActivity.userInfo.getUserID())
                                                    .setValue(modelUser)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(AnswerQuestionActivity.this, "Answer Posted Successfully", Toast.LENGTH_SHORT).show();
                                                                AnswerQuestionActivity.this.finish();
                                                                FirebaseDatabase.getInstance().getReference("questions")
                                                                        .child(questionID)
                                                                        .child("answers")
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                int answersCount = dataSnapshot.getValue(Integer.class);
                                                                                dataSnapshot.getRef().setValue(answersCount + 1);
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(AnswerQuestionActivity.this, "Failed to post Answer", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(AnswerQuestionActivity.this, "Failed to post Answer", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static Bitmap checkForRoatatedImage(Context context, Uri selectedImage) throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();


        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);


        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIR(context, img, selectedImage);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);


            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            final float totalPixels = width * height;


            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIR(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    void saveAsDraft()
    {
        int currentCount = -1;
        for(int i = 0; i < answerLinearLayout.getChildCount(); i++){
            View currentView = answerLinearLayout.getChildAt(i);

            if(currentView instanceof EditText){
                String text = ((EditText) currentView).getText().toString();
                currentCount = Helpers.nextEven(currentCount);
                draft.put("k" + Integer.toString(currentCount), text);

            }else if(currentView instanceof ImageView){

                String tag = (String) currentView.getTag();
                currentCount = Helpers.nextOdd(currentCount);
                draft.put("k" + Integer.toString(currentCount), tag);

            }
        }


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Drafts");
        final String draftID = ref.push().getKey();
        ModelDraft modelDraft = new ModelDraft(SplashActivity.userInfo.getUserID(), questionID,draftID, draft);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Draft");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ref.child(draftID).setValue(modelDraft)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(SplashActivity.userInfo.getUserID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                                            ArrayList<String> draftsArrayList = modelUser.getDraftsArrayList();
                                            try{
                                                draftsArrayList.add(draftID);
                                            }catch(NullPointerException e){
                                                draftsArrayList = new ArrayList<>();
                                                draftsArrayList.add(draftID);
                                            }
                                            modelUser.setDraftCount(modelUser.getDraftCount() + 1);
                                            modelUser.setDraftsArrayList(draftsArrayList);
                                            FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(SplashActivity.userInfo.getUserID())
                                                    .setValue(modelUser)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                try {
                                                                    progressDialog.dismiss();
                                                                }catch (IllegalArgumentException e){ }

                                                                Toast.makeText(AnswerQuestionActivity.this, "Draft Saved Successfully", Toast.LENGTH_SHORT).show();
                                                                AnswerQuestionActivity.this.finish();
                                                                FirebaseDatabase.getInstance().getReference("Users")
                                                                        .child(userID)
                                                                        .child("draftCount")
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                int draftsCount = dataSnapshot.getValue(Integer.class);
                                                                                dataSnapshot.getRef().setValue(draftsCount + 1);
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(AnswerQuestionActivity.this, "Failed to save Draft", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(AnswerQuestionActivity.this, "Failed to Save Draft", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    void saveDraftConfirm()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(AnswerQuestionActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure,you want to leave?");

        builder.setPositiveButton("Save As Draft", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                saveAsDraft();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Clear", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            progressDialog.dismiss();
        }catch (Exception e){ }
    }
}
