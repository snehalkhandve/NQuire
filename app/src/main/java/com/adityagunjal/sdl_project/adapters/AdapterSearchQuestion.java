package com.adityagunjal.sdl_project.adapters;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.SearchableActivity;
import com.adityagunjal.sdl_project.ShowAnswerActivity;
import com.adityagunjal.sdl_project.models.ModelQuestion;

import java.util.ArrayList;

public class AdapterSearchQuestion extends RecyclerView.Adapter<AdapterSearchQuestion.QuestionSearchViewHolder> {

    Context context;
    ArrayList<ModelQuestion> modelQuestionArrayList;


    class QuestionSearchViewHolder extends RecyclerView.ViewHolder {

        TextView question;

        public QuestionSearchViewHolder(View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.question_list_text);
        }
    }



    public AdapterSearchQuestion(Context context, ArrayList<ModelQuestion> modelQuestionArrayList)
    {
        this.context = context;
        this.modelQuestionArrayList = modelQuestionArrayList;
    }

    @Override
    public QuestionSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;


        view = LayoutInflater.from(context).inflate(R.layout.search_list_questions, parent, false);
        return new QuestionSearchViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final QuestionSearchViewHolder holder, final int position) {
         holder.question.setText(modelQuestionArrayList.get(position).getText());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, ShowAnswerActivity.class);
                i.putExtra("EXTRA_QUESTION", modelQuestionArrayList.get(position));
                i.putExtra("EXTRA_FLAG", "ALL");

                context.startActivity(i);
                ((SearchableActivity) context).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelQuestionArrayList.size();
    }

    public void addNewItem( ModelQuestion modelQuestion){
        modelQuestionArrayList.add(modelQuestion);
        notifyDataSetChanged();
    }

    public void removeAll(){
        modelQuestionArrayList.clear();
        notifyDataSetChanged();
    }
}
