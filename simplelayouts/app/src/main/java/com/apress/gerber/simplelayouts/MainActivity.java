package com.apress.gerber.simplelayouts;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends FragmentActivity
        implements BuddyListFragment.OnListItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.empty_fragment_container)!=null) {
            // We should return if we're being restored from a previous state
            // to avoid overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            BuddyListFragment buddyListFragment = new BuddyListFragment();

            // Pass any Intent extras to the fragment as arguments
            buddyListFragment.setArguments(getIntent().getExtras());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.empty_fragment_container, buddyListFragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemSelected(Person selectedPerson) {
        BuddyDetailFragment buddyDetailFragment = (BuddyDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.detail_fragment);

        if (buddyDetailFragment != null) {
            buddyDetailFragment.updateDetailView(selectedPerson);

        } else {
            buddyDetailFragment = new BuddyDetailFragment();
            Bundle args = new Bundle();
            args.putInt(BuddyDetailFragment.IMAGE, selectedPerson.image);
            args.putString(BuddyDetailFragment.NAME, selectedPerson.name);
            args.putString(BuddyDetailFragment.LOCATION, selectedPerson.location);
            args.putString(BuddyDetailFragment.WEBSITE, selectedPerson.website);
            args.putString(BuddyDetailFragment.DESCRIPTION, selectedPerson.descr);
            buddyDetailFragment.setArguments(args);
            //Start a fragment transaction to record changes in the fragments.
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.empty_fragment_container, buddyDetailFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }
}
