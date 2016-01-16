package uz.embeddedsystems.arduino_client.client;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

import butterknife.ButterKnife;


public class MainActivity extends Activity implements ManageAddressesFragment.Listener {
    ManageAddressesFragment fragmentManageAddresses;
    ConnectFragment fragmetnConnect;
    final FragmentManager fragmentManager = getFragmentManager();

    @Override
    public void onAddressAdded() {
        showProperFragment();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_main);
        fragmentManageAddresses = new ManageAddressesFragment();
        fragmetnConnect = new ConnectFragment();
        fragmentManageAddresses.setListener(this);
        showProperFragment();


    }

    private void showProperFragment() {
        if (isAnyPairStored()) {
            setActiveFragmentTo(fragmetnConnect);
        } else {
            setActiveFragmentTo(fragmentManageAddresses);
        }
    }


    private void setActiveFragmentTo(final Fragment fragment) {
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layout_fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private boolean isAnyPairStored() {
        final Pair<Set, Set> savedPair = SharedPreferencesUtils.getSavedPair(this);
        return !savedPair.first.isEmpty() && !savedPair.second.isEmpty();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.check_wifi_connection) {
            if (NetworkUtils.isWifiConnected(this)) {
                Toast.makeText(this, "You have WiFi connection", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You don't have WiFi connection", Toast.LENGTH_SHORT).show();
            }
            return NetworkUtils.isWifiConnected(this);
        }

        if (id == R.id.add_another_address) {
            setActiveFragmentTo(fragmentManageAddresses);
        }

        return super.onOptionsItemSelected(item);
    }
}


