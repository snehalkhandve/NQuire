package com.adityagunjal.sdl_project.ui.draft;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adityagunjal.sdl_project.AnswerQuestionActivity;
import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.SplashActivity;
import com.adityagunjal.sdl_project.adapters.AdapterAnswer;
import com.adityagunjal.sdl_project.adapters.AdapterDraft;
import com.adityagunjal.sdl_project.models.ModelAnswer;
import com.adityagunjal.sdl_project.models.ModelDraft;
import com.adityagunjal.sdl_project.models.ModelQuestion;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class DraftFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView;
    ArrayList<ModelDraft> modelDraftArrayList = new ArrayList<>();
    AdapterDraft adapterDraft;
    ImageButton editDraft,deleteDraft;
    Button deleteAll;
    LinearLayout answerLinearLayout;
    ModelDraft modelDraft;

    String dID,qID,uID;
    HashMap<String,String> draft =  new HashMap<>();

    TextView questionText;
    CircleImageView backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View ans = inflater.inflate(R.layout.activity_answer_question, container,false);
        answerLinearLayout = ans.findViewById(R.id.answer_linear_layout);
        View draft_card = inflater.inflate(R.layout.draft_card, container, false);

        deleteDraft = draft_card.findViewById(R.id.del_draft_button);
        deleteDraft.setOnClickListener(this);

        View view = inflater.inflate(R.layout.fragment_draft, container, false);
        deleteAll = view.findViewById(R.id.del_all_button);
        backButton = view.findViewById(R.id.back_button_icon);
        backButton.setOnClickListener(this);

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = FirebaseDatabase.getInstance().getReference("Drafts");

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                      adapterDraft.deleteAll();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        recyclerView = view.findViewById(R.id.recycler_view_drafts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        questionText = view.findViewById(R.id.draft_card_question);
        adapterDraft = new AdapterDraft(getActivity(), modelDraftArrayList);
        recyclerView.setAdapter(adapterDraft);

        populateRecyclerView();

        return  view;
    }


    public void populateRecyclerView(){

        Query query = FirebaseDatabase.getInstance().getReference("Drafts");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelDraft modelDraft = ds.getValue(ModelDraft.class);
                    modelDraft.setDraftID(ds.getKey());
                    adapterDraft.addNewItem(modelDraft);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.back_button_icon :
                getActivity().onBackPressed();
                break;
        }
    }







}
