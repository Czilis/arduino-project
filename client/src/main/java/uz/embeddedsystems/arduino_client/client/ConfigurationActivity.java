package uz.embeddedsystems.arduino_client.client;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Switch;



import butterknife.Bind;

/**
 * Created by Patryk on 26.11.2015.
 */
public class ConfigurationActivity extends Activity {

    @Bind(R.id.switch_blind1)
    Switch switchBlind1;
    @Bind(R.id.switch_blind2)
    Switch switchBlind2;
    @Bind(R.id.switch_blind3)
    Switch swithcBlind3;

    private String ipAddress;
    private String port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        Bundle extras = getIntent().getExtras();
        ipAddress = extras.get(MainActivity.IP_ADDRESS).toString();
        port = extras.get(MainActivity.PORT).toString();

    }


}
