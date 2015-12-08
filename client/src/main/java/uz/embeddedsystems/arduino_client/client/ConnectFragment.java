package uz.embeddedsystems.arduino_client.client;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnectFragment extends Fragment {
    public static final String IP_ADDRESS = "ip";
    public static final String PORT = "port";
    @Bind(R.id.btn_connect)
    Button btnConnect;
    @Bind(R.id.spinner_ip)
    Spinner spinnerIp;
    @Bind(R.id.spinner_port)
    Spinner spinnerPort;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_connect, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Set<String> ipSet = SharedPreferencesUtils.getSavedPair(getActivity()).first;
        final Set<String> portSet = SharedPreferencesUtils.getSavedPair(getActivity()).second;
        setupSpinners(ipSet, portSet);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick(R.id.btn_connect)
    public void onButtonConnectClicked() {
        final Intent intent = new Intent(getActivity(), ConfigurationActivity.class);
        intent.putExtra(IP_ADDRESS, (String) spinnerIp.getSelectedItem());
        intent.putExtra(PORT, (String) spinnerPort.getSelectedItem());
        if (isWifiConnected()) {
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "No connection!", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupSpinners(final Set<String> ipCollection, final Set<String> portCollection) {
        final ArrayAdapter<String> ipSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.single_item_spinner);
        ipSpinnerAdapter.addAll(ipCollection);
        spinnerIp.setAdapter(ipSpinnerAdapter);
        spinnerIp.setSelection(ipCollection.size()- ipCollection.size()-1);

        final ArrayAdapter<String> portSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.single_item_spinner);
        portSpinnerAdapter.addAll(portCollection);
        spinnerPort.setAdapter(portSpinnerAdapter);
        spinnerPort.setSelection(portCollection.size() - portCollection.size()-1);
    }
    private boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


}
