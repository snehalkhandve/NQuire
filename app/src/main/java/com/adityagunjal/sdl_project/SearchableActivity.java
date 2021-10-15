package com.adityagunjal.sdl_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.SearchView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.adapters.AdapterSearchQuestion;
import com.adityagunjal.sdl_project.adapters.AdapterSearchUser;
import com.adityagunjal.sdl_project.models.ModelQuestion;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<String> questionList;
    ArrayList<String> userNameList;
    ArrayList<String> fullNameList;
    ArrayList<ModelQuestion> modelQuestionArrayList;
    ArrayList<ModelUser> modelUserArrayList;

    AdapterSearchUser searchAdapterUser;
    AdapterSearchQuestion searchAdapterQuestion;
    SearchView searchView;
    CardView questionsCard,usersCard;
    int mainFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_searchable);

        searchView = findViewById(R.id.search_toolbar);
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(id);
        textView.setTextColor(getResources().getColor(R.color.black));
        questionsCard = findViewById(R.id.question_card_filter);
        usersCard = findViewById(R.id.user_card_filter);

        recyclerView =  findViewById(R.id.recyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        questionList = new ArrayList<>();
        userNameList = new ArrayList<>();
        fullNameList = new ArrayList<>();
        modelQuestionArrayList = new ArrayList<>();
        modelUserArrayList = new ArrayList<>();

        searchAdapterQuestion = new AdapterSearchQuestion(SearchableActivity.this, modelQuestionArrayList);
        recyclerView.setAdapter(searchAdapterQuestion);

        searchAdapterUser = new AdapterSearchUser(SearchableActivity.this, modelUserArrayList);

        final float factor1 = usersCard.getContext().getResources().getDisplayMetrics().density;
        final float factor2 = questionsCard.getContext().getResources().getDisplayMetrics().density;

        usersCard.setCardBackgroundColor(getResources().getColor((R.color.white)));
        questionsCard.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        questionsCard.setCardElevation(20 * factor2);
        ((TextView) questionsCard.findViewById(R.id.question_text)).setTextColor(getResources().getColor(R.color.white));
        usersCard.setCardElevation(0);
        mainFlag = 1;

        questionsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersCard.setCardBackgroundColor(getResources().getColor((R.color.white)));
                questionsCard.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                ((TextView) questionsCard.findViewById(R.id.question_text)).setTextColor(getResources().getColor(R.color.white));
                ((TextView) usersCard.findViewById(R.id.user_text)).setTextColor(getResources().getColor(R.color.black));
                questionsCard.setCardElevation(20 * factor2);
                usersCard.setCardElevation(0);
                mainFlag = 1;
                fullNameList.clear();
                userNameList.clear();
                searchAdapterUser.removeAll();
            }
        });

        usersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionsCard.setCardBackgroundColor(getResources().getColor((R.color.white)));
                usersCard.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                ((TextView) questionsCard.findViewById(R.id.question_text)).setTextColor(getResources().getColor(R.color.black));
                ((TextView) usersCard.findViewById(R.id.user_text)).setTextColor(getResources().getColor(R.color.white));
                usersCard.setCardElevation(20 * factor1);
                questionsCard.setCardElevation(0);
                mainFlag = 0;
                questionList.clear();
                searchAdapterQuestion.removeAll();
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s ) {
                if(mainFlag == 0) {
                    if (!s.isEmpty()) {
                        setAdapter(s);
                    }
                }
                else if(mainFlag == 1)
                {
                    if (!s.isEmpty()) {
                        setAdapterQuestions(s);
                    }
                }

                return true;
            }
        });

    }

    private void setAdapter(final String searchedString) {

        recyclerView.setAdapter(searchAdapterUser);
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        fullNameList.clear();
                        userNameList.clear();
                        searchAdapterUser.removeAll();

                        int counter = 0;


                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String uid = snapshot.getKey();
                            ModelUser modelUser = snapshot.getValue(ModelUser.class);
                            modelUser.setUserID(uid);

                            String fullName;
                            String firstName = modelUser.getFirstName();
                            String lastName = modelUser.getLastName();
                            String user_name = modelUser.getUsername();

                            if (user_name.toLowerCase().contains(searchedString.toLowerCase())) {
                                fullName = firstName +" "+ lastName;
                                userNameList.add(user_name);
                                fullNameList.add(fullName);
                                searchAdapterUser.addNewItem(modelUser);

                                counter++;
                            } else if (firstName.toLowerCase().contains(searchedString.toLowerCase())) {
                                String lastName1 = modelUser.getLastName();
                                fullName = firstName +" "+ lastName1;
                                userNameList.add(user_name);
                                fullNameList.add(fullName);
                                searchAdapterUser.addNewItem(modelUser);

                                counter++;
                            } else if(lastName.toLowerCase().contains(searchedString.toLowerCase())) {
                                String firstName1 = modelUser.getFirstName();
                                fullName = firstName1 +" "+ lastName;
                                userNameList.add(user_name);
                                fullNameList.add(fullName);
                                searchAdapterUser.addNewItem(modelUser);
                                counter++;
                            }

                            if (counter == 15)
                                break;
                        }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void setAdapterQuestions(final String searchedString)
    {
        recyclerView.setAdapter(searchAdapterQuestion);
        databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                questionList.clear();
                searchAdapterQuestion.removeAll();
                recyclerView.removeAllViews();

                int counter = 0;


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String qID = snapshot.getKey();
                    ModelQuestion modelQuestion = snapshot.getValue(ModelQuestion.class);
                    modelQuestion.setqID(qID);

                    String qtext = modelQuestion.getText();

                    if (qtext.toLowerCase().contains(searchedString.toLowerCase())) {
                        questionList.add(qtext);
                        searchAdapterQuestion.addNewItem(modelQuestion);
                        counter++;
                    }


                    if (counter == 15)
                        break;
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
