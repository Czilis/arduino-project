package uz.embeddedsystems.arduino_client.client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Patryk on 26.11.2015.
 */
public class ConfigurationActivity extends Activity {

    @Bind(R.id.switch_blind1)
    Switch switchBlind1;
    @Bind(R.id.switch_blind2)
    Switch switchBlind2;
    @Bind(R.id.switch_blind3)
    Switch switchBlind3;

    private ProgressDialog sendingDialog;
    private ArduinoConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        ButterKnife.bind(this);

        connection = ArduinoConnection.getInstance();
        setCallbacks();
        setSwitches(connection.getBindsStates());

        setListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        connection.disconnect();
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

    private void setCallbacks() {
        connection.setDisconnectedCallback(new ArduinoConnection.Callback() {
            @Override
            public void execute() {
                Toast.makeText(ConfigurationActivity.this, "Disconnected from server!", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });

        connection.setStateSetCallback(new ArduinoConnection.Callback() {
            @Override
            public void execute() {
                if (sendingDialog != null)
                    sendingDialog.dismiss();
            }
        });

        connection.setStateNotSetCallback(new ArduinoConnection.Callback() {
            @Override
            public void execute() {
                sendingDialog.setMessage("Problem with setting binds!");
                connection.disconnect();
            }
        });
    }

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
