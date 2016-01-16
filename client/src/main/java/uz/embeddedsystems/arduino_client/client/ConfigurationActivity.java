package uz.embeddedsystems.arduino_client.client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


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
//        Log.e("onButtonSendClicked:", connection.getBindsStates());
//        sendingDialog = ProgressDialog.show(ConfigurationActivity.this, "Fetching configuration", "Please wait ...", true);
//        connection.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        ButterKnife.bind(this);
        connection = ArduinoConnection.getInstance();
        setCallback();

//        fetchServer(generalAddress);
//        sendingDialog.dismiss();
//        Log.e("States: ", connection.getBindsStates());

//        setSwitches(connection.getBindsStates());
    }


    private void showMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

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

    private void setCallback() {
        connection.setExceptionCallback(new ArduinoConnection.Callback() {
            @Override
            public void execute() {
//                not used
            }

            @Override
            public void execute(final String message) {
                showMessage(message);
            }
        });

        connection.setConnectedCallback(new ArduinoConnection.Callback() {
            @Override
            public void execute() {
                Toast.makeText(ConfigurationActivity.this, "Connected !", Toast.LENGTH_SHORT).show();
                sendingDialog.dismiss();
            }

            @Override
            public void execute(final String message) {
//not used
            }
        });
    }

}
