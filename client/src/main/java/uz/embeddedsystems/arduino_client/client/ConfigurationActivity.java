package uz.embeddedsystems.arduino_client.client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
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
    @Bind(R.id.txt_actual_temp)
    TextView txtActualTemp;
    @Bind(R.id.txt_set_temp)
    EditText txtSetTemp;
    @Bind(R.id.btn_send)
    Button btnSend;
    private String blindsBeforeChange;
    private String tempBeforeChange;
    private String tempAfterChange;
    private ProgressDialog sendingDialog;
    private ArduinoConnection connection;

    @OnClick(R.id.btn_send)
    public void onButtonSendClicked() {
        sendingDialog = ProgressDialog.show(ConfigurationActivity.this, "Setting configuration", "Please wait ...", true);
        tempBeforeChange = txtActualTemp.getText().toString();
        tempAfterChange = txtSetTemp.getText().toString();
        setTemp(tempAfterChange);
        connection.setConfiguration("OZO", tempAfterChange);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        ButterKnife.bind(this);
        connection = ArduinoConnection.getInstance();
        setCallbacks();
        final String response = getIntent().getExtras().getString(ConnectFragment.CONFIGURATION);
        Log.e("CONFACT", "onCreate: " + response);
        final String switchesConf = response.substring(0, 3);
        blindsBeforeChange = switchesConf;
        setSwitches(switchesConf);
        setTemp(response.substring(response.indexOf(",") + 1, response.length()));
    }


    private void showMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setSwitches(final String bindsStates) {
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

    private void setCallbacks() {
        connection.setServerBusyCallback(new ArduinoConnection.Callback() {
            @Override
            public void execute(final String message) {
                Toast.makeText(ConfigurationActivity.this, "Server is busy! Try again later.", Toast.LENGTH_SHORT).show();
                setSwitches(blindsBeforeChange);
                setTemp(tempBeforeChange);
                sendingDialog.dismiss();
            }
        });

        connection.setExceptionCallback(new ArduinoConnection.Callback() {
            @Override
            public void execute(final String message) {
                showMessage(message);
            }
        });

        connection.setSuccessfulSetCallback(new ArduinoConnection.Callback() {
            @Override
            public void execute(final String message) {
                if (message.equals("OK")) {
                    blindsBeforeChange = getBindsStates();
                    Toast.makeText(ConfigurationActivity.this, "Settings successfully set !", Toast.LENGTH_SHORT).show();
                }
                sendingDialog.dismiss();
            }
        });
    }

    private String getBindsStates() {
        StringBuilder builder = new StringBuilder(3);
        builder.append(getState(switchBlind1))
                .append(getState(switchBlind2))
                .append(getState(switchBlind3));
        return builder.toString();
    }

    private String getState(final Switch switchBlind) {
        return (switchBlind.isChecked() ? "O" : "Z");
    }

    private void setTemp(final String temp) {
        txtActualTemp.setText(temp);
    }
}
