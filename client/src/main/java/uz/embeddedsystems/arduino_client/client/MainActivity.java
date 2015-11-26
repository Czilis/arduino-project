package uz.embeddedsystems.arduino_client.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {
    public final static String IP_ADDRESS = "ip_address";
    public final static String PORT = "port";
    @Bind(R.id.btn_connect)
    Button btnConnect;
    @Bind(R.id.txt_ip_address)
    EditText txtIpAddress;
    @Bind(R.id.txt_port)
    EditText txtPort;
    private boolean ipInserted = false;
    private boolean portInserted = false;

    @OnClick(R.id.btn_connect)
    public void onButtonConnectClicked() {
        final Intent intent = new Intent(this, ConfigurationActivity.class);
        intent.putExtra(IP_ADDRESS, txtIpAddress.getText().toString());
        intent.putExtra(PORT, txtPort.getText().toString());
        if (isWifiConnected()){
            startActivity(intent);
        } else {
            Toast.makeText(this, "No connection!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setListener();
        isWifiConnected();
    }

    private void checkIfButtonCanChangeState(){
        if (ipInserted && portInserted) {
            btnConnect.setEnabled(true);
        } else {
            btnConnect.setEnabled(false);
        }
    }

    private void setListener() {
        txtIpAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 7){
                    ipInserted = true;
                    checkIfButtonCanChangeState();
                } else {
                    ipInserted = false;
                    checkIfButtonCanChangeState();
                }
            }
        });

        txtPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 4 ) {
                    portInserted = true;
                    checkIfButtonCanChangeState();
                } else {
                    portInserted = false;
                    checkIfButtonCanChangeState();
                }
            }
        });
    }

    private boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            return true;
        } else {
            return false;
        }
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
            return isWifiConnected();
        }

        return super.onOptionsItemSelected(item);
    }
}
