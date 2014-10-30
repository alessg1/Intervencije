package com.intervencije.com.intervencije.sms;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.intervencije.MainActivity;
import com.intervencije.R;

/**
 * Created by Nejc on 5.11.2013.
 */
public class FragmentSms extends Fragment implements AdapterView.OnItemClickListener, ActionMode.Callback {

    ListView smsListView;

    private ActionMode mActionMode;

    public int selectedItem = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //This layout contains your list view
        View view = inflater.inflate(R.layout.fragment_sms, container, false);

        smsListView = (ListView) view.findViewById(R.id.smsListView);
        smsListView.setAdapter(MainActivity.smsListAdapter);

        smsListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete:
                MainActivity.smsListAdapter.deleteItem(selectedItem);
                actionMode.finish();
                return true;
            case R.id.action_deleteAll:
                MainActivity.smsListAdapter.deleteAllItems();
                actionMode.finish();
                return true;
            default:
                actionMode.finish();
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(this);
        smsListView.setSelected(true);
        selectedItem = position;
    }
}