package com.intervencije.com.intervencije.redList;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.intervencije.MainActivity;
import com.intervencije.R;

/**
 * Created by Nejc on 5.11.2013.
 */
public class FragmentRed extends Fragment {
    ListView redListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        //This layout contains your list view
        View view = inflater.inflate(R.layout.fragment_red, container, false);


        redListView = (ListView) view.findViewById(R.id.redlistView);
        redListView.setAdapter(MainActivity.redListAdapter);

        return view;
    }

}