package uz.embeddedsystems.arduino_client.client;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;


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

    private String ipAddress;
    private String port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        ButterKnife.bind(this);

        while (ArduinoConnection.getInstance().getBindsStates().equals("")) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        setSwitches(ArduinoConnection.getInstance().getBindsStates());

        setListeners();

    }

    private void setSwitches(String bindsStates) {
        char[] arr = bindsStates.toCharArray();
        if (arr[0] == 'O')
            switchBlind1.setChecked(true);
        else
            switchBlind1.setChecked(false);
        if (arr[1] == 'O')
            switchBlind2.setChecked(true);
        else
            switchBlind2.setChecked(false);
        if (arr[2] == 'O')
            switchBlind3.setChecked(true);
        else
            switchBlind3.setChecked(false);
    }

    private void setListeners() {
        CompoundButton.OnCheckedChangeListener listener = new MyChangeListener();
        switchBlind1.setOnCheckedChangeListener(listener);
        switchBlind2.setOnCheckedChangeListener(listener);
        switchBlind3.setOnCheckedChangeListener(listener);
    }

    class MyChangeListener implements CompoundButton.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int bindID = 0;

            if (compoundButton.getId() == R.id.switch_blind1) {
                bindID = 1;
            } else if (compoundButton.getId() == R.id.switch_blind2) {
                bindID = 2;
            } else bindID = 3;
            ArduinoConnection.getInstance().setBindState(bindID, b);
        }
    }
}
