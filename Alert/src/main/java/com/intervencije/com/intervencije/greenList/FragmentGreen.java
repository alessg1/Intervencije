package com.intervencije.com.intervencije.greenList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.intervencije.MainActivity;
import com.intervencije.R;

public class FragmentGreen extends Fragment {

    ListView greenListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        //This layout contains your list view
        View view = inflater.inflate(R.layout.fragment_green, container, false);


        greenListView = (ListView) view.findViewById(R.id.greenlistView);
        greenListView.setAdapter(MainActivity.greenListAdapter);


        return view;
    }
}