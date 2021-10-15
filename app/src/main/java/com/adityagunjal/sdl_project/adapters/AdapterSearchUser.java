package com.adityagunjal.sdl_project.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.ProfileActivity;
import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.adityagunjal.sdl_project.ui.profile.ShowProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class AdapterSearchUser extends RecyclerView.Adapter<AdapterSearchUser.UserSearchViewHolder> {

    Context context;

    String uid;
    ModelUser user;

    ArrayList<ModelUser> modelUserArrayList;



    class UserSearchViewHolder extends RecyclerView.ViewHolder {

        TextView  user_name,full_name;
        ImageView profilePic;


        public UserSearchViewHolder(View itemView) {
            super(itemView);

            full_name =  itemView.findViewById(R.id.full_name);
            user_name =  itemView.findViewById(R.id.user_name);
            profilePic = itemView.findViewById(R.id.profileImage);

        }
    }

    public AdapterSearchUser(Context context, ArrayList<ModelUser> modelUserArrayList) {
        this.context = context;
        this.modelUserArrayList = modelUserArrayList;
    }



    @Override
    public UserSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;


             view = LayoutInflater.from(context).inflate(R.layout.search_list_items, parent, false);
             return new UserSearchViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final UserSearchViewHolder holder, int position) {

        final ModelUser modelUser = modelUserArrayList.get(position);

        holder.user_name.setText(modelUser.getUsername());
        holder.full_name.setText(modelUser.getFirstName() + " " + modelUser.getLastName());

        if(modelUser.getImagePath().equals("default")) {
            holder.profilePic.setImageResource(R.drawable.ic_profile_pic);
        } else{
            FirebaseStorage.getInstance().getReference(modelUser.getImagePath())
                    .getBytes(1024 * 1024)
                    .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                        @Override
                        public void onComplete(@NonNull Task<byte[]> task) {
                            if (task.isSuccessful()) {
                                Bitmap image = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                holder.profilePic.setImageBitmap(image);
                            } else {
                                holder.profilePic.setImageResource(R.drawable.ic_profile_pic);
                            }
                        }
                    });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("EXTRA_USER", modelUser);
                i.putExtra("EXTRA_USER_ID", modelUser.getUserID());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelUserArrayList.size();
    }

    public void addNewItem(ModelUser modelUser){
        modelUserArrayList.add(modelUser);
        notifyDataSetChanged();
    }

    public void removeAll(){
        modelUserArrayList.clear();
        notifyDataSetChanged();
    }

}



