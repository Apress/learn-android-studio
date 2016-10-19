package com.apress.gerber.simplelayouts;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Clifton
 * Copyright 8/22/2014.
 */
public class BuddyListFragment extends ListFragment {

    private OnListItemSelectedListener onListItemSelectedListener;

    public interface OnListItemSelectedListener {
        void onListItemSelected(Person selectedPerson);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Person[] listItems = new Person[]{
                new Person(R.drawable.mary, "Mary",
                        "www.allmybuddies.com/mary",
                        "New York","Avid cook, writes poetry."),
                new Person(R.drawable.joseph, "Joseph",
                        "www.allmybuddies.com/joeseph",
                        "Virginia","Author of several novels"),
                new Person(R.drawable.leah, "Leah",
                        "www.allmybuddies.com/leah",
                        "North Carolina",
                        "Basketball superstar. Rock climber."),
                new Person(R.drawable.mark,"Mark",
                        "www.allmybuddies.com/mark",
                        "Denver",
                        "Established chemical scientist with several patents.")
        };
        setListAdapter(new PersonAdapter(getActivity(),
                        android.R.layout.simple_expandable_list_item_2,
                        listItems)
        );
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(!(activity instanceof OnListItemSelectedListener)) {
            throw new ClassCastException("Activity should implement OnListItemSelectedListener");
        }
        //Save the attached activity as an onListItemSelectedListener
        this.onListItemSelectedListener = (OnListItemSelectedListener) activity;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Person selectedPerson = (Person) l.getItemAtPosition(position);
        this.onListItemSelectedListener.onListItemSelected(selectedPerson);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, container, false);
    }
}
