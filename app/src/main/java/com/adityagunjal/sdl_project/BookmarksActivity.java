package com.adityagunjal.sdl_project;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.adityagunjal.sdl_project.ui.bookmarks.ListsFragment;

import java.util.ArrayList;

public class BookmarksActivity extends AppCompatActivity implements ListsFragment.onItemSelected{

    TextView tvDescription;
    ArrayList<String> answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        tvDescription = findViewById(R.id.tvDescription);
        answer = new ArrayList<>();
        answer.add("Answer 1");
        answer.add("Answer 2");
        answer.add("Answer 3");
    }

    @Override
    public void onItemSelected(int index) {
        tvDescription.setText(answer.get(index));
    }
}
