package uz.embeddedsystems.arduino_client.client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.http.GET;


public class ConfigurationActivity extends Activity {

    @Bind(R.id.switch_blind1)
    Switch switchBlind1;
    @Bind(R.id.switch_blind2)
    Switch switchBlind2;
    @Bind(R.id.switch_blind3)
    Switch switchBlind3;
    @Bind(R.id.btn_send)
    Button btnSend;

    private final String generalAddress = "http://192.168.1.2:8080/dupa";

    private ProgressDialog sendingDialog;
    private ArduinoConnection connection;
    String serverResponse;

    @OnClick(R.id.btn_send)
    public void onButtonSendClicked() {
        connection.setBindState(1, true);
        connection.setBindState(2, false);
        connection.setBindState(3, true);
        System.out.println("sfusdfosojfs");
        Log.e("onButtonSendClicked:", connection.getBindsStates());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        ButterKnife.bind(this);
//        sendingDialog = ProgressDialog.show(ConfigurationActivity.this, "Fetching configuration", "Please wait ...", true);
        fetchServer(generalAddress);
//        sendingDialog.dismiss();
//        connection = ArduinoConnection.getInstance();
//        Log.e("States: ", connection.getBindsStates());

//        setSwitches(connection.getBindsStates());
//        setListeners();
    }

    private void fetchServer(final String website) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final URI uri = new URI(website);
                    final HttpClient httpClient = new DefaultHttpClient();
                    final HttpGet getRequest = new HttpGet();
                    getRequest.setURI(uri);
                    final HttpResponse httpResponse = httpClient.execute(getRequest);
                    final InputStream content = httpResponse.getEntity().getContent();
                    final BufferedReader in = new BufferedReader(new InputStreamReader(content));
                    serverResponse = in.readLine();
                    Log.e("Odp od serwera: ", serverResponse);
                    showMessage(serverResponse);
                    content.close();
                } catch (ClientProtocolException e) {
                    // HTTP error
                    showMessage("HTTP error occurred !");
                    e.printStackTrace();
                } catch (IOException e) {
                    // IO error
                    showMessage("IO exception occurred !");
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    showMessage("URI error occurred !");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        connection.disconnect();
    }

    private void setSwitches(String bindsStates) {
        char[] states = bindsStates.toCharArray();
        setSwitch(switchBlind1, states[0]);
        setSwitch(switchBlind2, states[1]);
        setSwitch(switchBlind3, states[2]);
    }

    private void setSwitch(Switch switchBlind, char state) {
        if (state == 'O')
            switchBlind.setChecked(true);
        else
            switchBlind.setChecked(false);
    }

    private void setListeners() {
        switchBlind1.setOnCheckedChangeListener(new MyChangeListener(1));
        switchBlind2.setOnCheckedChangeListener(new MyChangeListener(2));
        switchBlind3.setOnCheckedChangeListener(new MyChangeListener(3));
    }

//    private void setCallbacks() {
//        connection.setDisconnectedCallback(new ArduinoConnection.Callback() {
//            @Override
//            public void execute() {
//                Toast.makeText(ConfigurationActivity.this, "Disconnected from server!", Toast.LENGTH_LONG).show();
//                onBackPressed();
//            }
//        });
//
//        connection.setStateSetCallback(new ArduinoConnection.Callback() {
//            @Override
//            public void execute() {
//                if (sendingDialog != null)
//                    sendingDialog.dismiss();
//            }
//        });
//
//        connection.setStateNotSetCallback(new ArduinoConnection.Callback() {
//            @Override
//            public void execute() {
//                sendingDialog.setMessage("Problem with setting binds!");
//                connection.disconnect();
//            }
//        });
//    }

    class MyChangeListener implements CompoundButton.OnCheckedChangeListener {

        private final int bindID;

        MyChangeListener(int bindID) {
            this.bindID = bindID;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            sendingDialog = ProgressDialog.show(ConfigurationActivity.this, "Setting bind...", "Please wait ...", true);
            connection.setBindState(bindID, b);
        }
    }
}
