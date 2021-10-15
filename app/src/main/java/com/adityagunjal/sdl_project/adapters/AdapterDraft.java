package com.adityagunjal.sdl_project.adapters;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.AnswerQuestionActivity;
import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.ShowAnswerActivity;
import com.adityagunjal.sdl_project.helpers.UserInfo;
import com.adityagunjal.sdl_project.models.ModelDraft;
import com.adityagunjal.sdl_project.models.ModelFeed;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdapterDraft extends RecyclerView.Adapter<AdapterDraft.MyViewHolder>{

    String qText = "a";

    ModelUser user;
    String draftId ;
    Context context;
    ArrayList<ModelDraft> modelDraftArrayList;
    DatabaseReference databaseReference;

    public AdapterDraft(Context context, ArrayList<ModelDraft> modelDraftArrayList){
        this.context = context;
        this.modelDraftArrayList = modelDraftArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.draft_card, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ModelDraft modelDraft = modelDraftArrayList.get(position);
       final String uID  = modelDraft.getUserID();
       final String qID = modelDraft.getQuestionID();
       final String dID = modelDraft.getDraftID();
       final HashMap<String,String> draft = modelDraft.getDraft();
      // Toast.makeText(context, "d"+dID + "u "+uID+"q"+ qID, Toast.LENGTH_SHORT).show();
        draftId = modelDraft.getDraftID();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("questions/"+qID+"/text");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                qText = dataSnapshot.getValue(String.class);
                holder.question.setText(qText);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AnswerQuestionActivity.class);
                i.putExtra("EXTRA_FLAG",1);
                i.putExtra("EXTRA_DRAFT_ID", dID);
                i.putExtra("EXTRA_QUESTION_ID", qID);
                i.putExtra("EXTRA_USER_ID",uID);
                i.putExtra("EXTRA_DRAFT_ANSWER",draft);
                context.startActivity(i);
            }
        });

        holder.editDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AnswerQuestionActivity.class);
                i.putExtra("EXTRA_FLAG",1);
                i.putExtra("EXTRA_DRAFT_ID", dID);
                i.putExtra("EXTRA_QUESTION_ID", qID);
                i.putExtra("EXTRA_USER_ID",uID);
                i.putExtra("EXTRA_DRAFT_ANSWER",draft);
                context.startActivity(i);
            }

        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = FirebaseDatabase.getInstance().getReference("Drafts/"+draftId);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelDraft modelDraft = dataSnapshot.getValue(ModelDraft.class);
                        draftId = modelDraft.getDraftID();
                        FirebaseDatabase.getInstance().getReference("Drafts").child(draftId).removeValue();
                        deleteItem(modelDraft);
                        databaseReference.child("Users/"+uID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                                modelUser.draftsArrayList.remove(draftId);
                                FirebaseDatabase.getInstance().getReference("Users/"+uID).setValue(modelUser);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        //FirebaseDatabase.getInstance().getReference("Users/"+uID+"/"+"draftsArrayList/"+dID).removeValue();
                        Toast.makeText(context, "Draft Deleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });

        holder.deleteDraft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Query query = FirebaseDatabase.getInstance().getReference("Drafts/"+draftId);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ModelDraft modelDraft = dataSnapshot.getValue(ModelDraft.class);
                            draftId = modelDraft.getDraftID();
                            FirebaseDatabase.getInstance().getReference("Drafts").child(draftId).removeValue();
                            deleteItem(modelDraft);
                        databaseReference.child("Users/"+uID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                                modelUser.draftsArrayList.remove(draftId);
                                FirebaseDatabase.getInstance().getReference().setValue(modelUser);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                            notifyDataSetChanged();
                            Toast.makeText(context, "Draft Deleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelDraftArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        final Context itemViewContext;

        TextView question;
        ImageButton editDraft,deleteDraft;
        TextView edit, delete;
        public MyViewHolder(View itemView) {
            super(itemView);

            itemViewContext = itemView.getContext();

            question = (TextView) itemView.findViewById(R.id.draft_card_question);
            editDraft = itemView.findViewById(R.id.edit_draft_button);
            deleteDraft = itemView.findViewById(R.id.del_draft_button);
            edit = itemView.findViewById(R.id.textEdit);
            delete = itemView.findViewById(R.id.textDelete);

        }
    }
    public void addNewItem(ModelDraft modelDraft){
        modelDraftArrayList.add(modelDraft);
      notifyDataSetChanged();
    }

    public void deleteItem(ModelDraft modelDraft)
    {
        modelDraftArrayList.remove(modelDraft);
        notifyDataSetChanged();
    }

    public void deleteAll()
    {
        modelDraftArrayList.clear();
        notifyDataSetChanged();
    }
}
