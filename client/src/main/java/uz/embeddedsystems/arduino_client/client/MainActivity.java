package uz.embeddedsystems.arduino_client.client;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.net.UnknownHostException;

public class MainActivity extends Activity {
    public final static String IP_ADDRESS = "ip_address";
    public final static String PORT = "port";
    @Bind(R.id.btn_connect)
    Button btnConnect;
    @Bind(R.id.txt_ip_address)
    EditText txtIpAddress;
    @Bind(R.id.txt_port)
    EditText txtPort;
    private Intent nextActivity;
    private boolean ipInserted = false;
    private boolean portInserted = false;

    @OnClick(R.id.btn_connect)
    public void onButtonConnectClicked() {
        nextActivity = new Intent(this, ConfigurationActivity.class);
        final String ip_address = txtIpAddress.getText().toString();
        final String port = txtPort.getText().toString();

        if (NetworkUtils.isWifiConnected(this)) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        ArduinoConnection.getInstance().connect(ip_address, port);

                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }.start();
            startActivity(nextActivity);
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
        NetworkUtils.isWifiConnected(this);
    }

    private void checkIfButtonCanChangeState() {
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
                if (editable.length() >= 7) {
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
                if (editable.length() == 4) {
                    portInserted = true;
                    checkIfButtonCanChangeState();
                } else {
                    portInserted = false;
                    checkIfButtonCanChangeState();
                }
            }
        });
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
            return NetworkUtils.isWifiConnected(this);
        }

        return super.onOptionsItemSelected(item);
    }

}
