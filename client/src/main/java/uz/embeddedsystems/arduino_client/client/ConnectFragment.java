package uz.embeddedsystems.arduino_client.client;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnectFragment extends Fragment {
    private static final String TAG = "ConnectFragment";
    public static final String CONFIGURATION = "configuration";
    public static final String SHARED_KEY = "sh";
    @Bind(R.id.spinner_ip)
    Spinner spinnerIp;
    @Bind(R.id.spinner_port)
    Spinner spinnerPort;
    private Intent nextActivity;
    private ArduinoConnection connection;
    private ProgressDialog connectingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        connection = ArduinoConnection.getInstance();
        setCallbacks();
    }

    private void setCallbacks() {
        connection.setSuccessfulFetchCallback(new ArduinoConnection.Callback() {
            @Override
            public void execute(final String message) {
                connectingDialog.dismiss();
                nextActivity.putExtra(CONFIGURATION, message);
                if (nextActivity != null)
                    startActivity(nextActivity);
            }
        });

        connection.setExceptionCallback(new ArduinoConnection.Callback() {
            @Override
            public void execute(final String message) {
                connectingDialog.dismiss();
                Log.e(TAG, "Exception: "+ message );
                Toast.makeText(getActivity(), "Exception! Message "+ message, Toast.LENGTH_SHORT).show();
            }
        });
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
        final SharedPreferences preferences = getActivity().getSharedPreferences(SHARED_KEY, Context.MODE_PRIVATE);
        final Set<String> ipSet = SharedPreferencesUtils.getSavedPair(preferences).first;
        final Set<String> portSet = SharedPreferencesUtils.getSavedPair(preferences).second;
        setupSpinners(ipSet, portSet);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick(R.id.btn_connect)
    public void onButtonConnectClicked() {
        nextActivity = new Intent(getActivity(), ConfigurationActivity.class);
        final String ip_address = (String) spinnerIp.getSelectedItem();
        final String port = (String) spinnerPort.getSelectedItem();

        if (NetworkUtils.isWifiConnected(getActivity())) {
            connectingDialog = ProgressDialog.show(getActivity(), "Fetching Configuration", "Please wait ...", true);
            connection.fetchConfiguration(ip_address, port);
        } else {
            Toast.makeText(getActivity(), "No connection!", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupSpinners(final Set<String> ipCollection, final Set<String> portCollection) {
        final ArrayAdapter<String> ipSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.single_item_spinner);
        ipSpinnerAdapter.addAll(ipCollection);
        spinnerIp.setAdapter(ipSpinnerAdapter);
        spinnerIp.setSelection(ipCollection.size() - ipCollection.size() - 1);

        final ArrayAdapter<String> portSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.single_item_spinner);
        portSpinnerAdapter.addAll(portCollection);
        spinnerPort.setAdapter(portSpinnerAdapter);
        spinnerPort.setSelection(portCollection.size() - portCollection.size() - 1);
    }

}
