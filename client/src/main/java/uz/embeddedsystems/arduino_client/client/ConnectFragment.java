package uz.embeddedsystems.arduino_client.client;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnectFragment extends Fragment {
    private static final String TAG = "ConnectFragment";
    public static final String CONFIGURATION = "configuration";
    @Bind(R.id.btn_connect)
    Button btnConnect;
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
        connection.setSuccessfulSetCallback(new ArduinoConnection.Callback() {
            @Override
            public void execute(final String message) {
                connectingDialog.dismiss();
                if (message.contains("BUSY")) {
                    Toast.makeText(getActivity(), "The server is currently bussy, try again later", Toast.LENGTH_SHORT).show();
                }
                nextActivity.putExtra(CONFIGURATION, message);
                if (nextActivity != null)
                    startActivity(nextActivity);
            }
        });
        connection.setExceptionCallback(new ArduinoConnection.Callback() {
            @Override
            public void execute(final String message) {
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

    public class GetExample {

        OkHttpClient client = new OkHttpClient();

        String run(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();


            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }
}
