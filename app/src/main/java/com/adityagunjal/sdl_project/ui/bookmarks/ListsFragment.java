package com.adityagunjal.sdl_project.ui.bookmarks;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListsFragment extends androidx.fragment.app.ListFragment {

    onItemSelected activity;

    public interface onItemSelected
    {
        void onItemSelected(int index);
    }

    public ListsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (onItemSelected)context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<String> data = new ArrayList<>();
        data.add("Question 1");
        data.add("Question 2");
        data.add("Question 3");

        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,data));
        activity.onItemSelected(0);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        activity.onItemSelected(position);
    }
}